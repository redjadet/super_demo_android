package com.redjadet.superdemo.features.feed

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.core.data.AppDatabase
import com.redjadet.superdemo.features.feed.data.CachingFeedRepository
import com.redjadet.superdemo.features.feed.data.RemoteFeedDataSource
import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import com.redjadet.superdemo.features.feed.domain.FeedPost
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CachingFeedRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CachingFeedRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun ensureSeededForOffline_populatesEmptyDatabase() = runTest {
        repository = CachingFeedRepository(FakeRemote(fail = true), database.feedPostDao(), database)
        repository.ensureSeededForOffline()
        assertThat(database.feedPostDao().count()).isAtLeast(3)
        assertThat(repository.observePosts().first()).isNotEmpty()
    }

    @Test
    fun refresh_writesRemoteIntoCacheTransactionally() = runTest {
        repository = CachingFeedRepository(
            FakeRemote(FeedLoadResult(posts = listOf(FeedPost(1, "Hello", "Body", 7)))),
            database.feedPostDao(),
            database,
        )
        val result = repository.refresh()
        assertThat(result.isStale).isFalse()
        assertThat(result.fromCache).isFalse()
        assertThat(database.feedPostDao().getAll().single().title).isEqualTo("Hello")
    }

    @Test
    fun refresh_returnsLocalCacheWhenRemoteFails() = runTest {
        repository = CachingFeedRepository(FakeRemote(fail = true), database.feedPostDao(), database)
        repository.ensureSeededForOffline()
        val result = repository.refresh()
        assertThat(result.isStale).isTrue()
        assertThat(result.fromCache).isTrue()
        assertThat(result.posts).isNotEmpty()
    }

    @Test
    fun refresh_preservesBookmarkToggledDuringNetworkWait() = runTest {
        val remote = object : RemoteFeedDataSource {
            override suspend fun fetchPosts(): FeedLoadResult {
                // Simulate toggle during in-flight request.
                repository.toggleBookmark(1)
                return FeedLoadResult(
                    posts = listOf(
                        FeedPost(1, "Hello", "Body", 1),
                        FeedPost(2, "World", "Body", 1),
                    ),
                )
            }
        }
        repository = CachingFeedRepository(remote, database.feedPostDao(), database)
        repository.ensureSeededForOffline()
        // Seed id=1 starts bookmarked=true; toggle during fetch flips to false then merge should keep false? 
        // Seed OfflineFeedSeed id=1 bookmarked=true. toggle -> false. Remote returns id=1; merge should keep false.
        val result = repository.refresh()
        assertThat(result.posts.first { it.id == 1 }.bookmarked).isFalse()
    }

    @Test
    fun toggleBookmark_isAtomic() = runTest {
        repository = CachingFeedRepository(FakeRemote(fail = true), database.feedPostDao(), database)
        repository.ensureSeededForOffline()
        val updated = repository.toggleBookmark(1)
        assertThat(updated?.bookmarked).isFalse() // seed starts true for id=1
        assertThat(database.feedPostDao().getById(1)?.bookmarked).isFalse()
    }

    private class FakeRemote(
        private val result: FeedLoadResult = FeedLoadResult(emptyList()),
        private val fail: Boolean = false,
    ) : RemoteFeedDataSource {
        override suspend fun fetchPosts(): FeedLoadResult {
            if (fail) error("network down")
            return result
        }
    }
}
