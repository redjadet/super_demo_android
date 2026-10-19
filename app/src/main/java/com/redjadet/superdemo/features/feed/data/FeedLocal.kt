package com.redjadet.superdemo.features.feed.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.redjadet.superdemo.features.feed.domain.FeedPost
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_feed_posts")
data class CachedFeedPostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val bookmarked: Boolean = false,
    val cachedAtEpochMs: Long,
)

@Dao
interface FeedPostDao {
    @Query("SELECT * FROM cached_feed_posts ORDER BY id ASC")
    fun observeAll(): Flow<List<CachedFeedPostEntity>>

    @Query("SELECT * FROM cached_feed_posts ORDER BY id ASC")
    suspend fun getAll(): List<CachedFeedPostEntity>

    @Query("SELECT * FROM cached_feed_posts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CachedFeedPostEntity?

    @Query("SELECT COUNT(*) FROM cached_feed_posts")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(posts: List<CachedFeedPostEntity>)

    @Query("UPDATE cached_feed_posts SET bookmarked = :bookmarked WHERE id = :id")
    suspend fun setBookmarked(id: Int, bookmarked: Boolean)

    @Query("DELETE FROM cached_feed_posts")
    suspend fun clear()
}

fun CachedFeedPostEntity.toDomain(): FeedPost = FeedPost(
    id = id,
    title = title,
    body = body,
    userId = userId,
    bookmarked = bookmarked,
)

fun PostDto.toEntity(cachedAtEpochMs: Long, bookmarked: Boolean = false): CachedFeedPostEntity =
    CachedFeedPostEntity(
        id = id,
        title = title,
        body = body,
        userId = userId,
        bookmarked = bookmarked,
        cachedAtEpochMs = cachedAtEpochMs,
    )
