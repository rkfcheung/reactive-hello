package com.rkfcheung.reactive.hello.config

import com.rkfcheung.reactive.hello.model.ConfigConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(PlaygroundProperties::class)
class PlaygroundConfig {

    @Autowired
    private lateinit var properties: PlaygroundProperties

    @Bean(ConfigConstants.PLAYGROUND_CLIENT)
    fun playgroundClient() = WebClient.builder()
            .baseUrl(properties.serviceUrl)
            .build()

}