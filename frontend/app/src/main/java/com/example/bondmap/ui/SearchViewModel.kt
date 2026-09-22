package com.example.bondmap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bondmap.data.BondRepository
import com.example.bondmap.data.BondSearchDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val loading: Boolean = false,
    val currency: String = "",
    val minYield: String = "",
    val maxYield: String = "",
    val sort: String = "yield_desc",
    val results: List<BondSearchDto> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

class SearchViewModel(
    private val repository: BondRepository = BondRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun updateCurrency(value: String) {
        _state.value = _state.value.copy(currency = value)
    }

    fun updateMinYield(value: String) {
        _state.value = _state.value.copy(minYield = value)
    }

    fun updateMaxYield(value: String) {
        _state.value = _state.value.copy(maxYield = value)
    }

    fun updateSort(value: String) {
        _state.value = _state.value.copy(sort = value)
    }

    fun search() {
        viewModelScope.launch {
            val current = _state.value
            _state.value = current.copy(loading = true, error = null)
            try {
                val results = repository.search(
                    currency = current.currency.trim().ifBlank { null },
                    minYield = current.minYield.trim().toDoubleOrNull(),
                    maxYield = current.maxYield.trim().toDoubleOrNull(),
                    sort = current.sort.ifBlank { null }
                )
                _state.value = _state.value.copy(
                    loading = false,
                    results = results,
                    hasSearched = true,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    results = emptyList(),
                    hasSearched = true,
                    error = e.message ?: e::class.java.simpleName
                )
            }
        }
    }
}
