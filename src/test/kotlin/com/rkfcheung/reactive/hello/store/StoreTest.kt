package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.get
import com.rkfcheung.reactive.hello.AbstractTest
import com.rkfcheung.reactive.hello.model.StreamResult
import com.rkfcheung.reactive.hello.service.PlaygroundService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
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
    fun testBuildStore() = runBlocking {
        val store = StoreBuilder.fromNonFlow<Int, JSONObject> { keyId ->
            JSONObject(playgroundService.get(keyId))
        }.build()

        (1..8).forEach { i ->
            val json = store.get(i)
            log.info(json.toString())
            assertTrue(json.get("url").toString().contains("key=$i"))

            withTimeoutOrNull(1_000) {
                store.stream(StoreRequest.cached(i, true)).take(i).collect { response ->
                    log.info(response.toString())
                }
            }
        }
    }

    @Test
    fun testBuildStoreFromFlow() = runBlocking(Dispatchers.Default) {
        val store = StoreBuilder.from<Int, StreamResult> {
            streamAsFlow(it)
        }.build()

        (1..8).forEach { i ->
            withTimeoutOrNull(1_000) {
                store.stream(StoreRequest.fresh(i)).collect { response ->
                    when (response) {
                        is StoreResponse.Data -> log.info(response.value.toString())
                        else -> log.info(response.toString())
                    }
                }
            }
        }
    }

    private fun streamAsFlow(n: Int) = runBlocking {
        playgroundService.stream(n)
    }
}