package com.redjadet.superdemo.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redjadet.superdemo.features.dashboard.domain.DashboardRepository
import com.redjadet.superdemo.features.dashboard.domain.DashboardSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val snapshot: DashboardSnapshot? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = DashboardUiState(isLoading = true)
            runCatching { repository.load() }
                .onSuccess { snapshot ->
                    _state.value = DashboardUiState(isLoading = false, snapshot = snapshot)
                }
                .onFailure { error ->
                    _state.value = DashboardUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load dashboard",
                    )
                }
        }
    }
}
