package com.example.gro.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gro.ui.theme.CardShape
import com.example.gro.ui.theme.GroSand
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.groShadow

@Composable
fun GroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .groShadow(),
        shape = CardShape,
        color = GroSand,
    ) {
        Column(
            modifier = Modifier.padding(GroSpacing.md),
            content = content,
        )
    }
}
