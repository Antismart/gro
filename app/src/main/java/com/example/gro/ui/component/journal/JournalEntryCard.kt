package com.example.gro.ui.component.journal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    modifier: Modifier = Modifier,
) {
    val iconColor = when (entry.action) {
        JournalAction.DEPOSIT -> Color(0xFF4A90D9)
        JournalAction.GROWTH -> GroGreen
        JournalAction.BLOOM -> Color(0xFFE8B849)
        JournalAction.STREAK -> Color(0xFFFF6B35)
        JournalAction.VISIT -> Color(0xFF9B59B6)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        // Icon dot
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = iconColor)
        }
        Spacer(modifier = Modifier.width(GroSpacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.action.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelMedium,
                color = iconColor,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = entry.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTimestamp(entry.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = GroEarth.copy(alpha = 0.6f),
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
