package com.redjadet.superdemo.features.feed

import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import com.redjadet.superdemo.features.feed.domain.FeedPost
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.feed.domain.RefreshFeedUseCase
import com.redjadet.superdemo.features.feed.domain.FeedDisplayError
import com.redjadet.superdemo.features.feed.presentation.FeedViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
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

    @Test
    fun refresh_successEmptyRemoteSetsEmptyErrorNotNetwork() = runTest(dispatcher) {
        val repository = FakeFeedRepository(
            local = emptyList(),
            refreshResult = FeedLoadResult(posts = emptyList(), fromCache = false, isStale = false),
        )
        val viewModel = FeedViewModel(RefreshFeedUseCase(repository), repository)
        advanceUntilIdle()

        assertThat(viewModel.state.value.posts).isEmpty()
        assertThat(viewModel.state.value.error).isEqualTo(FeedDisplayError.Empty)
    }

    @Test
    fun refresh_cancelKeepsLocalPostsWithoutNetworkError() = runTest(dispatcher) {
        val hangStarted = CompletableDeferred<Unit>()
        val repository = object : FeedRepository {
            private val posts = MutableStateFlow(
                listOf(FeedPost(1, "Local", "Body", 1)),
            )
            private var refreshCount = 0

            override fun observePosts(): Flow<List<FeedPost>> = posts

            override suspend fun refresh(): FeedLoadResult {
                refreshCount += 1
                if (refreshCount == 1) {
                    hangStarted.complete(Unit)
                    awaitCancellation()
                }
                return FeedLoadResult(posts = posts.value, fromCache = false)
            }

            override suspend fun toggleBookmark(postId: Int): FeedPost? = null

            override suspend fun ensureSeededForOffline() = Unit
        }

        val viewModel = FeedViewModel(RefreshFeedUseCase(repository), repository)
        // Pump until the first refresh parks on awaitCancellation (don't use
        // advanceUntilIdle — it never finishes while a job is suspended).
        var spins = 0
        while (!hangStarted.isCompleted && spins < 50) {
            dispatcher.scheduler.runCurrent()
            spins += 1
        }
        assertThat(hangStarted.isCompleted).isTrue()
        viewModel.refresh() // cancels the hung first refresh
        advanceUntilIdle()

        assertThat(viewModel.state.value.posts).isNotEmpty()
        assertThat(viewModel.state.value.error).isNull()
        assertThat(viewModel.state.value.isLoading).isFalse()
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
