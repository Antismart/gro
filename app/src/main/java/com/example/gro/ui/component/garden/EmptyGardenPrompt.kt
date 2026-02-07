package com.example.gro.ui.component.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroSpacing

@Composable
fun EmptyGardenPrompt(
    onDeposit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        GardenBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GroSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Your garden awaits",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(GroSpacing.xs))
            Text(
                text = "Plant your first seed with a deposit",
                style = MaterialTheme.typography.bodyLarge,
                color = GroEarth,
            )
            Spacer(modifier = Modifier.height(GroSpacing.lg))
            GroButton(
                text = "Plant a seed",
                onClick = onDeposit,
            )
        }
    }
}
