package com.redjadet.superdemo.features.feed.domain

import kotlinx.coroutines.flow.Flow

data class FeedPost(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val bookmarked: Boolean = false,
)

data class FeedLoadResult(
    val posts: List<FeedPost>,
    val isStale: Boolean = false,
    val fromCache: Boolean = false,
)

sealed interface FeedDisplayError {
    data object Network : FeedDisplayError
    data object Empty : FeedDisplayError
    data class Unknown(val message: String) : FeedDisplayError
}

/**
 * Offline-first Feed contract:
 * - [observePosts] is the local source of truth (Room).
 * - [refresh] syncs remote when online and updates local; on failure keeps local and marks stale.
 */
interface FeedRepository {
    fun observePosts(): Flow<List<FeedPost>>
    suspend fun refresh(): FeedLoadResult
    suspend fun toggleBookmark(postId: Int): FeedPost?
    suspend fun ensureSeededForOffline()
}
