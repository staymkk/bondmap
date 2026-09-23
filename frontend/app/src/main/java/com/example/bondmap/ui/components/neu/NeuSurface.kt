package com.example.bondmap.ui.components.neu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.CardShape

@Composable
fun NeuSurface(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    color: Color = BondMapColors.SurfaceNeu,
    pressed: Boolean = false,
    inset: Boolean = false,
    dropShadow: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val shadow = when {
        pressed || inset -> Modifier.neuInset(shape)
        dropShadow -> Modifier.neuDrop(shape)
        else -> Modifier
    }
    Box(
        modifier = modifier
            .then(if (pressed || inset) Modifier else shadow)
            .clip(shape)
            .then(if (pressed || inset) Modifier.neuInset(shape) else Modifier)
            .background(color, shape),
        content = content
    )
}
