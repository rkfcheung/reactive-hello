package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.get
import com.rkfcheung.reactive.hello.AbstractTest
import com.rkfcheung.reactive.hello.service.PlaygroundService
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class StoreTest : AbstractTest() {
    @Autowired
    private lateinit var playgroundService: PlaygroundService

    @ExperimentalCoroutinesApi
    @FlowPreview
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
}