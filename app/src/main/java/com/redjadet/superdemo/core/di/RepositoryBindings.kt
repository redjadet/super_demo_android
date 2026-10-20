package com.redjadet.superdemo.core.di

import com.redjadet.superdemo.features.dashboard.data.SampleDashboardRepository
import com.redjadet.superdemo.features.dashboard.domain.DashboardRepository
import com.redjadet.superdemo.features.feed.data.CachingFeedRepository
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.items.data.RoomItemRepository
import com.redjadet.superdemo.features.items.domain.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {

    // Invoked by Hilt code generation — IDE unused-symbol inspections are false positives.
    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindFeedRepository(impl: CachingFeedRepository): FeedRepository

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindItemRepository(impl: RoomItemRepository): ItemRepository

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindDashboardRepository(impl: SampleDashboardRepository): DashboardRepository
}
