package com.example.bondmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.ChipShape

data class CurrencyOption(val code: String, val label: String)

val CurrencyOptions = listOf(
    CurrencyOption("", "Все"),
    CurrencyOption("RUB", "₽ RUB"),
    CurrencyOption("USD", "$ USD"),
    CurrencyOption("EUR", "€ EUR")
)

@Composable
fun CurrencyDropdown(
    selectedCode: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = CurrencyOptions.find { it.code.equals(selectedCode, ignoreCase = true) }
        ?: CurrencyOptions.first()
    val density = LocalDensity.current
    var menuWidth by remember { mutableStateOf(0.dp) }
    var menuOffsetY by remember { mutableStateOf(0) }
    val shape = ChipShape
    val borderColor = if (expanded) BondMapColors.Navy else BondMapColors.Divider

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                menuWidth = with(density) { size.width.toDp() }
                menuOffsetY = size.height + with(density) { 4.dp.roundToPx() }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(BondMapColors.Surface, shape)
                .border(1.dp, borderColor, shape)
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.label,
                style = MaterialTheme.typography.bodyLarge,
                color = BondMapColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Валюта",
                tint = BondMapColors.TextSecondary
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, menuOffsetY),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(
                    focusable = true,
                    clippingEnabled = false
                )
            ) {
                Column(
                    modifier = Modifier
                        .width(menuWidth)
                        .shadow(8.dp, shape)
                        .clip(shape)
                        .background(BondMapColors.Surface, shape)
                        .border(1.dp, BondMapColors.Divider, shape)
                ) {
                    CurrencyOptions.forEach { option ->
                        val isSelected = option.code.equals(selected.code, ignoreCase = true)
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) BondMapColors.Navy else BondMapColors.TextPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(option.code)
                                    expanded = false
                                }
                                .background(
                                    if (isSelected) BondMapColors.ChipBg else BondMapColors.Surface
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
