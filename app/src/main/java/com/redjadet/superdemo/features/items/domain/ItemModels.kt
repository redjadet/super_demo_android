package com.redjadet.superdemo.features.items.domain

import kotlinx.coroutines.flow.Flow

data class Item(
    val id: Long,
    val title: String,
    val notes: String,
    val createdAtEpochMs: Long,
)

interface ItemRepository {
    fun observeItems(): Flow<List<Item>>
    suspend fun ensureSeeded()
    suspend fun getItem(id: Long): Item?
    suspend fun addItem(title: String, notes: String): Item
    suspend fun updateItem(id: Long, title: String, notes: String): Item?
    suspend fun deleteItem(id: Long)
}
