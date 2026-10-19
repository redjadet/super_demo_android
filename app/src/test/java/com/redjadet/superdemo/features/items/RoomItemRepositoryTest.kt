package com.redjadet.superdemo.features.items

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.core.data.AppDatabase
import com.redjadet.superdemo.features.items.data.ItemSeedStore
import com.redjadet.superdemo.features.items.data.RoomItemRepository
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
class RoomItemRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: RoomItemRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        // Fresh prefs name per test via clear
        context.getSharedPreferences("items_seed", Context.MODE_PRIVATE).edit().clear().apply()
        repository = RoomItemRepository(database.itemDao(), ItemSeedStore(context))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun ensureSeeded_seedsOnceAndObserves() = runTest {
        repository.ensureSeeded()
        val items = repository.observeItems().first()
        assertThat(items).isNotEmpty()

        items.forEach { repository.deleteItem(it.id) }
        repository.ensureSeeded()
        assertThat(repository.observeItems().first()).isEmpty()
    }

    @Test
    fun addUpdateDelete_roundTrip() = runTest {
        repository.ensureSeeded()
        val created = repository.addItem("New", "Notes")
        val updated = repository.updateItem(created.id, "Updated", "Changed")
        repository.deleteItem(created.id)
        assertThat(updated?.title).isEqualTo("Updated")
        assertThat(repository.getItem(created.id)).isNull()
    }
}
