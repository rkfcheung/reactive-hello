package com.rkfcheung.reactive.hello.control

import com.rkfcheung.reactive.hello.AbstractTest
import com.rkfcheung.reactive.hello.model.StreamResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.retrieveFlow

internal class StreamControllerTest : AbstractTest() {

    @Autowired
    private lateinit var streamController: StreamController

    @Test
    fun testStream() = runBlocking {
        var i = 0
        val size = 8
        streamController.stream(size).collect {
            log.info("Result: $it")
            i++
        }
        assertEquals(size, i)
    }

    @Test
    fun testRSocketRequester() = runBlocking {
        var i = 0
        val size = 2
        val strategies = RSocketStrategies.builder()
                .encoders { it.add(Jackson2CborEncoder()) }
                .decoders { it.add(Jackson2CborDecoder()) }
                .build()
        val requester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .connectTcp("localhost", 7000)
                .block()
        requester?.route("stream")?.data(size)?.retrieveFlow<StreamResult>()?.collect {
        log.info("Result: $it")
            i++
        }
        assertEquals(size, i)
    }
}