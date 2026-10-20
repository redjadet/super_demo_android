package com.redjadet.superdemo.features.items.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redjadet.superdemo.features.items.domain.Item
import com.redjadet.superdemo.features.items.domain.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemsUiState(
    val isLoading: Boolean = true,
    val items: List<Item> = emptyList(),
    val draftTitle: String = "",
    val errorMessage: String? = null,
)

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val repository: ItemRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsUiState())
    val state: StateFlow<ItemsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.ensureSeeded() }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Seed failed")
                    }
                }
            repository.observeItems().collect { items ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        // Keep seed/mutation errors visible when the list is still empty.
                        errorMessage = if (items.isNotEmpty()) null else it.errorMessage,
                    )
                }
            }
        }
    }

    fun onDraftTitleChange(value: String) {
        _state.update {
            it.copy(
                draftTitle = value,
                errorMessage = if (value.isNotBlank()) null else it.errorMessage,
            )
        }
    }

    fun addDraft() {
        val title = _state.value.draftTitle.trim()
        if (title.isEmpty()) {
            _state.update { it.copy(errorMessage = "Title can’t be blank.") }
            return
        }
        viewModelScope.launch {
            runCatching { repository.addItem(title = title, notes = "") }
                .onSuccess { _state.update { it.copy(draftTitle = "", errorMessage = null) } }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _state.update {
                        it.copy(errorMessage = error.message ?: "Could not add item")
                    }
                }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching { repository.deleteItem(id) }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _state.update {
                        it.copy(errorMessage = error.message ?: "Could not delete item")
                    }
                }
        }
    }
}

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val item: Item? = null,
    val title: String = "",
    val notes: String = "",
    val saved: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId: Long = checkNotNull(savedStateHandle["itemId"])

    private val _state = MutableStateFlow(ItemDetailUiState())
    val state: StateFlow<ItemDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.getItem(itemId) }
                .onSuccess { item ->
                    _state.value = ItemDetailUiState(
                        isLoading = false,
                        item = item,
                        title = item?.title.orEmpty(),
                        notes = item?.notes.orEmpty(),
                    )
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _state.value = ItemDetailUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load item",
                    )
                }
        }
    }

    fun onTitleChange(value: String) {
        _state.update {
            it.copy(title = value, saved = false, errorMessage = if (value.isNotBlank()) null else it.errorMessage)
        }
    }

    fun onNotesChange(value: String) {
        _state.update { it.copy(notes = value, saved = false) }
    }

    fun save() {
        val title = _state.value.title.trim()
        if (title.isEmpty()) {
            _state.update { it.copy(errorMessage = "Title can’t be blank.", saved = false) }
            return
        }
        viewModelScope.launch {
            runCatching {
                repository.updateItem(
                    id = itemId,
                    title = title,
                    notes = _state.value.notes,
                )
            }.onSuccess { updated ->
                _state.update { it.copy(item = updated, saved = updated != null, errorMessage = null) }
            }.onFailure { error ->
                if (error is CancellationException) throw error
                _state.update {
                    it.copy(errorMessage = error.message ?: "Could not save item")
                }
            }
        }
    }
}
