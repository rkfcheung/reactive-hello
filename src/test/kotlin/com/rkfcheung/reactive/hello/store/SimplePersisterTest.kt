package com.rkfcheung.reactive.hello.store

import com.dropbox.android.external.store4.*
import com.rkfcheung.reactive.hello.AbstractTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExperimentalStoreApi
@FlowPreview
internal class SimplePersisterTest : AbstractTest() {
    private val persister = SimplePersister<String, UUID>()

    @Test
    fun testUsePersister() = testScope.runBlockingTest {
        val size = 8
        val store = prepareStore(size.toLong())

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
                persister.delete(p)
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

    @Test
    fun testGetFreshClearKeys() = testScope.runBlockingTest {
        val store = prepareStore(2L)
        val a = store.get("a")
        val b = store.get("b")
        val c = store.get("c")
        assertEquals(3, persister.count())
        assertEquals(a, store.get("a"))
        assertEquals(b, store.get("b"))
        assertEquals(c, store.get("c"))
        assertEquals(persister.read("a"), store.get("a"))
        assertEquals(persister.read("b"), store.get("b"))
        assertEquals(persister.read("c"), store.get("c"))

        val c2 = get("c")
        persister.write("c", c2)
        assertNotEquals(c, store.get("c"))
        assertEquals(c2, store.get("c"))
        assertNotEquals(c2, store.fresh("c"))

        store.clear("a")
        assertEquals(2, persister.count())
        assertNotEquals(a, store.get("a"))
        assertEquals(3, persister.count())

        store.clearAll()
        assertEquals(0, persister.count())
    }

    private fun prepareStore(maxSize: Long) = StoreBuilder.fromNonFlow<String, UUID> { key ->
        get(key)
    }.scope(testScope).cachePolicy(
            MemoryPolicy.builder()
                    .setMemorySize(maxSize)
                    .setExpireAfterAccess(20)
                    .setExpireAfterTimeUnit(TimeUnit.MICROSECONDS)
                    .build()
    ).nonFlowingPersister(
            reader = persister::read,
            writer = { key, value -> persister.write(key, value) },
            delete = persister::delete,
            deleteAll = persister::deleteAll
    ).build()

    private fun get(key: String) = UUID.randomUUID().also {
        log.info("get($key) -> $it")
    }
}