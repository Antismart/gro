package com.example.gro.ui.component.garden

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun GardenBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Layer 1: Sky gradient (top 60%)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD4E8F0),
                    Color(0xFFBDD9C7),
                    Color(0xFFA8D5BA),
                ),
                startY = 0f,
                endY = h * 0.6f,
            ),
            size = Size(w, h * 0.6f),
        )

        // Layer 2: Clouds
        val cloudColor = Color(0x50FFFFFF)
        drawOval(
            color = cloudColor,
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.22f, h * 0.055f),
        )
        drawOval(
            color = cloudColor,
            topLeft = Offset(w * 0.15f, h * 0.065f),
            size = Size(w * 0.18f, h * 0.05f),
        )
        drawOval(
            color = cloudColor,
            topLeft = Offset(w * 0.55f, h * 0.12f),
            size = Size(w * 0.25f, h * 0.05f),
        )
        drawOval(
            color = cloudColor,
            topLeft = Offset(w * 0.62f, h * 0.105f),
            size = Size(w * 0.2f, h * 0.045f),
        )

        // Layer 3: Distant treeline
        val treePath = Path().apply {
            moveTo(0f, h * 0.52f)
            cubicTo(w * 0.08f, h * 0.46f, w * 0.15f, h * 0.49f, w * 0.22f, h * 0.44f)
            cubicTo(w * 0.30f, h * 0.39f, w * 0.38f, h * 0.47f, w * 0.48f, h * 0.42f)
            cubicTo(w * 0.58f, h * 0.37f, w * 0.65f, h * 0.45f, w * 0.75f, h * 0.40f)
            cubicTo(w * 0.85f, h * 0.35f, w * 0.92f, h * 0.44f, w, h * 0.48f)
            lineTo(w, h * 0.6f)
            lineTo(0f, h * 0.6f)
            close()
        }
        drawPath(treePath, Color(0xFF5C7A5E))

        // Layer 4: Midground foliage
        val midPath = Path().apply {
            moveTo(0f, h * 0.55f)
            cubicTo(w * 0.2f, h * 0.52f, w * 0.4f, h * 0.56f, w * 0.6f, h * 0.53f)
            cubicTo(w * 0.8f, h * 0.50f, w * 0.9f, h * 0.55f, w, h * 0.54f)
            lineTo(w, h * 0.62f)
            lineTo(0f, h * 0.62f)
            close()
        }
        drawPath(midPath, Color(0xFF6B8F6D))

        // Layer 5: Ground (grass)
        val groundPath = Path().apply {
            moveTo(0f, h * 0.58f)
            cubicTo(w * 0.15f, h * 0.56f, w * 0.35f, h * 0.60f, w * 0.5f, h * 0.57f)
            cubicTo(w * 0.65f, h * 0.55f, w * 0.85f, h * 0.59f, w, h * 0.57f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(groundPath, Color(0xFF8FAE8B))

        // Layer 6: Soil strip at bottom
        val soilPath = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(w * 0.25f, h * 0.80f, w * 0.75f, h * 0.84f, w, h * 0.82f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(soilPath, Color(0xFF9B8B7A))

        // Darker soil accent
        drawRect(
            color = Color(0xFF7C6955),
            topLeft = Offset(0f, h * 0.92f),
            size = Size(w, h * 0.08f),
        )
    }
}
