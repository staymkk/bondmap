package com.example.bondmap.ui.components.neu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.components.BondCard
import com.example.bondmap.ui.components.HeroMetricsRow
import com.example.bondmap.ui.components.SearchFilterPanel
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.BondMapTheme

@Preview(showBackground = true, backgroundColor = 0xFFF0F3F6, widthDp = 390)
@Composable
private fun NeuKitPreview() {
    BondMapTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BondMapColors.Canvas)
                .padding(16.dp)
        ) {
            SearchFilterPanel(
                currency = "RUB",
                onCurrencyChange = {},
                isin = "",
                onIsinChange = {},
                name = "",
                onNameChange = {},
                maturityFrom = "",
                onMaturityFromChange = {},
                maturityTo = "",
                onMaturityToChange = {},
                minYield = "8",
                onMinYieldChange = {},
                maxYield = "15",
                onMaxYieldChange = {},
                sort = "yield_desc",
                onSortChange = {},
                loading = false,
                onSearch = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeuPill(label = "1M", selected = true, onClick = {})
                NeuPill(label = "3M", onClick = {})
                NeuPill(label = "1Y", onClick = {})
                NeuPill(label = "All", onClick = {})
            }
            Spacer(modifier = Modifier.height(16.dp))
            HeroMetricsRow(price = "98.40", yieldText = "12.15%", couponText = "8.5%")
            Spacer(modifier = Modifier.height(8.dp))
            BondCard(
                name = "ОФЗ-ПД 26243",
                ticker = "RU000A1038V6",
                currency = "RUB",
                couponRate = 14.0,
                price = 945.80,
                yieldPercent = 14.80,
                maturityDate = "2038-05-19",
                onClick = {}
            )
        }
    }
}
