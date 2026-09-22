package com.example.bondmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bondmap.ui.components.AnalyticsSection
import com.example.bondmap.ui.components.BondMapTopBar
import com.example.bondmap.ui.components.DetailParamRow
import com.example.bondmap.ui.components.ErrorState
import com.example.bondmap.ui.components.HeroMetricsRow
import com.example.bondmap.ui.components.LoadingState
import com.example.bondmap.ui.components.PriceChartRange
import com.example.bondmap.ui.components.PriceChartSection
import com.example.bondmap.ui.components.ScenarioSection
import com.example.bondmap.ui.theme.BondMapColors

@Composable
fun BondDetailsScreen(
    bondId: Long,
    onBack: () -> Unit
) {
    val viewModel: BondDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BondDetailsViewModel(bondId) as T
            }
        }
    )
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BondMapColors.Canvas)
    ) {
        BondMapTopBar(
            title = "Детали",
            subtitle = state.details?.ticker,
            navigation = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = BondMapColors.TextOnNavy
                    )
                }
            }
        )

        when {
            state.loading -> LoadingState()
            state.error != null && state.details == null -> ErrorState(
                message = state.error.orEmpty(),
                onRetry = viewModel::load
            )
            state.details != null -> DetailsBody(
                state = state,
                onSelectShock = viewModel::selectShock
            )
        }
    }
}

@Composable
private fun DetailsBody(
    state: BondDetailsUiState,
    onSelectShock: (Int) -> Unit
) {
    val details = state.details!!
    var range by rememberSaveable { mutableStateOf(PriceChartRange.ALL.name) }
    val selectedRange = PriceChartRange.entries.find { it.name == range } ?: PriceChartRange.ALL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = details.name,
            style = MaterialTheme.typography.headlineMedium,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${details.ticker} · ${details.currency}",
            style = MaterialTheme.typography.bodyMedium,
            color = BondMapColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))
        HeroMetricsRow(
            price = details.currentPrice?.let { String.format("%.2f", it) } ?: "—",
            yieldText = details.currentYield?.let { String.format("%.2f%%", it) } ?: "—",
            couponText = String.format("%.1f%%", details.couponRate)
        )

        Spacer(modifier = Modifier.height(18.dp))
        AnalyticsSection(analytics = state.analytics)

        Spacer(modifier = Modifier.height(18.dp))
        ScenarioSection(
            selectedShockBp = state.selectedShockBp,
            scenario = state.scenario,
            loading = state.scenarioLoading,
            onSelectShock = onSelectShock
        )

        Spacer(modifier = Modifier.height(18.dp))
        PriceChartSection(
            history = details.priceHistory,
            selectedRange = selectedRange,
            onRangeSelected = { range = it.name }
        )

        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Параметры выпуска",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        DetailParamRow("Номинал", details.nominal.toString())
        DetailParamRow("Валюта", details.currency)
        DetailParamRow(
            "Купонный доход / год",
            String.format("%.2f", details.annualCouponIncome)
        )

        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Последние цены",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (details.priceHistory.isEmpty()) {
            Text(
                text = "История пока пуста — добавьте цены через API",
                style = MaterialTheme.typography.bodyMedium,
                color = BondMapColors.TextSecondary
            )
        } else {
            details.priceHistory
                .sortedByDescending { it.date }
                .take(8)
                .forEach { point ->
                    DetailParamRow(point.date, String.format("%.2f", point.price))
                }
        }
    }
}
