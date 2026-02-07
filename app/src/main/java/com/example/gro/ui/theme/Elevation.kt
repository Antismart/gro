package com.example.gro.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.groShadow(): Modifier = this.shadow(
    elevation = 4.dp,
    shape = RoundedCornerShape(20.dp),
    ambientColor = Color(0x15000000),
    spotColor = Color(0x20000000),
)

fun Modifier.groShadowSmall(): Modifier = this.shadow(
    elevation = 2.dp,
    shape = RoundedCornerShape(12.dp),
    ambientColor = Color(0x10000000),
    spotColor = Color(0x15000000),
)
