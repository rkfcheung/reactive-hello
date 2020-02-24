package com.rkfcheung.reactive.hello

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class AbstractTest {
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)
}