package com.example.bondmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bondmap.data.BondAnalyticsDto
import com.example.bondmap.data.ScenarioDto
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape

@Composable
fun AnalyticsSection(analytics: BondAnalyticsDto?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BondMapColors.Surface, CardShape)
            .padding(16.dp)
    ) {
        Text(
            text = "Рыночный контекст",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (analytics == null) {
            Text(
                text = "Аналитика недоступна",
                style = MaterialTheme.typography.bodyMedium,
                color = BondMapColors.TextSecondary
            )
            return
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticMetric(
                label = "Ключевая ставка",
                value = analytics.keyRate?.let { String.format("%.1f%%", it) } ?: "—",
                caption = analytics.keyRateDate,
                modifier = Modifier.weight(1f)
            )
            AnalyticMetric(
                label = "Спред",
                value = analytics.spreadToKeyRate?.let { String.format("%+.2f п.п.", it) } ?: "—",
                caption = "yield − key rate",
                accent = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioSection(
    selectedShockBp: Int,
    scenario: ScenarioDto?,
    loading: Boolean,
    onSelectShock: (Int) -> Unit
) {
    val shocks = listOf(-100, -50, 50, 100, 200)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BondMapColors.Surface, CardShape)
            .padding(16.dp)
    ) {
        Text(
            text = "Сценарий ставки",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Оценка влияния шока доходности на цену",
            style = MaterialTheme.typography.labelMedium,
            color = BondMapColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            shocks.forEach { bp ->
                FilterChip(
                    selected = selectedShockBp == bp,
                    onClick = { onSelectShock(bp) },
                    label = {
                        Text(if (bp > 0) "+$bp б.п." else "$bp б.п.")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BondMapColors.AccentSoft,
                        selectedLabelColor = BondMapColors.Accent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when {
            loading -> Text(
                "Считаем сценарий…",
                color = BondMapColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            scenario == null -> Text(
                "Недостаточно данных (нужны цена и дата погашения)",
                color = BondMapColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            else -> {
                val changeColor = when {
                    (scenario.estimatedPriceChange ?: 0.0) >= 0 -> BondMapColors.Accent
                    else -> BondMapColors.Danger
                }
                Text(
                    text = "Новая цена: " +
                        (scenario.estimatedNewPrice?.let { String.format("%.2f", it) } ?: "—"),
                    style = MaterialTheme.typography.headlineSmall,
                    color = BondMapColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Изменение: " +
                        (scenario.estimatedPriceChange?.let { String.format("%+.2f", it) } ?: "—") +
                        " (" +
                        (scenario.estimatedPriceChangePercent?.let { String.format("%+.2f%%", it) } ?: "—") +
                        ")",
                    style = MaterialTheme.typography.titleMedium,
                    color = changeColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = scenario.note,
                    style = MaterialTheme.typography.labelSmall,
                    color = BondMapColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun AnalyticMetric(
    label: String,
    value: String,
    caption: String?,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    Column(
        modifier = modifier
            .background(
                if (accent) BondMapColors.AccentSoft else BondMapColors.Canvas,
                CardShape
            )
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BondMapColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = if (accent) BondMapColors.Accent else BondMapColors.TextPrimary
        )
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = BondMapColors.TextSecondary
            )
        }
    }
}
