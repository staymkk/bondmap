package com.example.bondmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bondmap.ui.components.BondCard
import com.example.bondmap.ui.components.BondMapTopBar
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape

@OptIn(ExperimentalMaterial3Api::class)
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.currency,
                onValueChange = viewModel::updateCurrency,
                label = { Text("Валюта") },
                placeholder = { Text("RUB") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = CardShape,
                colors = searchFieldColors()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.minYield,
                onValueChange = viewModel::updateMinYield,
                label = { Text("Мин. доходность, %") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = CardShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = searchFieldColors()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.maxYield,
                onValueChange = viewModel::updateMaxYield,
                label = { Text("Макс. доходность, %") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = CardShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = searchFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.labelLarge,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                FilterChip(
                    selected = state.sort == "yield_desc",
                    onClick = { viewModel.updateSort("yield_desc") },
                    label = { Text("Доходность ↓") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BondMapColors.AccentSoft,
                        selectedLabelColor = BondMapColors.Accent
                    )
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                FilterChip(
                    selected = state.sort == "yield_asc",
                    onClick = { viewModel.updateSort("yield_asc") },
                    label = { Text("Доходность ↑") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BondMapColors.AccentSoft,
                        selectedLabelColor = BondMapColors.Accent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.search() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading,
                shape = CardShape,
                colors = ButtonDefaults.buttonColors(containerColor = BondMapColors.Navy)
            ) {
                Text(if (state.loading) "Ищем…" else "Найти")
            }

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
                            ticker = item.ticker,
                            currency = item.currency,
                            yieldPercent = item.currentYield,
                            onClick = { onOpenDetails(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun searchFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BondMapColors.Navy,
    unfocusedBorderColor = BondMapColors.Divider,
    focusedLabelColor = BondMapColors.Navy,
    cursorColor = BondMapColors.Navy
)
