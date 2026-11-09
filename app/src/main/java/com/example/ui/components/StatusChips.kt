package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubmissionStatus
import com.example.data.model.TicketPriority
import com.example.data.model.TicketStatus

@Composable
fun SubmissionStatusChip(
    status: SubmissionStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        SubmissionStatus.SAVED_ON_DEVICE -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "Saved on this device")
        SubmissionStatus.DRAFT -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "Local Draft")
        SubmissionStatus.SYNCING -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Syncing…")
        SubmissionStatus.SUBMITTED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Submitted")
        SubmissionStatus.FAILED -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Failed")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("status_chip_${status.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun TicketStatusChip(
    status: TicketStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        TicketStatus.OPEN -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        TicketStatus.IN_PROGRESS -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        TicketStatus.AWAITING_CLIENT -> Pair(Color(0xFFF3E8FF), Color(0xFF6B21A8))
        TicketStatus.RESOLVED -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        TicketStatus.CLOSED -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun TicketPriorityChip(
    priority: TicketPriority,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (priority) {
        TicketPriority.LOW -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "Low")
        TicketPriority.MEDIUM -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Medium")
        TicketPriority.HIGH -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "High")
        TicketPriority.CRITICAL -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Critical")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 10.sp
            )
        )
    }
}
