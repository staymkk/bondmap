package com.example.bondmap.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bondmap.ui.components.neu.NeuPill
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape

data class TermHint(
    val title: String,
    val body: AnnotatedString,
    val formula: String? = null
)

private val primary = SpanStyle(color = BondMapColors.TextPrimary)
private val strong = SpanStyle(
    color = BondMapColors.Navy,
    fontWeight = FontWeight.Bold
)
private val emph = SpanStyle(
    color = BondMapColors.Navy,
    fontStyle = FontStyle.Italic
)

private fun hintText(block: AnnotatedString.Builder.() -> Unit): AnnotatedString =
    buildAnnotatedString {
        withStyle(primary, block)
    }

private fun AnnotatedString.Builder.strong(text: String) {
    withStyle(strong) { append(text) }
}

private fun AnnotatedString.Builder.emph(text: String) {
    withStyle(emph) { append(text) }
}

object TermHints {
    val ytm = TermHint(
        title = "Доходность к погашению (YTM)",
        body = hintText {
            append("Показывает ожидаемую годовую доходность при покупке облигации по текущей цене и удержании до даты погашения.\n\n")
            strong("Учитывает:\n")
            append("• текущую цену;\n• купоны;\n• номинал;\n• срок до погашения.\n\n")
            emph("Купон ≠ доходность: ")
            append("купон — фиксированная выплата, доходность зависит от цены покупки.")
        },
        formula = "P = C₁/(1+y)^t₁ + … + (Cₙ+N)/(1+y)^tₙ"
    )
    val coupon = TermHint(
        title = "Купон",
        body = hintText {
            strong("Купон")
            append(" — фиксированная выплата по облигации. ")
            emph("Доходность")
            append(" учитывает текущую цену покупки и будущие денежные потоки.")
        }
    )
    val baseRate = TermHint(
        title = "Базовая ставка рынка",
        body = hintText {
            append("Рыночный ориентир для сравнения. ")
            strong("Базовая ставка не используется напрямую в расчёте цены")
            append(" облигации — только помогает понять положение выпуска относительно рынка.")
        }
    )
    val spread = TermHint(
        title = "Спред",
        body = hintText {
            append("Разница между доходностью облигации и базовой ставкой рынка. ")
            emph("Цветовая индикация показывает только разницу между показателями")
            append(" и не является оценкой привлекательности облигации.")
        },
        formula = "Spread = YTM − Base Rate"
    )
    val duration = TermHint(
        title = "Модифицированная дюрация",
        body = hintText {
            append("Показывает чувствительность цены облигации к изменению доходности.\n\n")
            append("При изменении доходности на ")
            strong("1 п.п.")
            append(" цена изменится примерно на ")
            emph("D%")
            append(".")
        },
        formula = "ΔP / P ≈ −D_mod × Δy"
    )
    val convexity = TermHint(
        title = "Выпуклость облигации",
        body = hintText {
            append("Повышает точность больших сценариев и учитывает нелинейность изменения цены. ")
            emph("В учебной модели сценарии MVP считаются через дюрацию.")
        },
        formula = "ΔP / P ≈ −D Δy + ½ C (Δy)²"
    )
    val scenario = TermHint(
        title = "Сценарий изменения доходности",
        body = hintText {
            append("Оценка изменения цены при изменении требуемой доходности облигации. ")
            strong("Это учебная модель «что может быть», а не прогноз. ")
            emph("Историческая динамика цены к этому сценарию не привязана.")
        },
        formula = "ΔP ≈ −D_mod × Δy × P"
    )
    val estimatedPrice = TermHint(
        title = "Оценочная цена",
        body = hintText {
            append("Новая цена, полученная из текущей игровой котировки и модифицированной дюрации. ")
            emph("Это приближение, а не рыночная котировка.")
        }
    )
    val priceChange = TermHint(
        title = "Изменение цены",
        body = hintText {
            append("Разница между оценочной ценой сценария и текущей игровой ценой.")
        }
    )
    val simulated = TermHint(
        title = "Симулированные данные",
        body = hintText {
            append("Реальные названия и ISIN используются только для идентификации инструментов. ")
            strong("Рыночные котировки, цены и часть показателей — игровые")
            append(" и не отражают текущий рынок.")
        }
    )
    val yieldColor = TermHint(
        title = "Цветовая индикация",
        body = hintText {
            append("Цвет показывает только математическое положение доходности относительно базовой ставки и ")
            emph("не является инвестиционной рекомендацией.")
        }
    )
}

@Composable
fun InfoHintButton(
    hint: TermHint,
    modifier: Modifier = Modifier,
    contentDescription: String = hint.title,
    tint: androidx.compose.ui.graphics.Color = BondMapColors.TextSecondary
) {
    var open by remember { mutableStateOf(false) }
    IconButton(
        onClick = { open = true },
        modifier = modifier.size(24.dp)
    ) {
        Icon(
            imageVector = AppIcons.Info,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
    if (open) {
        Dialog(
            onDismissRequest = { open = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            NeuSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = CardShape,
                color = BondMapColors.Surface,
                dropShadow = false
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = hint.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BondMapColors.Navy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = hint.body,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (hint.formula != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Формула",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = BondMapColors.Navy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = hint.formula,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium
                            ),
                            color = BondMapColors.Navy
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    NeuPill(
                        label = "Понятно",
                        modifier = Modifier.fillMaxWidth(),
                        selected = true,
                        onClick = { open = false },
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LabelWithHint(
    label: String,
    hint: TermHint,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = BondMapColors.Navy
        )
        InfoHintButton(hint = hint, contentDescription = label)
    }
}
