package com.rkfcheung.reactive.hello.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

// https://github.com/dropbox/Store/blob/master/store/src/test/java/com/dropbox/android/external/store4/testutil/InMemoryPersister.kt
class SimplePersister<Key, Output> {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val data = mutableMapOf<Key, Output>()

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun read(key: Key) = data[key].also {
        log.info("[$key] read: $it")
    }

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun write(key: Key, output: Output) {
        val oldOutput = data[key]
        data[key] = output
        log.info("[$key] write: $oldOutput -> $output")
    }

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun deleteByKey(key: Key) {
        val output = data.remove(key)
        log.info("[$key] deleteByKey: $output")
    }

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun deleteAll() {
        data.clear()
        log.info("[_all] deleteAll")
    }
}