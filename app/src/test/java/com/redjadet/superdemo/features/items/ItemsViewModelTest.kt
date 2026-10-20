package com.redjadet.superdemo.features.items

import com.google.common.truth.Truth.assertThat
import com.redjadet.superdemo.features.items.domain.Item
import com.redjadet.superdemo.features.items.domain.ItemRepository
import com.redjadet.superdemo.features.items.presentation.ItemsViewModel
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
class ItemsViewModelTest {

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
    fun addDraft_blankTitle_setsValidationError() = runTest(dispatcher) {
        val viewModel = ItemsViewModel(FakeItemRepository())
        advanceUntilIdle()
        viewModel.addDraft()
        advanceUntilIdle()
        assertThat(viewModel.state.value.errorMessage).isEqualTo("Title can’t be blank.")
        assertThat(viewModel.state.value.items).isEmpty()
    }

    @Test
    fun addDraft_validTitle_clearsErrorAndAddsItem() = runTest(dispatcher) {
        val repository = FakeItemRepository()
        val viewModel = ItemsViewModel(repository)
        advanceUntilIdle()
        viewModel.onDraftTitleChange("Notebook")
        viewModel.addDraft()
        advanceUntilIdle()
        assertThat(viewModel.state.value.errorMessage).isNull()
        assertThat(viewModel.state.value.draftTitle).isEmpty()
        assertThat(viewModel.state.value.items.map { it.title }).contains("Notebook")
    }

    private class FakeItemRepository : ItemRepository {
        private val items = MutableStateFlow<List<Item>>(emptyList())
        private var nextId = 1L

        override fun observeItems(): Flow<List<Item>> = items

        override suspend fun ensureSeeded() = Unit

        override suspend fun getItem(id: Long): Item? = items.value.firstOrNull { it.id == id }

        override suspend fun addItem(title: String, notes: String): Item {
            val item = Item(
                id = nextId++,
                title = title,
                notes = notes,
                createdAtEpochMs = 0L,
            )
            items.value = listOf(item) + items.value
            return item
        }

        override suspend fun updateItem(id: Long, title: String, notes: String): Item? = null

        override suspend fun deleteItem(id: Long) {
            items.value = items.value.filterNot { it.id == id }
        }
    }
}
