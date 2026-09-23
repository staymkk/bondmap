package com.example.bondmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.components.neu.NeuPill
import com.example.bondmap.ui.components.neu.NeuSearchField
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.PanelShape

@Composable
fun SearchFilterPanel(
    currency: String,
    onCurrencyChange: (String) -> Unit,
    isin: String,
    onIsinChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    maturityFrom: String,
    onMaturityFromChange: (String) -> Unit,
    maturityTo: String,
    onMaturityToChange: (String) -> Unit,
    minYield: String,
    onMinYieldChange: (String) -> Unit,
    maxYield: String,
    onMaxYieldChange: (String) -> Unit,
    sort: String,
    onSortChange: (String) -> Unit,
    loading: Boolean,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeuSurface(
        modifier = modifier.fillMaxWidth(),
        shape = PanelShape
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Валюта",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            CurrencyDropdown(
                selectedCode = currency,
                onSelect = onCurrencyChange
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ISIN",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            NeuSearchField(
                value = isin,
                onValueChange = onIsinChange,
                placeholder = "Например, RU000A000000"
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Название",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            NeuSearchField(
                value = name,
                onValueChange = onNameChange,
                placeholder = "Например, ОФЗ-ПД 26243"
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Дата погашения",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "от",
                        style = MaterialTheme.typography.labelSmall,
                        color = BondMapColors.TextSecondary
                    )
                    NeuSearchField(
                        value = maturityFrom,
                        onValueChange = onMaturityFromChange,
                        placeholder = "01.01.2027"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "до",
                        style = MaterialTheme.typography.labelSmall,
                        color = BondMapColors.TextSecondary
                    )
                    NeuSearchField(
                        value = maturityTo,
                        onValueChange = onMaturityToChange,
                        placeholder = "31.12.2038"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Доходность, %",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeuSearchField(
                    value = minYield,
                    onValueChange = onMinYieldChange,
                    placeholder = "Мин.",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                NeuSearchField(
                    value = maxYield,
                    onValueChange = onMaxYieldChange,
                    placeholder = "Макс.",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Сортировка",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeuPill(
                    label = "Доходность ↓",
                    selected = sort == "yield_desc",
                    onClick = { onSortChange("yield_desc") }
                )
                NeuPill(
                    label = "Доходность ↑",
                    selected = sort == "yield_asc",
                    onClick = { onSortChange("yield_asc") }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            NeuPill(
                label = if (loading) "Ищем…" else "Найти",
                modifier = Modifier.fillMaxWidth(),
                selected = true,
                enabled = !loading,
                onClick = onSearch,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            )
        }
    }
}
