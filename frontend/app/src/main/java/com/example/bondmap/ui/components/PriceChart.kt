package com.example.bondmap.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bondmap.data.PricePointDto
import com.example.bondmap.ui.components.neu.NeuPill
import com.example.bondmap.ui.components.neu.NeuPillRow
import com.example.bondmap.ui.components.neu.NeuSurface
import com.example.bondmap.ui.formatChartDate
import com.example.bondmap.ui.formatDate
import com.example.bondmap.ui.formatNumber
import com.example.bondmap.ui.parseDate
import com.example.bondmap.ui.theme.BondMapColors
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

private const val DefaultVisibleCandles = 60f
private const val MinVisibleCandles = 12f

enum class PriceChartRange(val label: String, val days: Int?) {
    MONTH("1M", 30),
    QUARTER("3M", 90),
    YEAR("1Y", 365),
    ALL("All", null)
}

private enum class PriceChartStyle { CANDLE, LINE }

@Composable
fun PriceChartSection(
    history: List<PricePointDto>,
    selectedRange: PriceChartRange,
    onRangeSelected: (PriceChartRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var styleName by rememberSaveable { mutableStateOf(PriceChartStyle.CANDLE.name) }
    val style = PriceChartStyle.entries.find { it.name == styleName } ?: PriceChartStyle.CANDLE
    val filtered = remember(history, selectedRange) {
        filterHistory(history, selectedRange)
    }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val startIndex = remember { mutableFloatStateOf(0f) }
    val visibleCount = remember { mutableFloatStateOf(DefaultVisibleCandles) }

    LaunchedEffect(filtered.size, selectedRange, styleName) {
        selectedIndex = null
        resetViewport(startIndex, visibleCount, filtered.size)
    }

    NeuSurface(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Динамика цены",
                style = MaterialTheme.typography.titleLarge,
                color = BondMapColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            NeuPillRow {
                PriceChartRange.entries.forEach { range ->
                    NeuPill(
                        label = range.label,
                        modifier = Modifier.weight(1f),
                        selected = selectedRange == range,
                        onClick = { onRangeSelected(range) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val minPoints = if (style == PriceChartStyle.CANDLE) 1 else 2
            if (filtered.size < minPoints) {
                NeuSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    inset = true
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
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
                }
            } else {
                val viewPoints = slicePoints(filtered, startIndex.floatValue, visibleCount.floatValue)
                val first = filtered.first()
                val last = filtered.last()
                val firstPrice = first.closePrice()
                val lastPrice = last.closePrice()
                val delta = lastPrice - firstPrice
                val deltaPct = if (firstPrice != 0.0) delta / firstPrice * 100 else 0.0
                val up = delta >= 0
                val axisFirst = viewPoints.first()
                val axisLast = viewPoints.last()
                val axisMid = viewPoints[viewPoints.size / 2]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatNumber(lastPrice),
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
                    text = "${formatDate(first.date)} → ${formatDate(last.date)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = BondMapColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    PriceChart(
                        allPoints = filtered,
                        startIndex = startIndex,
                        visibleCount = visibleCount,
                        candles = style == PriceChartStyle.CANDLE,
                        lineColor = if (up) BondMapColors.Accent else BondMapColors.Danger,
                        selectedIndex = selectedIndex,
                        onSelectIndex = { selectedIndex = it },
                        onClearSelection = { selectedIndex = null },
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            styleName = if (style == PriceChartStyle.CANDLE) {
                                PriceChartStyle.LINE.name
                            } else {
                                PriceChartStyle.CANDLE.name
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                            .size(32.dp)
                            .background(BondMapColors.SurfaceNeu.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (style == PriceChartStyle.CANDLE) {
                                AppIcons.ChartLine
                            } else {
                                AppIcons.ChartCandle
                            },
                            contentDescription = if (style == PriceChartStyle.CANDLE) {
                                "Линейный график"
                            } else {
                                "Свечной график"
                            },
                            tint = BondMapColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, end = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatChartDate(axisFirst.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = BondMapColors.Navy
                    )
                    if (viewPoints.size > 2) {
                        Text(
                            text = formatChartDate(axisMid.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = BondMapColors.Navy
                        )
                    }
                    Text(
                        text = formatChartDate(axisLast.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = BondMapColors.Navy
                    )
                }
            }
        }
    }
}

@Composable
fun PriceChart(
    allPoints: List<PricePointDto>,
    startIndex: MutableFloatState,
    visibleCount: MutableFloatState,
    candles: Boolean,
    lineColor: androidx.compose.ui.graphics.Color,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val points = slicePoints(allPoints, startIndex.floatValue, visibleCount.floatValue)
    val highs = points.mapIndexed { index, point ->
        val prev = points.getOrNull(index - 1)?.closePrice()
            ?: allPoints.getOrNull(startIndex.floatValue.roundToInt() + index - 1)?.closePrice()
        point.highPrice().coerceAtLeast(point.openPrice(prev))
    }
    val lows = points.mapIndexed { index, point ->
        val prev = points.getOrNull(index - 1)?.closePrice()
            ?: allPoints.getOrNull(startIndex.floatValue.roundToInt() + index - 1)?.closePrice()
        point.lowPrice().coerceAtMost(point.openPrice(prev))
    }
    val minP = if (candles) (lows.minOrNull() ?: 0.0) else (points.minOf { it.closePrice() })
    val maxP = if (candles) (highs.maxOrNull() ?: 1.0) else (points.maxOf { it.closePrice() })
    val span = max(maxP - minP, 0.0001)
    val lastClose = allPoints.last().closePrice()
    val density = LocalDensity.current
    val total = allPoints.size

    Canvas(
        modifier = modifier
            .pointerInput(total, candles) {
                val padRight = with(density) { 40.dp.toPx() }
                val slop = with(density) { 8.dp.toPx() }
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val plotW = (size.width - padRight).coerceAtLeast(1f)
                    fun viewCount(): Int = slicePoints(
                        allPoints,
                        startIndex.floatValue,
                        visibleCount.floatValue
                    ).size

                    onSelectIndex(
                        indexFromX(down.position.x, size.width.toFloat(), viewCount(), candles, padRight)
                    )
                    var lastPos = down.position
                    var lastSpan = 0f
                    var panning = false
                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.filter { it.pressed }
                            if (pressed.isEmpty()) break

                            if (pressed.size >= 2) {
                                val a = pressed[0].position
                                val b = pressed[1].position
                                val dist = hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble()).toFloat()
                                val focalX = ((a.x + b.x) / 2f).coerceIn(0f, plotW)
                                if (lastSpan > 0f && dist > 0f) {
                                    zoomViewport(
                                        startIndex = startIndex,
                                        visibleCount = visibleCount,
                                        total = total,
                                        factor = dist / lastSpan,
                                        focalX = focalX,
                                        plotW = plotW
                                    )
                                }
                                lastSpan = dist
                                lastPos = Offset(focalX, (a.y + b.y) / 2f)
                                pressed.forEach { it.consume() }
                            } else {
                                lastSpan = 0f
                                val pointer = pressed.first()
                                val dx = pointer.position.x - lastPos.x
                                val dy = pointer.position.y - lastPos.y
                                if (!panning && abs(dx) > abs(dy) && abs(dx) > slop) {
                                    panning = true
                                }
                                onSelectIndex(
                                    indexFromX(
                                        pointer.position.x,
                                        size.width.toFloat(),
                                        viewCount(),
                                        candles,
                                        padRight
                                    )
                                )
                                if (panning) {
                                    panViewport(
                                        startIndex = startIndex,
                                        visibleCount = visibleCount,
                                        total = total,
                                        dxPx = dx,
                                        plotW = plotW
                                    )
                                    pointer.consume()
                                }
                                lastPos = pointer.position
                            }
                        }
                    } finally {
                        onClearSelection()
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val padY = 14.dp.toPx()
        val padRight = 40.dp.toPx()
        val plotW = (w - padRight).coerceAtLeast(1f)
        val usableH = h - padY * 2

        fun yFor(price: Double): Float {
            val t = ((price - minP) / span).toFloat()
            return padY + usableH * (1f - t)
        }

        fun xFor(index: Int): Float {
            return if (candles) {
                val slot = plotW / points.size
                slot * (index + 0.5f)
            } else if (points.size == 1) {
                0f
            } else {
                plotW * index / (points.size - 1)
            }
        }

        val gridEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = BondMapColors.Navy.toArgb()
            textSize = 10.sp.toPx()
            textAlign = Paint.Align.RIGHT
        }

        for (i in 0..3) {
            val price = maxP - span * i / 3.0
            val y = padY + usableH * i / 3f
            drawLine(
                color = BondMapColors.Divider,
                start = Offset(0f, y),
                end = Offset(plotW, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = gridEffect
            )
            drawContext.canvas.nativeCanvas.drawText(
                formatNumber(price),
                w - 4.dp.toPx(),
                y + 4.dp.toPx(),
                labelPaint
            )
        }

        if (lastClose in minOf(minP, maxP)..maxOf(minP, maxP)) {
            val lastY = yFor(lastClose)
            drawLine(
                color = BondMapColors.Navy.copy(alpha = 0.45f),
                start = Offset(0f, lastY),
                end = Offset(plotW, lastY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = gridEffect
            )
        }

        if (candles) {
            val slot = plotW / points.size
            val bodyW = (slot * 0.62f).coerceIn(2.dp.toPx(), 10.dp.toPx())
            val wickW = 1.dp.toPx()
            val minBody = 1.dp.toPx()

            points.forEachIndexed { index, point ->
                val globalIndex = startIndex.floatValue.roundToInt() + index
                val prev = allPoints.getOrNull(globalIndex - 1)?.closePrice()
                val open = point.openPrice(prev)
                val close = point.closePrice()
                val x = xFor(index)
                val bullish = close >= open
                val color = if (bullish) BondMapColors.Accent else BondMapColors.Danger

                drawLine(
                    color = color,
                    start = Offset(x, yFor(point.highPrice())),
                    end = Offset(x, yFor(point.lowPrice())),
                    strokeWidth = wickW,
                    cap = StrokeCap.Round
                )
                val top = minOf(yFor(open), yFor(close))
                val bodyH = max(abs(yFor(close) - yFor(open)), minBody)
                drawRect(
                    color = color,
                    topLeft = Offset(x - bodyW / 2f, top),
                    size = Size(bodyW, bodyH)
                )
            }
        } else {
            val path = Path()
            points.forEachIndexed { index, point ->
                val x = xFor(index)
                val y = yFor(point.closePrice())
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = Offset(xFor(points.lastIndex), yFor(points.last().closePrice()))
            )
        }

        val selected = selectedIndex
        if (selected != null && selected in points.indices) {
            val point = points[selected]
            val globalIndex = startIndex.floatValue.roundToInt() + selected
            val prev = allPoints.getOrNull(globalIndex - 1)?.closePrice()
            val x = xFor(selected)
            val y = yFor(point.closePrice())
            drawLine(
                color = BondMapColors.Navy.copy(alpha = 0.55f),
                start = Offset(x, padY),
                end = Offset(x, padY + usableH),
                strokeWidth = 1.dp.toPx()
            )
            drawCircle(
                color = if (point.closePrice() >= point.openPrice(prev)) {
                    BondMapColors.Accent
                } else {
                    BondMapColors.Danger
                },
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )

            val dateText = formatDate(point.date)
            val valuesText = if (candles) {
                "O ${formatNumber(point.openPrice(prev))}  H ${formatNumber(point.highPrice())}  " +
                    "L ${formatNumber(point.lowPrice())}  C ${formatNumber(point.closePrice())}"
            } else {
                "Цена  ${formatNumber(point.closePrice())}"
            }
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = BondMapColors.TextOnNavy.toArgb()
                textSize = 11.sp.toPx()
                isFakeBoldText = true
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = BondMapColors.TextOnNavy.toArgb()
                textSize = 10.sp.toPx()
            }
            val padH = 8.dp.toPx()
            val padV = 5.dp.toPx()
            val lineGap = 3.dp.toPx()
            val boxW = max(titlePaint.measureText(dateText), bodyPaint.measureText(valuesText)) + padH * 2
            val boxH = padV * 2 + titlePaint.textSize + bodyPaint.textSize + lineGap
            val maxLeft = (plotW - boxW).coerceAtLeast(36.dp.toPx())
            val boxX = ((plotW - boxW) / 2f).coerceIn(36.dp.toPx(), maxLeft)
            val boxY = 4.dp.toPx()
            drawRoundRect(
                color = BondMapColors.Navy,
                topLeft = Offset(boxX, boxY),
                size = Size(boxW, boxH),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            val native = drawContext.canvas.nativeCanvas
            native.drawText(dateText, boxX + padH, boxY + padV + titlePaint.textSize, titlePaint)
            native.drawText(
                valuesText,
                boxX + padH,
                boxY + padV + titlePaint.textSize + lineGap + bodyPaint.textSize,
                bodyPaint
            )
        }
    }
}

private fun resetViewport(
    startIndex: MutableFloatState,
    visibleCount: MutableFloatState,
    total: Int
) {
    if (total <= 0) {
        startIndex.floatValue = 0f
        visibleCount.floatValue = DefaultVisibleCandles
        return
    }
    val count = DefaultVisibleCandles.coerceAtMost(total.toFloat()).coerceAtLeast(
        MinVisibleCandles.coerceAtMost(total.toFloat())
    )
    visibleCount.floatValue = count
    startIndex.floatValue = (total - count).coerceAtLeast(0f)
}

private fun panViewport(
    startIndex: MutableFloatState,
    visibleCount: MutableFloatState,
    total: Int,
    dxPx: Float,
    plotW: Float
) {
    val count = visibleCount.floatValue.coerceAtLeast(1f)
    val slot = plotW / count
    startIndex.floatValue = clampStart(startIndex.floatValue - dxPx / slot, count, total)
}

private fun zoomViewport(
    startIndex: MutableFloatState,
    visibleCount: MutableFloatState,
    total: Int,
    factor: Float,
    focalX: Float,
    plotW: Float
) {
    if (total <= 0 || plotW <= 0f || factor <= 0f) return
    val oldCount = visibleCount.floatValue
    val newCount = (oldCount / factor).coerceIn(
        MinVisibleCandles.coerceAtMost(total.toFloat()),
        total.toFloat()
    )
    val ratio = (focalX / plotW).coerceIn(0f, 1f)
    val focalIndex = startIndex.floatValue + ratio * oldCount
    visibleCount.floatValue = newCount
    startIndex.floatValue = clampStart(focalIndex - ratio * newCount, newCount, total)
}

private fun clampStart(start: Float, count: Float, total: Int): Float {
    val maxStart = (total - count).coerceAtLeast(0f)
    return start.coerceIn(0f, maxStart)
}

private fun slicePoints(
    points: List<PricePointDto>,
    start: Float,
    count: Float
): List<PricePointDto> {
    if (points.isEmpty()) return points
    val n = points.size
    val from = start.roundToInt().coerceIn(0, n - 1)
    val to = (from + count.roundToInt().coerceAtLeast(1)).coerceAtMost(n)
    if (from >= to) return listOf(points[from])
    return points.subList(from, to)
}

private fun indexFromX(
    x: Float,
    width: Float,
    count: Int,
    candles: Boolean,
    padRight: Float
): Int {
    if (count <= 1) return 0
    val plotW = (width - padRight).coerceAtLeast(1f)
    val clamped = x.coerceIn(0f, plotW)
    return if (candles) {
        val slot = plotW / count
        (clamped / slot).toInt().coerceIn(0, count - 1)
    } else {
        ((clamped / plotW) * (count - 1)).roundToInt().coerceIn(0, count - 1)
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
