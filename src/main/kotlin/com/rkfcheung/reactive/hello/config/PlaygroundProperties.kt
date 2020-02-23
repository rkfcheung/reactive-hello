package com.rkfcheung.reactive.hello.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("playground")
data class PlaygroundProperties(var serviceUrl: String, var login: String, var password: String)