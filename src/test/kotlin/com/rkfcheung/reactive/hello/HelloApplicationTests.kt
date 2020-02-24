package com.rkfcheung.reactive.hello

import com.rkfcheung.reactive.hello.config.Auth
import com.rkfcheung.reactive.hello.config.PlaygroundAuth
import com.rkfcheung.reactive.hello.config.PlaygroundProperties
import com.rkfcheung.reactive.hello.model.ConfigConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value

class HelloApplicationTests : AbstractTest() {

    @Autowired
    private lateinit var properties: PlaygroundProperties

    @Autowired
    private lateinit var adminAuth: PlaygroundAuth

    @Autowired
    @Qualifier(ConfigConstants.USER_AUTH)
    private lateinit var userAuth: Auth

    @Autowired
    private lateinit var guestAuth: Auth

    @Value("\${playground.serviceUrl}")
    private lateinit var serviceUrl: String

    @Value("\${playground.admin.login}")
    private lateinit var adminLogin: String

    @Value("\${playground.guest.login}")
    private lateinit var guestLogin: String

    @Test
    fun contextLoads() {
        log.info("Hello World!")
    }

    @Test
    fun testLoadProperties() {
        assertEquals(serviceUrl, properties.serviceUrl)
        assertEquals(PlaygroundAuth(userAuth), properties.auth)
        assertEquals(adminLogin, adminAuth.login)
        assertEquals(guestLogin, guestAuth.login)
    }

}
