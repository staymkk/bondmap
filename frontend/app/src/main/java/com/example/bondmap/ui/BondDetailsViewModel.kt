package com.example.bondmap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bondmap.data.BondAnalyticsDto
import com.example.bondmap.data.BondDetailsDto
import com.example.bondmap.data.BondRepository
import com.example.bondmap.data.ScenarioDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BondDetailsUiState(
    val loading: Boolean = false,
    val details: BondDetailsDto? = null,
    val analytics: BondAnalyticsDto? = null,
    val scenario: ScenarioDto? = null,
    val selectedShockBp: Int = 100,
    val scenarioLoading: Boolean = false,
    val error: String? = null
)

class BondDetailsViewModel(
    private val bondId: Long,
    private val repository: BondRepository = BondRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(BondDetailsUiState(loading = true))
    val state: StateFlow<BondDetailsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = BondDetailsUiState(loading = true)
            try {
                val detailsDeferred = async { repository.getDetails(bondId) }
                val analyticsDeferred = async { repository.getAnalytics(bondId) }
                val details = detailsDeferred.await()
                val analytics = analyticsDeferred.await()
                val shock = 100
                val scenario = runCatching {
                    repository.getScenario(bondId, shock)
                }.getOrNull()

                _state.value = BondDetailsUiState(
                    details = details,
                    analytics = analytics,
                    scenario = scenario,
                    selectedShockBp = shock
                )
            } catch (e: Exception) {
                _state.value = BondDetailsUiState(
                    error = e.message ?: "Не удалось загрузить детали"
                )
            }
        }
    }

    fun selectShock(shockBp: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedShockBp = shockBp,
                scenarioLoading = true
            )
            try {
                val scenario = repository.getScenario(bondId, shockBp)
                _state.value = _state.value.copy(
                    scenario = scenario,
                    scenarioLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    scenarioLoading = false
                )
            }
        }
    }
}
