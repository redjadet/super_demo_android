package com.redjadet.superdemo.features.feed.data

import androidx.room.withTransaction
import com.redjadet.superdemo.core.data.AppDatabase
import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import com.redjadet.superdemo.features.feed.domain.FeedPost
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingFeedRepository @Inject constructor(
    private val remote: RemoteFeedDataSource,
    private val dao: FeedPostDao,
    private val database: AppDatabase,
) : FeedRepository {

    override fun observePosts(): Flow<List<FeedPost>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun ensureSeededForOffline() {
        if (dao.count() == 0) {
            dao.upsertAll(OfflineFeedSeed.posts())
        }
    }

    override suspend fun refresh(): FeedLoadResult {
        ensureSeededForOffline()
        return try {
            val remoteResult = remote.fetchPosts()
            val now = System.currentTimeMillis()
            // Bookmark map + replace must share one transaction so a toggle after
            // the network returns but before upsert cannot be overwritten.
            val entities = database.withTransaction {
                val bookmarkMap = dao.getAll().associate { it.id to it.bookmarked }
                val mapped = remoteResult.posts.map { post ->
                    CachedFeedPostEntity(
                        id = post.id,
                        title = post.title,
                        body = post.body,
                        userId = post.userId,
                        bookmarked = bookmarkMap[post.id] == true,
                        cachedAtEpochMs = now,
                    )
                }
                dao.clear()
                dao.upsertAll(mapped)
                mapped
            }
            FeedLoadResult(
                posts = entities.map { it.toDomain() },
                isStale = false,
                fromCache = false,
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            val cached = dao.getAll()
            FeedLoadResult(
                posts = cached.map { it.toDomain() },
                isStale = cached.isNotEmpty(),
                fromCache = true,
            )
        }
    }

    override suspend fun toggleBookmark(postId: Int): FeedPost? {
        return database.withTransaction {
            val existing = dao.getById(postId) ?: return@withTransaction null
            val next = !existing.bookmarked
            dao.setBookmarked(postId, next)
            existing.copy(bookmarked = next).toDomain()
        }
    }
}
