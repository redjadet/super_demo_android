package com.redjadet.superdemo.features.feed

import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import com.redjadet.superdemo.features.feed.domain.FeedPost
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.feed.domain.RefreshFeedUseCase
import com.redjadet.superdemo.features.feed.presentation.FeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_emitsLocalPostsBeforeRemoteSuccess() = runTest(dispatcher) {
        val repository = FakeFeedRepository(
            local = listOf(FeedPost(1, "Local", "Body", 1)),
            refreshResult = FeedLoadResult(
                posts = listOf(FeedPost(1, "Local", "Body", 1), FeedPost(2, "Remote", "Body", 1)),
                fromCache = false,
            ),
        )
        val viewModel = FeedViewModel(RefreshFeedUseCase(repository), repository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.posts.map { it.id }).containsAtLeast(1, 2)
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun refresh_marksShowingLocalOnlyWhenOffline() = runTest(dispatcher) {
        val repository = FakeFeedRepository(
            local = listOf(FeedPost(1, "Offline-first Feed cache", "Body", 1)),
            refreshResult = FeedLoadResult(
                posts = listOf(FeedPost(1, "Offline-first Feed cache", "Body", 1)),
                isStale = true,
                fromCache = true,
            ),
        )
        val viewModel = FeedViewModel(RefreshFeedUseCase(repository), repository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.showingLocalOnly).isTrue()
        assertThat(viewModel.state.value.isStale).isTrue()
        assertThat(viewModel.state.value.posts).isNotEmpty()
        assertThat(viewModel.state.value.error).isNull()
    }

    private class FakeFeedRepository(
        local: List<FeedPost>,
        private val refreshResult: FeedLoadResult,
    ) : FeedRepository {
        private val posts = MutableStateFlow(local)

        override fun observePosts(): Flow<List<FeedPost>> = posts

        override suspend fun refresh(): FeedLoadResult {
            posts.value = refreshResult.posts
            return refreshResult
        }

        override suspend fun toggleBookmark(postId: Int): FeedPost? {
            val updated = posts.value.map {
                if (it.id == postId) it.copy(bookmarked = !it.bookmarked) else it
            }
            posts.value = updated
            return updated.firstOrNull { it.id == postId }
        }

        override suspend fun ensureSeededForOffline() = Unit
    }
}
