package com.example.gymnastlink.model

data class CacheEntry<T>(
    val data: T,
    val timestamp: Long
)