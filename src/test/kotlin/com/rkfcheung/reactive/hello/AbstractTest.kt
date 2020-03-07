package com.rkfcheung.reactive.hello

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class AbstractTest {
    @ExperimentalCoroutinesApi
    protected val testScope = TestCoroutineScope()
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)
}