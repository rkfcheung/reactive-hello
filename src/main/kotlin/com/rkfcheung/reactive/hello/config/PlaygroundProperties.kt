package com.rkfcheung.reactive.hello.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@ConstructorBinding
@ConfigurationProperties("playground")
data class PlaygroundProperties(var serviceUrl: String, var auth: PlaygroundAuth)

data class PlaygroundAuth(override var login: String, override var password: String): Auth() {
    constructor(auth: Auth): this(auth.login, auth.password)
}

@Component
class Auth {
    lateinit var login: String
    lateinit var password: String
}
