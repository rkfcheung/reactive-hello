package com.rkfcheung.reactive.hello.service

import com.rkfcheung.reactive.hello.model.ConfigConstants
import com.rkfcheung.reactive.hello.model.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class PlaygroundService {
    @Autowired
    @Qualifier(ConfigConstants.PLAYGROUND_CLIENT)
    private lateinit var client: WebClient

    suspend fun stream(n: Int) = client.get()
            .uri("${ConfigConstants.PATH_STREAM}/{n}", n)
            .awaitExchange()
            .bodyToFlux(Result::class.java)

}