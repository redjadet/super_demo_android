package com.redjadet.superdemo.core.di

import android.content.Context
import androidx.room.Room
import com.redjadet.superdemo.core.data.AppDatabase
import com.redjadet.superdemo.features.dashboard.data.SampleDashboardRepository
import com.redjadet.superdemo.features.dashboard.domain.DashboardRepository
import com.redjadet.superdemo.features.feed.data.CachingFeedRepository
import com.redjadet.superdemo.features.feed.data.FeedPostDao
import com.redjadet.superdemo.features.feed.data.RemoteFeedDataSource
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.items.data.ItemDao
import com.redjadet.superdemo.features.items.data.ItemSeedStore
import com.redjadet.superdemo.features.items.data.RoomItemRepository
import com.redjadet.superdemo.features.items.domain.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "super_demo.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideFeedPostDao(db: AppDatabase): FeedPostDao = db.feedPostDao()

    @Provides
    fun provideItemDao(db: AppDatabase): ItemDao = db.itemDao()

    @Provides
    @Singleton
    fun provideFeedRepository(
        remote: RemoteFeedDataSource,
        dao: FeedPostDao,
        database: AppDatabase,
    ): FeedRepository = CachingFeedRepository(remote, dao, database)

    @Provides
    @Singleton
    fun provideItemSeedStore(@ApplicationContext context: Context): ItemSeedStore =
        ItemSeedStore(context)

    @Provides
    @Singleton
    fun provideItemRepository(
        dao: ItemDao,
        seedStore: ItemSeedStore,
    ): ItemRepository = RoomItemRepository(dao, seedStore)

    @Provides
    @Singleton
    fun provideDashboardRepository(): DashboardRepository = SampleDashboardRepository()
}
