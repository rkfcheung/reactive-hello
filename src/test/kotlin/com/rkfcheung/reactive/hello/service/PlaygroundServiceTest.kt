package com.rkfcheung.reactive.hello.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
internal class PlaygroundServiceTest {

    @Autowired
    private lateinit var playgroundService: PlaygroundService

    @Test
    fun testStream() = runBlocking {
        val result = playgroundService.stream(16).take(2)
        val ts = StepVerifier.create(result)
                .expectNextMatches {
                    println(it)
                    it.id == 0
                }
                .expectNextMatches {
                    println(it)
                    it.id == 1
                }
                .verifyComplete()
        println(ts)
    }
}