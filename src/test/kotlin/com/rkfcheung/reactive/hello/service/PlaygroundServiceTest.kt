package com.rkfcheung.reactive.hello.service

import com.rkfcheung.reactive.hello.AbstractTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.test.StepVerifier

internal class PlaygroundServiceTest : AbstractTest() {

    @Autowired
    private lateinit var playgroundService: PlaygroundService

    @Test
    fun testStream() = runBlocking {
        val result = playgroundService.stream(16).take(2)
        val ts = StepVerifier.create(result)
                .expectNextMatches {
                    log.info(it.toString())
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