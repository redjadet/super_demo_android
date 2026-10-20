package com.redjadet.superdemo.features.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redjadet.superdemo.features.feed.domain.FeedDisplayError
import com.redjadet.superdemo.features.feed.domain.FeedRepository
import com.redjadet.superdemo.features.feed.domain.RefreshFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val isLoading: Boolean = false,
    val posts: List<com.redjadet.superdemo.features.feed.domain.FeedPost> = emptyList(),
    val isStale: Boolean = false,
    val showingLocalOnly: Boolean = false,
    val error: FeedDisplayError? = null,
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val refreshFeed: RefreshFeedUseCase,
    private val repository: FeedRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeededForOffline()
            repository.observePosts().collect { posts ->
                _state.update { current ->
                    current.copy(
                        posts = posts,
                        error = when {
                            posts.isNotEmpty() -> null
                            current.isLoading -> null
                            else -> FeedDisplayError.Empty
                        },
                    )
                }
            }
        }
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = refreshFeed()
                // Posts stay Room-driven via observePosts(); only sync metadata here.
                _state.update {
                    val empty = it.posts.isEmpty()
                    it.copy(
                        isLoading = false,
                        isStale = result.isStale,
                        showingLocalOnly = result.fromCache,
                        error = when {
                            !empty -> null
                            // Offline/sync failure with empty Room → network error.
                            result.fromCache || result.isStale -> FeedDisplayError.Network
                            // Successful remote empty → empty state (not a network failure).
                            else -> FeedDisplayError.Empty
                        },
                    )
                }
            } catch (cancelled: CancellationException) {
                _state.update { it.copy(isLoading = false) }
                throw cancelled
            } catch (_: Exception) {
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        isStale = current.posts.isNotEmpty(),
                        showingLocalOnly = current.posts.isNotEmpty(),
                        error = if (current.posts.isEmpty()) FeedDisplayError.Network else null,
                    )
                }
            }
        }
    }

    fun toggleBookmark(postId: Int) {
        viewModelScope.launch {
            runCatching { repository.toggleBookmark(postId) }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                }
        }
    }
}
