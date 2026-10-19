package com.redjadet.superdemo.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.redjadet.superdemo.features.feed.data.CachedFeedPostEntity
import com.redjadet.superdemo.features.feed.data.FeedPostDao
import com.redjadet.superdemo.features.items.data.ItemDao
import com.redjadet.superdemo.features.items.data.ItemEntity

@Database(
    entities = [CachedFeedPostEntity::class, ItemEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedPostDao(): FeedPostDao
    abstract fun itemDao(): ItemDao
}
