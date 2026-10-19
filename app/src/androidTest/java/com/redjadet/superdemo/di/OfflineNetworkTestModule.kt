package com.redjadet.superdemo.di

import com.redjadet.superdemo.core.di.NetworkModule
import com.redjadet.superdemo.features.feed.data.RemoteFeedDataSource
import com.redjadet.superdemo.features.feed.domain.FeedLoadResult
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class],
)
object OfflineNetworkTestModule {

    @Provides
    @Singleton
    fun provideFailingRemote(): RemoteFeedDataSource = object : RemoteFeedDataSource {
        override suspend fun fetchPosts(): FeedLoadResult {
            error("forced offline for instrumentation")
        }
    }
}
