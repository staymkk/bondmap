package com.example.bondmap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bondmap.data.BondRepository
import com.example.bondmap.data.BondSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BondListUiState(
    val loading: Boolean = false,
    val bonds: List<BondSummaryDto> = emptyList(),
    val error: String? = null
)

class BondListViewModel(
    private val repository: BondRepository = BondRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(BondListUiState(loading = true))
    val state: StateFlow<BondListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = BondListUiState(loading = true)
            try {
                _state.value = BondListUiState(bonds = repository.getBondSummaries())
            } catch (e: Exception) {
                _state.value = BondListUiState(
                    error = e.message ?: "Не удалось загрузить список"
                )
            }
        }
    }
}
