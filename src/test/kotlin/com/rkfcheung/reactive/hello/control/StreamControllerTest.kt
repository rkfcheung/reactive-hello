package com.rkfcheung.reactive.hello.control

import com.rkfcheung.reactive.hello.AbstractTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class StreamControllerTest : AbstractTest() {

    @Autowired
    private lateinit var streamController: StreamController

    @Test
    fun testStream() = runBlocking {
        val result = streamController.stream(8).take(2)
        val ts = StepVerifier.create(result)
                .expectNextMatches {
                    assertEquals(0, it.id)
                    it.id == 0
                }
                .expectNextMatches {
                    log.info(it.toString())
                    it.id == 1
                }
                .verifyComplete()
        log.info("Duration : $ts")
    }
}