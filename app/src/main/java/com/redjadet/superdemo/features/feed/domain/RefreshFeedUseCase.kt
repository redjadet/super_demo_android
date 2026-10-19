package com.redjadet.superdemo.features.feed.domain

import javax.inject.Inject

class RefreshFeedUseCase @Inject constructor(
    private val repository: FeedRepository,
) {
    suspend operator fun invoke(): FeedLoadResult = repository.refresh()
}
