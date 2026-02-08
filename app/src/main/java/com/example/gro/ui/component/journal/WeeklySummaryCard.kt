package com.example.gro.ui.component.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing

data class WeeklySummary(
    val deposits: Int = 0,
    val growthEvents: Int = 0,
    val streakDays: Int = 0,
    val blooms: Int = 0,
)

@Composable
fun WeeklySummaryCard(
    summary: WeeklySummary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0EBE1))
            .padding(GroSpacing.md),
    ) {
        Text(
            text = "This Week",
            style = MaterialTheme.typography.titleSmall,
            color = GroEarth,
        )
        Spacer(modifier = Modifier.height(GroSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SummaryItem(count = summary.deposits, label = "Deposits")
            SummaryItem(count = summary.growthEvents, label = "Growth")
            SummaryItem(count = summary.streakDays, label = "Streak days")
            SummaryItem(count = summary.blooms, label = "Blooms")
        }
    }
}

@Composable
private fun SummaryItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            color = GroGreen,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GroEarth,
        )
    }
}
