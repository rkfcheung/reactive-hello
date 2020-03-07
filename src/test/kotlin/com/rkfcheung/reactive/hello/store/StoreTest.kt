package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.*
import com.rkfcheung.reactive.hello.AbstractTest
import com.rkfcheung.reactive.hello.model.StreamResult
import com.rkfcheung.reactive.hello.service.PlaygroundService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@ExperimentalCoroutinesApi
@FlowPreview
@InternalCoroutinesApi
internal class StoreTest : AbstractTest() {
    @Autowired
    private lateinit var playgroundService: PlaygroundService

    @Test
    fun testBuildStore() = runBlocking(testScope.coroutineContext) {
        val store = StoreBuilder.fromNonFlow<Int, JSONObject> { keyId ->
            JSONObject(playgroundService.get(keyId))
        }.scope(testScope).build()

        val jobs = mutableListOf<Job>()
        (1..8).forEach { i ->
            val json = store.get(i)
            log.info(json.toString())
            assertTrue(json.get("url").toString().contains("key=$i"))

            jobs += launch {
                store.stream(StoreRequest.cached(i, true)).filterNot { it.origin == ResponseOrigin.Fetcher }.collect { response ->
                    log.info(response.toString())
                    assertTrue(response.requireData().get("url").toString().contains("key=$i"))
                }
            }
        }
        jobs.forEach { it.cancelAndJoin() }
    }

    @Test
    fun testBuildStoreFromFlow() = runBlocking(testScope.coroutineContext) {
        val store = StoreBuilder.from<Int, StreamResult> {
            streamAsFlow(it)
        }.scope(testScope).build()

        val jobs = mutableListOf<Job>()
        (1..8).forEach { i ->
            jobs += launch {
                store.stream(StoreRequest.fresh(i)).collect { response ->
                    when (response) {
                        is StoreResponse.Data -> log.info(response.value.toString())
                        else -> log.info(response.toString())
                    }
                }
            }
        }
        jobs.forEach { it.cancelAndJoin() }
    }

    private fun streamAsFlow(n: Int) = runBlocking(testScope.coroutineContext) {
        playgroundService.stream(n)
    }
}