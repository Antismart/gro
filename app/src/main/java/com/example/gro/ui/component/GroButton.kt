package com.example.gro.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.gro.ui.theme.ButtonShape
import com.example.gro.ui.theme.GroBark
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.GroWhite

enum class GroButtonStyle { Primary, Secondary, Tertiary }

@Composable
fun GroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    style: GroButtonStyle = GroButtonStyle.Primary,
) {
    val contentAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        label = "contentAlpha",
    )

    when (style) {
        GroButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(52.dp),
                enabled = enabled && !isLoading,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GroGreen,
                    contentColor = GroWhite,
                ),
                contentPadding = PaddingValues(
                    horizontal = GroSpacing.lg,
                    vertical = GroSpacing.sm,
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = GroWhite,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.alpha(contentAlpha),
                    )
                }
            }
        }

        GroButtonStyle.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(52.dp),
                enabled = enabled && !isLoading,
                shape = ButtonShape,
                border = BorderStroke(1.5.dp, GroGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GroGreen,
                ),
                contentPadding = PaddingValues(
                    horizontal = GroSpacing.lg,
                    vertical = GroSpacing.sm,
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = GroGreen,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        GroButtonStyle.Tertiary -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = GroEarth,
                ),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = GroEarth,
                )
            }
        }
    }
}
