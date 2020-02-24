package com.rkfcheung.reactive.hello.config

import com.rkfcheung.reactive.hello.model.ConfigConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(PlaygroundProperties::class)
class PlaygroundConfig {

    @Autowired
    private lateinit var properties: PlaygroundProperties

    @Bean(name = [ConfigConstants.ADMIN_AUTH])
    @ConfigurationProperties(prefix = "playground.admin")
    fun adminAuth(
            @Value("\${playground.admin.login}") login: String,
            @Value("\${playground.admin.password}") password: String
    ) = PlaygroundAuth(login, password)

    @Bean(name = [ConfigConstants.USER_AUTH])
    @ConfigurationProperties(prefix = "playground.auth")
    fun userAuth() = Auth()

    @Bean(name = [ConfigConstants.GUEST_AUTH])
    @Primary
    @ConfigurationProperties(prefix = "playground.guest")
    fun guestAuth() = Auth()

    @Bean(ConfigConstants.PLAYGROUND_CLIENT)
    fun playgroundClient() = WebClient.builder()
            .baseUrl(properties.serviceUrl)
            .build()

}