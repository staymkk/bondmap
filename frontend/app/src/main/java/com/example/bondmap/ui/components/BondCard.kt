package com.example.bondmap.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.components.neu.NeuPill
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.formatNumber
import com.example.bondmap.ui.formatPercent
import com.example.bondmap.ui.theme.BondMapColors

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

    NeuSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        pressed = pressed
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
                val flag = currencyFlagColors(currency)
                NeuPill(
                    label = currency,
                    containerColor = flag.background,
                    contentColorOverride = flag.content,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when {
                    price != null -> MetricTiny(
                        label = "Цена",
                        value = formatNumber(price)
                    )
                    couponRate != null -> MetricTiny(
                        label = "Купон",
                        value = formatPercent(couponRate, 1)
                    )
                }
                MetricTiny(
                    label = "Доходность",
                    value = formatPercent(yieldPercent),
                    accent = yieldPercent != null
                )
                if (maturityDate != null) {
                    MetricTiny(label = "Погашение", value = maturityDate.take(4))
                }
            }
        }
    }
}

private data class CurrencyFlagColors(val background: Color, val content: Color)

private fun currencyFlagColors(code: String): CurrencyFlagColors = when (code.uppercase()) {
    "RUB" -> CurrencyFlagColors(BondMapColors.FlagRub, Color.White)
    "USD" -> CurrencyFlagColors(BondMapColors.FlagUsd, Color.White)
    "EUR" -> CurrencyFlagColors(BondMapColors.FlagEur, BondMapColors.FlagEurGold)
    else -> CurrencyFlagColors(BondMapColors.SurfaceNeu, BondMapColors.TextPrimary)
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
