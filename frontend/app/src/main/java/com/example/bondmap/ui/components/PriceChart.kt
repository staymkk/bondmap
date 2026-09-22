package com.example.bondmap.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.bondmap.data.PricePointDto
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlin.math.max

enum class PriceChartRange(val label: String, val days: Int?) {
    MONTH("1M", 30),
    QUARTER("3M", 90),
    YEAR("1Y", 365),
    ALL("All", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceChartSection(
    history: List<PricePointDto>,
    selectedRange: PriceChartRange,
    onRangeSelected: (PriceChartRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val filtered = remember(history, selectedRange) {
        filterHistory(history, selectedRange)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BondMapColors.Surface, CardShape)
            .padding(16.dp)
    ) {
        Text(
            text = "Динамика цены",
            style = MaterialTheme.typography.titleLarge,
            color = BondMapColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PriceChartRange.entries.forEach { range ->
                FilterChip(
                    selected = selectedRange == range,
                    onClick = { onRangeSelected(range) },
                    label = { Text(range.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BondMapColors.AccentSoft,
                        selectedLabelColor = BondMapColors.Accent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filtered.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(BondMapColors.Canvas, CardShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = if (history.isEmpty()) {
                        "Нет данных для графика"
                    } else {
                        "Мало точек за выбранный период"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = BondMapColors.TextSecondary
                )
            }
        } else {
            val first = filtered.first()
            val last = filtered.last()
            val delta = last.price - first.price
            val deltaPct = if (first.price != 0.0) delta / first.price * 100 else 0.0
            val up = delta >= 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%.2f", last.price),
                    style = MaterialTheme.typography.headlineSmall,
                    color = BondMapColors.TextPrimary
                )
                Text(
                    text = String.format("%+.2f (%+.2f%%)", delta, deltaPct),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (up) BondMapColors.Accent else BondMapColors.Danger
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${first.date} → ${last.date}",
                style = MaterialTheme.typography.labelMedium,
                color = BondMapColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            PriceChart(
                points = filtered,
                lineColor = if (up) BondMapColors.Accent else BondMapColors.Danger,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}

@Composable
fun PriceChart(
    points: List<PricePointDto>,
    lineColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val prices = points.map { it.price }
    val minP = prices.minOrNull() ?: 0.0
    val maxP = prices.maxOrNull() ?: 1.0
    val span = max(maxP - minP, 0.0001)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padY = 8.dp.toPx()
        val usableH = h - padY * 2
        val stepX = if (points.size == 1) 0f else w / (points.size - 1)

        // grid
        val gridEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        for (i in 0..3) {
            val y = padY + usableH * i / 3f
            drawLine(
                color = BondMapColors.Divider,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = gridEffect
            )
        }

        fun yFor(price: Double): Float {
            val t = ((price - minP) / span).toFloat()
            return padY + usableH * (1f - t)
        }

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = stepX * index
            val y = yFor(point.price)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // end dot
        val last = points.last()
        drawCircle(
            color = lineColor,
            radius = 5.dp.toPx(),
            center = Offset(stepX * (points.size - 1), yFor(last.price))
        )
    }
}

fun filterHistory(
    history: List<PricePointDto>,
    range: PriceChartRange
): List<PricePointDto> {
    val sorted = history.sortedBy { parseDate(it.date) ?: LocalDate.MIN }
    if (range.days == null || sorted.isEmpty()) return sorted

    val latest = sorted.mapNotNull { parseDate(it.date) }.maxOrNull() ?: return sorted
    val from = latest.minusDays(range.days.toLong())
    return sorted.filter { point ->
        val d = parseDate(point.date) ?: return@filter false
        !d.isBefore(from)
    }
}

private fun parseDate(value: String): LocalDate? =
    try {
        LocalDate.parse(value.take(10))
    } catch (_: DateTimeParseException) {
        null
    }
