package com.redjadet.superdemo.features.items

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.features.items.domain.Item
import com.redjadet.superdemo.features.items.domain.ItemRepository
import com.redjadet.superdemo.features.items.presentation.ItemDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun save_blankTitle_preservesStoredItem_thenValidEditClearsError() = runTest(dispatcher) {
        val repository = FakeItemRepository()
        val viewModel = ItemDetailViewModel(repository, SavedStateHandle(mapOf("itemId" to 1L)))
        advanceUntilIdle()

        viewModel.onTitleChange("   ")
        viewModel.save()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo("Title can’t be blank.")
        assertThat(viewModel.state.value.saved).isFalse()
        assertThat(repository.item.title).isEqualTo("Notebook")
        assertThat(repository.updateCount).isEqualTo(0)

        viewModel.onTitleChange("Travel notes")
        viewModel.save()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isNull()
        assertThat(viewModel.state.value.saved).isTrue()
        assertThat(repository.item.title).isEqualTo("Travel notes")
        assertThat(repository.updateCount).isEqualTo(1)
    }

    private class FakeItemRepository : ItemRepository {
        var item = Item(1L, "Notebook", "Packing list", 0L)
        var updateCount = 0

        override fun observeItems() = flowOf(listOf(item))
        override suspend fun ensureSeeded() = Unit
        override suspend fun getItem(id: Long) = item.takeIf { it.id == id }
        override suspend fun addItem(title: String, notes: String): Item = error("Not used")
        override suspend fun deleteItem(id: Long) = Unit
        override suspend fun updateItem(id: Long, title: String, notes: String): Item {
            updateCount++
            item = item.copy(title = title, notes = notes)
            return item
        }
    }
}
