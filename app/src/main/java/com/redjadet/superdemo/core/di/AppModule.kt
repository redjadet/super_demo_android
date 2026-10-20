package com.redjadet.superdemo.core.di

import android.content.Context
import androidx.room.Room
import com.redjadet.superdemo.core.data.AppDatabase
import com.redjadet.superdemo.features.feed.data.FeedPostDao
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.feed.domain.RefreshFeedUseCase
import com.redjadet.superdemo.features.items.data.ItemDao
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
        // Portfolio sample: schema is small and disposable; destructive migration
        // avoids shipping unused migration paths. Not a production migration story.
        Room.databaseBuilder(context, AppDatabase::class.java, "super_demo.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideFeedPostDao(db: AppDatabase): FeedPostDao = db.feedPostDao()

    @Provides
    fun provideItemDao(db: AppDatabase): ItemDao = db.itemDao()

    @Provides
    fun provideRefreshFeedUseCase(repository: FeedRepository): RefreshFeedUseCase =
        RefreshFeedUseCase(repository)
}
