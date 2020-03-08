package com.rkfcheung.reactive.hello.store

interface DiskDelete<Key> {
    suspend fun delete(key: Key)
    suspend fun deleteAll()
}