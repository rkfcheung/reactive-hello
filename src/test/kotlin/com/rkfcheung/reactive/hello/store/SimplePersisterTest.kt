package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.*
import com.rkfcheung.reactive.hello.AbstractTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@FlowPreview
internal class SimplePersisterTest : AbstractTest() {
    @ExperimentalCoroutinesApi
    private val persister = SimplePersister<String, UUID>()

    @Test
    fun testUsePersister() = testScope.runBlockingTest {
        val size = 8
        val store = StoreBuilder.fromNonFlow<String, UUID> { key ->
            get(key)
        }.scope(testScope).cachePolicy(
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
        val times = size * 2
        repeat(times) { i ->
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

        log.info(persister.toString())
        for (i in 0 until times) {
            assertNotNull(persister.read(i.toString()))
        }

        val job = launch {
            store.stream(StoreRequest.cached(key, false)).collect {
                log.info(it.toString())
                assertEquals(persister.read(key), it.requireData())
            }
        }
        job.cancelAndJoin()
    }

    private fun get(key: String) = UUID.randomUUID().also {
        log.info("get($key) -> $it")
    }
}