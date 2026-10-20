package com.redjadet.superdemo.features.feed.data

/**
 * Deterministic local seed so Dashboard / Feed / Items remain usable with airplane mode
 * on first launch (reviewer demo path). Remote refresh replaces these when online.
 */
object OfflineFeedSeed {
    fun posts(nowEpochMs: Long = System.currentTimeMillis()): List<CachedFeedPostEntity> = listOf(
        CachedFeedPostEntity(
            id = 1,
            title = "Offline-first Feed cache",
            body = "Room is the source of truth. This post is seeded locally so the Feed tab works without network.",
            userId = 1,
            bookmarked = true,
            cachedAtEpochMs = nowEpochMs,
        ),
        CachedFeedPostEntity(
            id = 2,
            title = "Sync when online",
            body = "Pull to refresh replaces the seed with JSONPlaceholder posts when the device is online.",
            userId = 1,
            bookmarked = false,
            cachedAtEpochMs = nowEpochMs,
        ),
        CachedFeedPostEntity(
            id = 3,
            title = "Keep reading offline",
            body = "If a later refresh fails, the UI keeps local posts and shows a stale banner.",
            userId = 2,
            bookmarked = false,
            cachedAtEpochMs = nowEpochMs,
        ),
    )
}
