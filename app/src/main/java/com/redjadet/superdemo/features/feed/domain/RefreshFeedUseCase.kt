package com.redjadet.superdemo.features.feed.domain

/** Domain use case — constructed in DI (`AppModule`); no framework imports here. */
class RefreshFeedUseCase(
    private val repository: FeedRepository,
) {
    suspend operator fun invoke(): FeedLoadResult = repository.refresh()
}
