package com.example.gro.ui.component.garden

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.gro.ui.theme.GroEarth

@Composable
fun StreakBadge(
    streakCount: Int,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "streak")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "streakGlow",
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2

            // Glow behind flame
            drawCircle(
                color = Color(0xFFFF6B35).copy(alpha = glowAlpha * 0.4f),
                radius = w * 0.6f,
                center = Offset(cx, h * 0.45f),
            )

            // Flame shape
            val flamePath = Path().apply {
                moveTo(cx, h * 0.05f)
                cubicTo(cx + w * 0.08f, h * 0.25f, cx + w * 0.35f, h * 0.3f, cx + w * 0.3f, h * 0.55f)
                cubicTo(cx + w * 0.28f, h * 0.75f, cx + w * 0.15f, h * 0.9f, cx, h * 0.95f)
                cubicTo(cx - w * 0.15f, h * 0.9f, cx - w * 0.28f, h * 0.75f, cx - w * 0.3f, h * 0.55f)
                cubicTo(cx - w * 0.35f, h * 0.3f, cx - w * 0.08f, h * 0.25f, cx, h * 0.05f)
                close()
            }
            drawPath(
                flamePath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFD700), Color(0xFFFF6B35), Color(0xFFE84530)),
                ),
            )

            // Inner bright core
            val corePath = Path().apply {
                moveTo(cx, h * 0.3f)
                cubicTo(cx + w * 0.1f, h * 0.45f, cx + w * 0.12f, h * 0.6f, cx, h * 0.75f)
                cubicTo(cx - w * 0.12f, h * 0.6f, cx - w * 0.1f, h * 0.45f, cx, h * 0.3f)
                close()
            }
            drawPath(
                corePath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFF8DC), Color(0xFFFFD700)),
                ),
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$streakCount",
            style = MaterialTheme.typography.labelLarge,
            color = GroEarth,
        )
    }
}
