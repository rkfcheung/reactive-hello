package com.rkfcheung.reactive.hello.service

import com.rkfcheung.reactive.hello.model.ConfigConstants
import com.rkfcheung.reactive.hello.model.StreamResult
import kotlinx.coroutines.reactive.asFlow
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class PlaygroundService {
    @Autowired
    @Qualifier(ConfigConstants.PLAYGROUND_CLIENT)
    private lateinit var client: WebClient

    suspend fun stream(n: Int) = client.get()
            .uri("${ConfigConstants.PATH_STREAM}/{n}", n)
            .accept(MediaType.ALL)
            .awaitExchange()
            .bodyToFlux(StreamResult::class.java)
            .asFlow()

    suspend fun get(keyId: Int) = client.get()
            .uri("${ConfigConstants.PATH_GET}?key=$keyId", keyId)
            .awaitExchange()
            .awaitBodyOrNull<String>()?.let { JSONObject(it) } ?: JSONObject()
}