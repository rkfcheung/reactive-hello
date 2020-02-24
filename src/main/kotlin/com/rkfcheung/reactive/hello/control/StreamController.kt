package com.rkfcheung.reactive.hello.control

import com.rkfcheung.reactive.hello.service.PlaygroundService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class StreamController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var service: PlaygroundService

    @MessageMapping("stream")
    suspend fun stream(n: Int) = service.stream(n)
            .also { log.info(it.toString()) }
}