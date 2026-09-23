package com.example.bondmap.ui.components.neu

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import com.example.bondmap.ui.theme.CardShape
import com.example.bondmap.ui.theme.NeuTokens

fun Modifier.neuDrop(
    shape: Shape = CardShape,
    offset: Dp = NeuTokens.Offset,
    blur: Dp = NeuTokens.Blur,
    lightColor: Color = NeuTokens.ShadowLight,
    darkColor: Color = NeuTokens.ShadowDarkDrop
): Modifier = drawBehind {
    val ox = offset.toPx()
    val blurPx = blur.toPx()
    drawOuterShadow(shape, lightColor, Offset(-ox, -ox), blurPx)
    drawOuterShadow(shape, darkColor, Offset(ox, ox), blurPx)
}

fun Modifier.neuInset(
    shape: Shape = CardShape,
    offset: Dp = NeuTokens.Offset,
    blur: Dp = NeuTokens.Blur,
    lightColor: Color = NeuTokens.ShadowLight,
    darkColor: Color = NeuTokens.ShadowDarkInner
): Modifier = drawWithContent {
    drawContent()
    val ox = offset.toPx()
    val blurPx = blur.toPx()
    drawInnerShadow(shape, lightColor, Offset(-ox, -ox), blurPx)
    drawInnerShadow(shape, darkColor, Offset(ox, ox), blurPx)
}

private fun DrawScope.drawOuterShadow(
    shape: Shape,
    color: Color,
    offset: Offset,
    blurPx: Float
) {
    if (size.width <= 0f || size.height <= 0f || color.alpha == 0f) return
    val outline = shape.createOutline(size, layoutDirection, this)
    val paint = shadowPaint(color, blurPx)
    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offset.x, offset.y)
        canvas.nativeCanvas.drawOutlinePath(outline, paint)
        canvas.restore()
    }
}

private fun DrawScope.drawInnerShadow(
    shape: Shape,
    color: Color,
    shadowOffset: Offset,
    blurPx: Float
) {
    if (size.width <= 0f || size.height <= 0f || color.alpha == 0f) return
    val outline = shape.createOutline(size, layoutDirection, this)
    val fillPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        style = android.graphics.Paint.Style.FILL
    }
    val punchPaint = shadowPaint(Color.Black, blurPx).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }
    val bounds = Rect(-blurPx, -blurPx, size.width + blurPx, size.height + blurPx)
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val checkpoint = native.saveLayer(
            bounds.left,
            bounds.top,
            bounds.right,
            bounds.bottom,
            null
        )
        native.drawOutlinePath(outline, fillPaint)
        native.save()
        native.translate(-shadowOffset.x, -shadowOffset.y)
        native.drawOutlinePath(outline, punchPaint)
        native.restore()
        native.restoreToCount(checkpoint)
    }
}

private fun shadowPaint(color: Color, blurPx: Float): android.graphics.Paint {
    return android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        style = android.graphics.Paint.Style.FILL
        maskFilter = BlurMaskFilter(blurPx.coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
    }
}

private fun NativeCanvas.drawOutlinePath(outline: Outline, paint: android.graphics.Paint) {
    when (outline) {
        is Outline.Rectangle -> {
            drawRect(outline.rect.left, outline.rect.top, outline.rect.right, outline.rect.bottom, paint)
        }
        is Outline.Rounded -> {
            val rr = outline.roundRect
            val radii = floatArrayOf(
                rr.topLeftCornerRadius.x, rr.topLeftCornerRadius.y,
                rr.topRightCornerRadius.x, rr.topRightCornerRadius.y,
                rr.bottomRightCornerRadius.x, rr.bottomRightCornerRadius.y,
                rr.bottomLeftCornerRadius.x, rr.bottomLeftCornerRadius.y
            )
            drawPath(
                android.graphics.Path().apply {
                    addRoundRect(
                        RectF(rr.left, rr.top, rr.right, rr.bottom),
                        radii,
                        android.graphics.Path.Direction.CW
                    )
                },
                paint
            )
        }
        is Outline.Generic -> drawPath(android.graphics.Path(outline.path.asAndroidPath()), paint)
    }
}
