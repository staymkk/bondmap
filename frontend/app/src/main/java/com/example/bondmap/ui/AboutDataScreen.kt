package com.example.bondmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.components.AppBarIcon
import com.example.bondmap.ui.components.AppIcons
import com.example.bondmap.ui.components.BondMapTopBar
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.theme.BondMapColors

@Composable
fun AboutDataScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BondMapColors.Canvas)
    ) {
        BondMapTopBar(
            title = "О данных",
            subtitle = "Учебный режим · игровые котировки",
            navigation = {
                AppBarIcon(
                    imageVector = AppIcons.Back,
                    contentDescription = "Назад",
                    onClick = onBack
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            NeuSurface {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BondMapColors.Navy)) {
                                append("BondMap")
                            }
                            withStyle(SpanStyle(color = BondMapColors.TextPrimary)) {
                                append(" — учебное приложение для изучения анализа облигаций.")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = BondMapColors.TextPrimary)) {
                                append("Реальные названия и ISIN")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BondMapColors.Navy)) {
                                append(" используются только для идентификации финансовых инструментов.")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = BondMapColors.TextPrimary)) {
                                append(" Рыночные котировки, цены и некоторые показатели являются")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BondMapColors.Navy)) {
                                append(" симулированными и не отражают текущие значения рынка.")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = BondMapColors.TextPrimary)) {
                                append("Расчёты предназначены для образовательных целей и ")
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Medium,
                                    color = BondMapColors.Navy
                                )
                            ) {
                                append("не являются инвестиционной рекомендацией.")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle( color = BondMapColors.TextPrimary)) {
                                append("Цвет доходности и спреда показывает только разницу с базовой ставкой, а не привлекательность бумаги.")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
