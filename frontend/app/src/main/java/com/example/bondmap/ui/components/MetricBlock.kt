package com.example.bondmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.theme.BondMapColors

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
            hint = TermHints.simulated,
            modifier = Modifier.weight(1f)
        )
        HeroMetric(
            label = "Доходность",
            value = yieldText,
            hint = TermHints.ytm,
            accent = true,
            modifier = Modifier.weight(1f)
        )
        HeroMetric(
            label = "Купон",
            value = couponText,
            hint = TermHints.coupon,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    hint: TermHint? = null,
    accent: Boolean = false
) {
    NeuSurface(modifier = modifier) {
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
                color = if (accent) BondMapColors.Accent else BondMapColors.TextPrimary
            )
        }
    }
}

@Composable
fun DetailParamRow(label: String, value: String) {
    NeuSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
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
}
