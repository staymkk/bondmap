package com.example.bondmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bondmap.data.BondAnalyticsDto
import com.example.bondmap.data.ScenarioDto
import com.example.bondmap.ui.components.neu.NeuPill
import com.example.bondmap.ui.components.neu.NeuPillRow
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.formatPercent
import com.example.bondmap.ui.formatSignedPp
import com.example.bondmap.ui.theme.BondMapColors

@Composable
fun AnalyticsSection(analytics: BondAnalyticsDto?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Рыночный контекст",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (analytics == null) {
            NeuSurface(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Аналитика недоступна",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BondMapColors.TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return
        }

        val spread = analytics.displaySpread()
        val spreadPositive = (spread ?: 0.0) > 0
        val spreadNegative = (spread ?: 0.0) < 0

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticMetric(
                label = "Ключевая ставка",
                value = formatPercent(analytics.displayBaseRate(), 1),
                caption = analytics.baseRateDate ?: analytics.keyRateDate,
                hint = TermHints.baseRate,
                modifier = Modifier.weight(1f)
            )
            AnalyticMetric(
                label = "Спред",
                value = formatSignedPp(spread),
                caption = "yield − key rate",
                hint = TermHints.spread,
                accent = spreadPositive,
                danger = spreadNegative,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScenarioSection(
    selectedShockBp: Int,
    scenario: ScenarioDto?,
    loading: Boolean,
    onSelectShock: (Int) -> Unit
) {
    val shocks = listOf(-100, -50, 50, 100)

    NeuSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Сценарий ставки",
                    style = MaterialTheme.typography.titleLarge,
                    color = BondMapColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                InfoHintButton(hint = TermHints.scenario)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Оценка влияния шока доходности на цену",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.Navy
            )
            Spacer(modifier = Modifier.height(8.dp))

            NeuPillRow {
                shocks.forEach { bp ->
                    NeuPill(
                        label = if (bp > 0) "+$bp б.п." else "$bp б.п.",
                        modifier = Modifier.weight(1f),
                        selected = selectedShockBp == bp,
                        onClick = { onSelectShock(bp) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Новая цена: " +
                                (scenario.estimatedNewPrice?.let { String.format("%.2f", it) } ?: "—"),
                            style = MaterialTheme.typography.headlineSmall,
                            color = BondMapColors.TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        InfoHintButton(hint = TermHints.estimatedPrice)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Изменение: " +
                                (scenario.estimatedPriceChange?.let { String.format("%+.2f", it) } ?: "—") +
                                " (" +
                                (scenario.estimatedPriceChangePercent?.let { String.format("%+.2f%%", it) } ?: "—") +
                                ")",
                            style = MaterialTheme.typography.titleMedium,
                            color = changeColor,
                            modifier = Modifier.weight(1f)
                        )
                        InfoHintButton(hint = TermHints.priceChange)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scenario.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = BondMapColors.Navy
                    )
                }
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
    hint: TermHint? = null,
    accent: Boolean = false,
    danger: Boolean = false
) {
    val bg = when {
        danger -> BondMapColors.DangerSoft
        accent -> BondMapColors.AccentSoft
        else -> BondMapColors.SurfaceNeu
    }
    val valueColor = when {
        danger -> BondMapColors.Danger
        accent -> BondMapColors.Accent
        else -> BondMapColors.TextPrimary
    }
    NeuSurface(
        modifier = modifier,
        color = bg
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (hint != null) {
                LabelWithHint(label = label, hint = hint)
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = BondMapColors.TextSecondary
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor
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
}
