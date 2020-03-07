package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.Persister
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// https://github.com/dropbox/Store/blob/master/store/src/test/java/com/dropbox/android/external/store4/testutil/InMemoryPersister.kt
class SimplePersister<Key, Raw>: Persister<Raw, Key> {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val data = mutableMapOf<Key, Raw>()

    override suspend fun read(key: Key) = data[key].also {
        log.info("[$key] read: $it")
    }

    override suspend fun write(key: Key, raw: Raw): Boolean {
        val oldRaw = data[key]
        data[key] = raw
        log.info("[$key] write: $oldRaw -> $raw")
        return true
    }

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun delete(key: Key) {
        val output = data.remove(key)
        log.info("[$key] remove: $output")
    }

    @Suppress("RedundantSuspendModifier") // for function reference
    suspend fun deleteAll() {
        data.clear()
        log.info("[_all] deleteAll")
    }

    fun count() = data.size

    override fun toString(): String {
        return "SimplePersister(data=$data)"
    }
}