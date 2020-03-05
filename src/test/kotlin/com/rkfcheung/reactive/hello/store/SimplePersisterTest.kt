package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import com.rkfcheung.reactive.hello.AbstractTest
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@FlowPreview
internal class SimplePersisterTest : AbstractTest() {
    private val persister = SimplePersister<String, UUID>()

    @Test
    fun testUsePersister() = runBlocking {
        val size = 8
        val store = StoreBuilder.fromNonFlow<String, UUID> { key ->
            get(key)
        }.cachePolicy(
                MemoryPolicy.builder()
                        .setMemorySize(size.toLong())
                        .setExpireAfterAccess(20)
                        .setExpireAfterTimeUnit(TimeUnit.MICROSECONDS)
                        .build()
        ).nonFlowingPersister(
                reader = persister::read,
                writer = { key, value -> persister.write(key, value) },
                delete = persister::deleteByKey,
                deleteAll = persister::deleteAll
        ).build()

        val key = "hello"
        val result = store.get(key)
        log.info("$result vs ${store.get(key)}")
        assertEquals(result, store.get(key))
        assertNotEquals(result, store.fresh(key))

        val data = mutableMapOf<String, UUID>()
        repeat(size * 2) { i ->
            val k = i.toString()
            data[k] = store.get(k)

            val p = (i - size).toString()
            val v = data[p] ?: return@repeat
            val refreshed = i > size && i % 2 == 0
            if (refreshed)
                persister.deleteByKey(p)
            val u = store.get(p)
            log.info("[$p] $v vs $u")
            if (refreshed)
                assertNotEquals(v, u)
            else
                assertEquals(v, u)
        }

        assertTrue(true)
    }

    private fun get(key: String) = UUID.randomUUID().also {
        log.info("get($key) -> $it")
    }
}