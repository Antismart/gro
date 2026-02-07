package com.example.gro.ui.component.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.theme.GroBark
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroSpacing

@Composable
fun EmptyGardenPrompt(
    onDeposit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        GardenBackground(modifier = Modifier.fillMaxSize())

        // Frosted card anchored to the bottom of the scene
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.xl),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xE6F5F0E8)) // GroCream @ 90% opacity
                    .padding(horizontal = GroSpacing.lg, vertical = GroSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Your garden awaits",
                    style = MaterialTheme.typography.headlineLarge,
                    color = GroBark,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(GroSpacing.xs))
                Text(
                    text = "Plant your first seed with a deposit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GroEarth,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(GroSpacing.lg))
                GroButton(
                    text = "Plant a seed",
                    onClick = onDeposit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
