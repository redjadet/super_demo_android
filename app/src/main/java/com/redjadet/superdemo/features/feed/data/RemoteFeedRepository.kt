package com.redjadet.superdemo.features.feed.data

import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import com.redjadet.superdemo.features.feed.domain.FeedPost
import javax.inject.Inject
import javax.inject.Singleton

interface RemoteFeedDataSource {
    suspend fun fetchPosts(): FeedLoadResult
}

@Singleton
class RemoteFeedRepository @Inject constructor(
    private val api: FeedApi,
) : RemoteFeedDataSource {
    override suspend fun fetchPosts(): FeedLoadResult {
        val posts = api.getPosts().take(20).map {
            FeedPost(
                id = it.id,
                title = it.title.replaceFirstChar { c -> c.uppercase() },
                body = it.body,
                userId = it.userId,
            )
        }
        return FeedLoadResult(posts = posts, isStale = false)
    }
}
