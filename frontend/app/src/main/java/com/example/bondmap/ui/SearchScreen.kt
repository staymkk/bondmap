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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bondmap.ui.components.BondCard
import com.example.bondmap.ui.components.BondMapTopBar
import com.example.bondmap.ui.components.SearchFilterPanel
import com.example.bondmap.ui.theme.BondMapColors

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenDetails: (Long) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BondMapColors.Canvas)
    ) {
        BondMapTopBar(
            title = "Поиск",
            subtitle = "Фильтры и доходность",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            SearchFilterPanel(
                currency = state.currency,
                onCurrencyChange = viewModel::updateCurrency,
                isin = state.isin,
                onIsinChange = viewModel::updateIsin,
                name = state.name,
                onNameChange = viewModel::updateName,
                maturityFrom = state.maturityFrom,
                onMaturityFromChange = viewModel::updateMaturityFrom,
                maturityTo = state.maturityTo,
                onMaturityToChange = viewModel::updateMaturityTo,
                minYield = state.minYield,
                onMinYieldChange = viewModel::updateMinYield,
                maxYield = state.maxYield,
                onMaxYieldChange = viewModel::updateMaxYield,
                sort = state.sort,
                onSortChange = viewModel::updateSort,
                loading = state.loading,
                onSearch = viewModel::search
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = BondMapColors.Danger
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when {
                state.loading -> {
                    Text(
                        "Загрузка результатов…",
                        color = BondMapColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                state.hasSearched && state.results.isEmpty() && state.error == null -> {
                    Text(
                        "Ничего не найдено. Оставьте фильтры пустыми и нажмите «Найти».",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BondMapColors.TextSecondary
                    )
                }
                else -> {
                    state.results.forEach { item ->
                        BondCard(
                            name = item.name,
                            ticker = item.displayIsin(),
                            currency = item.currency,
                            couponRate = item.couponRate,
                            price = item.currentPrice,
                            yieldPercent = item.displayYield(),
                            maturityDate = item.maturityDate,
                            onClick = { onOpenDetails(item.id) }
                        )
                    }
                }
            }
        }
    }
}
