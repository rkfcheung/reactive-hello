package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.*
import com.rkfcheung.reactive.hello.AbstractTest
import com.rkfcheung.reactive.hello.model.StreamResult
import com.rkfcheung.reactive.hello.service.PlaygroundService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
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
            playgroundService.get(keyId)
        }.scope(testScope).build()

        val jobs = mutableListOf<Job>()
        (1..8).forEach { i ->
            val json = store.get(i)
            log.info(json.toString())
            assertTrue(json.getString("url").contains("key=$i"))

            jobs += launch(Dispatchers.Unconfined) {
                store.stream(StoreRequest.cached(i, true)).filterNot { it.origin == ResponseOrigin.Fetcher }.collect { response ->
                    log.info("[$i, ${Thread.currentThread().name}] $response")
                    assertTrue(response.requireData().getString("url").contains("key=$i"))
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
        coroutineScope {
            log.info("[${Thread.currentThread().name}] $store is built.")
            (1..8).forEach { i ->
                jobs += launch {
                    withTimeoutOrNull(2_000) {
                        val result = store.stream(StoreRequest.fresh(i))
                                .filterNot { it is StoreResponse.Loading }
                                .filter { it.requireData().id == i - 1 }
                                .transformLatest {
                                    log.info("[$i, ${Thread.currentThread().name}] transformLatest: $it")
                                    assertEquals(ResponseOrigin.Fetcher, it.origin)
                                    assertEquals(i - 1, it.requireData().id)
                                    emit(it.requireData())
                                }
                                .first()
                        log.info("[$i, ${Thread.currentThread().name}] $result")
                    }
                }
            }
        }
        (1..8).forEach { i ->
            jobs += launch(Dispatchers.IO) {
                store.stream(StoreRequest.cached(i, false)).transform { response ->
                    when (response) {
                        is StoreResponse.Data -> {
                            log.info("[$i, ${Thread.currentThread().name}] transform: ${response.value} from ${response.origin}")
                            assertEquals(ResponseOrigin.Cache, response.origin)
                            emit(response.value)
                        }
                        else -> log.info("[$i, ${Thread.currentThread().name}] $response")
                    }
                }.filter { it.id == i - 1 }.collectLatest { result ->
                    log.info("[$i, ${Thread.currentThread().name}] collectLatest: $result")
                    assertEquals(i - 1, result.id)
                }
            }
        }
        jobs.forEach { it.cancelAndJoin() }
    }

    private fun streamAsFlow(n: Int) = runBlocking(testScope.coroutineContext) {
        playgroundService.stream(n)
    }
}