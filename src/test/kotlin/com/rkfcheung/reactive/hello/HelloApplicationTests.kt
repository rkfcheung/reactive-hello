package com.rkfcheung.reactive.hello

import com.rkfcheung.reactive.hello.config.PlaygroundProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HelloApplicationTests {

	@Autowired
	private lateinit var properties: PlaygroundProperties

	@Test
	fun contextLoads() {
	}

	@Test
	fun testLoadProperties() {
		println(properties)
	}

}
