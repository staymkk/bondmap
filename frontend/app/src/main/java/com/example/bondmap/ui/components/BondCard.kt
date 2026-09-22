package com.example.bondmap.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape
import com.example.bondmap.ui.theme.ChipShape

@Composable
fun BondCard(
    name: String,
    ticker: String,
    currency: String,
    couponRate: Double? = null,
    price: Double? = null,
    yieldPercent: Double? = null,
    maturityDate: String? = null,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "cardScale")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        shape = CardShape,
        color = BondMapColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = BondMapColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ticker,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BondMapColors.TextSecondary
                    )
                }
                CurrencyChip(currency)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when {
                    price != null -> MetricTiny(
                        label = "Цена",
                        value = String.format("%.2f", price)
                    )
                    couponRate != null -> MetricTiny(
                        label = "Купон",
                        value = String.format("%.1f%%", couponRate)
                    )
                }
                MetricTiny(
                    label = "Доходность",
                    value = yieldPercent?.let { String.format("%.2f%%", it) } ?: "—",
                    accent = yieldPercent != null
                )
                if (maturityDate != null) {
                    MetricTiny(label = "Погашение", value = maturityDate.take(4))
                }
            }
        }
    }
}

@Composable
private fun CurrencyChip(currency: String) {
    Text(
        text = currency,
        style = MaterialTheme.typography.labelMedium,
        color = BondMapColors.Navy,
        modifier = Modifier
            .background(BondMapColors.ChipBg, ChipShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun MetricTiny(
    label: String,
    value: String,
    accent: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BondMapColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = if (accent) BondMapColors.Accent else BondMapColors.TextPrimary
        )
    }
}
