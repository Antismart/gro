package com.example.gro.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSage
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.GroSurface

enum class BottomNavItem(val label: String) {
    Garden("Garden"),
    Deposit("Water"),
    Social("Visit"),
    Settings("Settings"),
}

@Composable
fun GroBottomNavBar(
    currentItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(GroSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = GroSpacing.md, vertical = GroSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem.entries.forEach { item ->
                val selected = item == currentItem
                NavItemView(
                    item = item,
                    selected = selected,
                    onClick = { onItemSelected(item) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.85f,
        animationSpec = tween(200),
        label = "navScale",
    )
    val iconColor = if (selected) GroGreen else GroEarth
    val textColor = if (selected) GroGreen else GroEarth.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onClick() }
            .padding(vertical = GroSpacing.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Leaf shape behind active item
            if (selected) {
                Canvas(modifier = Modifier.size(32.dp)) {
                    drawLeafBackground(GroSage.copy(alpha = 0.25f))
                }
            }
            Canvas(modifier = Modifier.size((24 * scale).dp)) {
                when (item) {
                    BottomNavItem.Garden -> drawLeafIcon(iconColor, selected)
                    BottomNavItem.Deposit -> drawDropletIcon(iconColor, selected)
                    BottomNavItem.Social -> drawBinocularsIcon(iconColor, selected)
                    BottomNavItem.Settings -> drawGearIcon(iconColor, selected)
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}

private fun DrawScope.drawLeafBackground(color: Color) {
    val path = Path().apply {
        moveTo(size.width * 0.5f, 0f)
        cubicTo(size.width, 0f, size.width, size.height * 0.5f, size.width * 0.5f, size.height)
        cubicTo(0f, size.height * 0.5f, 0f, 0f, size.width * 0.5f, 0f)
        close()
    }
    drawPath(path, color, style = Fill)
}

private fun DrawScope.drawLeafIcon(color: Color, filled: Boolean) {
    val path = Path().apply {
        moveTo(size.width * 0.5f, size.height * 0.1f)
        cubicTo(size.width * 0.9f, size.height * 0.1f, size.width * 0.9f, size.height * 0.6f, size.width * 0.5f, size.height * 0.9f)
        cubicTo(size.width * 0.1f, size.height * 0.6f, size.width * 0.1f, size.height * 0.1f, size.width * 0.5f, size.height * 0.1f)
        close()
    }
    if (filled) {
        drawPath(path, color, style = Fill)
    } else {
        drawPath(path, color, style = Stroke(width = 2f))
    }
    // Stem
    drawLine(color, Offset(size.width * 0.5f, size.height * 0.3f), Offset(size.width * 0.5f, size.height * 0.85f), strokeWidth = 1.5f)
}

private fun DrawScope.drawDropletIcon(color: Color, filled: Boolean) {
    val path = Path().apply {
        moveTo(size.width * 0.5f, size.height * 0.1f)
        cubicTo(size.width * 0.5f, size.height * 0.1f, size.width * 0.9f, size.height * 0.55f, size.width * 0.5f, size.height * 0.9f)
        cubicTo(size.width * 0.1f, size.height * 0.55f, size.width * 0.5f, size.height * 0.1f, size.width * 0.5f, size.height * 0.1f)
        close()
    }
    if (filled) {
        drawPath(path, color, style = Fill)
    } else {
        drawPath(path, color, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawBinocularsIcon(color: Color, filled: Boolean) {
    val r = size.width * 0.2f
    val style = if (filled) Fill else Stroke(width = 2f)
    // Left circle
    drawCircle(color, r, Offset(size.width * 0.3f, size.height * 0.6f), style = style)
    // Right circle
    drawCircle(color, r, Offset(size.width * 0.7f, size.height * 0.6f), style = style)
    // Bridge
    drawLine(color, Offset(size.width * 0.4f, size.height * 0.45f), Offset(size.width * 0.6f, size.height * 0.45f), strokeWidth = 2f)
    // Tubes
    drawLine(color, Offset(size.width * 0.3f, size.height * 0.4f), Offset(size.width * 0.3f, size.height * 0.25f), strokeWidth = 2f)
    drawLine(color, Offset(size.width * 0.7f, size.height * 0.4f), Offset(size.width * 0.7f, size.height * 0.25f), strokeWidth = 2f)
}

private fun DrawScope.drawGearIcon(color: Color, filled: Boolean) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val outerR = size.width * 0.38f
    val innerR = size.width * 0.22f
    val style = if (filled) Fill else Stroke(width = 2f)
    // Outer gear circle
    drawCircle(color, outerR, Offset(cx, cy), style = Stroke(width = 2f))
    // Inner circle
    drawCircle(color, innerR, Offset(cx, cy), style = style)
    // Teeth (4 lines)
    val teeth = listOf(0f, 90f, 45f, 135f)
    teeth.forEach { angle ->
        val rad = Math.toRadians(angle.toDouble())
        val cos = kotlin.math.cos(rad).toFloat()
        val sin = kotlin.math.sin(rad).toFloat()
        drawLine(
            color,
            Offset(cx + cos * (outerR - 2f), cy + sin * (outerR - 2f)),
            Offset(cx + cos * (outerR + size.width * 0.08f), cy + sin * (outerR + size.width * 0.08f)),
            strokeWidth = 3f,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBottomNav() {
    GroBottomNavBar(
        currentItem = BottomNavItem.Garden,
        onItemSelected = {},
    )
}
