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
            // Read bookmarks after network returns so mid-flight toggles win.
            val bookmarkMap = dao.getAll().associate { it.id to it.bookmarked }
            val now = System.currentTimeMillis()
            val entities = remoteResult.posts.map { post ->
                CachedFeedPostEntity(
                    id = post.id,
                    title = post.title,
                    body = post.body,
                    userId = post.userId,
                    bookmarked = bookmarkMap[post.id] == true,
                    cachedAtEpochMs = now,
                )
            }
            database.withTransaction {
                dao.clear()
                dao.upsertAll(entities)
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
