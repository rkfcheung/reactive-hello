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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


@ExperimentalCoroutinesApi
@FlowPreview
internal class StoreTest : AbstractTest() {
    @Autowired
    private lateinit var playgroundService: PlaygroundService


    @Test
    fun testBuildStore() = runBlocking {
        val store = StoreBuilder.fromNonFlow<Int, Map<String, Any>> { keyId ->
            playgroundService.get(keyId)
        }.build()
        (1..8).forEach { i ->
            val response = store.get(i)
            log.info(response.toString())
            assertTrue(response["url"]?.toString()?.contains("key=$i") == true)
        }
    }

    @InternalCoroutinesApi
    @Test
    fun testBuildStoreFromFlow() = runBlocking(Dispatchers.Default) {
        val store = StoreBuilder.from<Int, StreamResult> {
            runBlocking {
                playgroundService.stream(it)
            }
        }.build()
        withTimeoutOrNull(5_000) {
            store.stream(StoreRequest.fresh(2)).collect { response ->
                when (response) {
                    is StoreResponse.Data -> log.info(response.value.toString())
                    else -> log.info(response.toString())
                }
            }
        }
        assertTrue(true)
    }
}