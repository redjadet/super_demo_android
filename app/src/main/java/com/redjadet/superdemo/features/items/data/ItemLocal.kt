package com.redjadet.superdemo.features.items.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.redjadet.superdemo.features.items.domain.Item
import com.redjadet.superdemo.features.items.domain.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String,
    val createdAtEpochMs: Long,
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY createdAtEpochMs DESC")
    suspend fun getAll(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ItemEntity): Long

    @Update
    suspend fun update(entity: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun count(): Int
}

fun ItemEntity.toDomain(): Item = Item(
    id = id,
    title = title,
    notes = notes,
    createdAtEpochMs = createdAtEpochMs,
)

@Singleton
class RoomItemRepository @Inject constructor(
    private val dao: ItemDao,
    private val seedStore: ItemSeedStore,
) : ItemRepository {

    override fun observeItems(): Flow<List<Item>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun ensureSeeded() {
        if (seedStore.hasSeeded()) return
        if (dao.count() == 0) {
            listOf(
                "Room persistence" to "Local Items store with suspend DAO APIs.",
                "UDF ViewModel" to "StateFlow UI state driven by Room Flow.",
                "Welcome to Items" to "Open an item to edit its title and notes. Your changes stay on this device.",
            ).forEach { (title, notes) ->
                dao.insert(
                    ItemEntity(
                        title = title,
                        notes = notes,
                        createdAtEpochMs = System.currentTimeMillis(),
                    ),
                )
            }
        }
        seedStore.markSeeded()
    }

    override suspend fun getItem(id: Long): Item? = dao.getById(id)?.toDomain()

    override suspend fun addItem(title: String, notes: String): Item {
        ensureSeeded()
        val entity = ItemEntity(
            title = title.trim(),
            notes = notes.trim(),
            createdAtEpochMs = System.currentTimeMillis(),
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun updateItem(id: Long, title: String, notes: String): Item? {
        val existing = dao.getById(id) ?: return null
        val updated = existing.copy(title = title.trim(), notes = notes.trim())
        dao.update(updated)
        return updated.toDomain()
    }

    override suspend fun deleteItem(id: Long) {
        dao.delete(id)
    }
}
