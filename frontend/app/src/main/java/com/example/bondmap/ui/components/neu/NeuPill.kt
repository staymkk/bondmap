package com.example.bondmap.ui.components.neu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.bondmap.ui.theme.BondMapColors
import com.example.bondmap.ui.theme.PillShape

@Composable
fun NeuPill(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    containerColor: Color? = null,
    contentColorOverride: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled && onClick != null) 0.98f else 1f,
        label = "pillScale"
    )
    val shape = PillShape
    val container = when {
        containerColor != null -> containerColor
        !enabled -> BondMapColors.ChipBg
        selected -> BondMapColors.Navy
        else -> BondMapColors.SurfaceNeu
    }
    val contentColor = when {
        contentColorOverride != null -> contentColorOverride
        !enabled -> BondMapColors.TextSecondary.copy(alpha = 0.55f)
        selected -> BondMapColors.TextOnNavy
        else -> BondMapColors.TextPrimary
    }
    val colored = containerColor != null
    val shadow = when {
        !enabled -> Modifier
        pressed && onClick != null -> Modifier
        selected || colored -> Modifier.neuDrop(
            shape = shape,
            offset = 3.dp,
            blur = 8.dp,
            lightColor = Color.Transparent,
            darkColor = Color.Black.copy(alpha = 0.18f)
        )
        else -> Modifier.neuDrop(shape)
    }

    Row(
        modifier = modifier
            .scale(scale)
            .then(shadow)
            .clip(shape)
            .then(
                if (pressed && enabled && onClick != null) Modifier.neuInset(shape) else Modifier
            )
            .background(container, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Composable
fun NeuPillRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
