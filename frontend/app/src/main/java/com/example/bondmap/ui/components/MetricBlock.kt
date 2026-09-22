package com.example.bondmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape

@Composable
fun HeroMetricsRow(
    price: String,
    yieldText: String,
    couponText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HeroMetric(
            label = "Цена",
            value = price,
            modifier = Modifier.weight(1f)
        )
        HeroMetric(
            label = "Доходность",
            value = yieldText,
            accent = true,
            modifier = Modifier.weight(1f)
        )
        HeroMetric(
            label = "Купон",
            value = couponText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = if (accent) BondMapColors.AccentSoft else BondMapColors.Surface,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
        }
    }
}

@Composable
fun DetailParamRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(BondMapColors.Surface, CardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = BondMapColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = BondMapColors.TextPrimary
        )
    }
}
