package com.example.bondmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bondmap.ui.components.BondCard
import com.example.bondmap.ui.components.BondMapTopBar
import com.example.bondmap.ui.components.EmptyState
import com.example.bondmap.ui.components.ErrorState
import com.example.bondmap.ui.components.LoadingState
import com.example.bondmap.ui.theme.BondMapColors

@Composable
fun BondListScreen(
    onOpenDetails: (Long) -> Unit,
    onOpenSearch: () -> Unit,
    viewModel: BondListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BondMapColors.Canvas)
    ) {
        BondMapTopBar(
            title = "BondMap",
            subtitle = "Облигации",
            actions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = BondMapColors.TextOnNavy
                    )
                }
                IconButton(onClick = viewModel::refresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Обновить",
                        tint = BondMapColors.TextOnNavy
                    )
                }
            }
        )

        when {
            state.loading -> LoadingState()
            state.error != null -> ErrorState(
                message = state.error.orEmpty(),
                onRetry = viewModel::refresh
            )
            state.bonds.isEmpty() -> EmptyState(
                title = "Пока нет облигаций",
                subtitle = "Добавьте выпуски через API или Swagger"
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(state.bonds, key = { it.id }) { bond ->
                        BondCard(
                            name = bond.name,
                            ticker = bond.ticker,
                            currency = bond.currency,
                            price = bond.currentPrice,
                            yieldPercent = bond.currentYield,
                            maturityDate = bond.maturityDate,
                            onClick = { onOpenDetails(bond.id) }
                        )
                    }
                }
            }
        }
    }
}
