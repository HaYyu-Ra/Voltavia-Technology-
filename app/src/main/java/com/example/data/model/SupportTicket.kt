package com.example.data.model

import java.util.UUID

enum class TicketCategory(val displayName: String) {
    NETWORK_OUTAGE("Network & Wi-Fi Outage"),
    SOFTWARE_BUG("Software / System Issue"),
    SOLAR_POWER("Solar & Inverter Fault"),
    CCTV_OFFLINE("CCTV & Security Offline"),
    ICT_HELP("General ICT Assistance")
}

enum class TicketPriority(val displayName: String) {
    LOW("Low - Routine Request"),
    MEDIUM("Medium - Normal Priority"),
    HIGH("High - Significant Impact"),
    CRITICAL("Critical - System Down")
}

enum class TicketStatus(val displayName: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    AWAITING_CLIENT("Awaiting Client"),
    RESOLVED("Resolved"),
    CLOSED("Closed")
}

data class TicketReply(
    val id: String = UUID.randomUUID().toString(),
    val ticketId: String,
    val senderName: String,
    val senderRole: String, // e.g. "Voltavia Support Engineer" or "Client"
    val message: String,
    val timestampFormatted: String,
    val isClientReply: Boolean
)

data class SupportTicket(
    val id: String = UUID.randomUUID().toString(),
    val referenceCode: String,
    val category: TicketCategory,
    val priority: TicketPriority,
    val subject: String,
    val description: String,
    val contactPerson: String,
    val status: TicketStatus = TicketStatus.OPEN,
    val submissionStatus: SubmissionStatus = SubmissionStatus.SAVED_ON_DEVICE,
    val createdAtFormatted: String,
    val replies: List<TicketReply> = emptyList()
)

object SampleTickets {
    val sampleTickets: List<SupportTicket> = listOf(
        SupportTicket(
            id = "tkt_sample_01",
            referenceCode = "TKT-DEMO-5012",
            category = TicketCategory.NETWORK_OUTAGE,
            priority = TicketPriority.HIGH,
            subject = "Second floor switch intermittent link drops",
            description = "Workstations on the marketing wing lose network connection for 2-3 minutes periodically during peak hours.",
            contactPerson = "Abebe Kebede",
            status = TicketStatus.IN_PROGRESS,
            submissionStatus = SubmissionStatus.SAVED_ON_DEVICE,
            createdAtFormatted = "Yesterday, 3:45 PM",
            replies = listOf(
                TicketReply(
                    id = "rep_1",
                    ticketId = "tkt_sample_01",
                    senderName = "Voltavia Helpdesk",
                    senderRole = "Support Engineer",
                    message = "We received your report. A remote diagnostics check on the Core Switch showed port buffer throttling on Gi0/24.",
                    timestampFormatted = "Yesterday, 4:10 PM",
                    isClientReply = false
                ),
                TicketReply(
                    id = "rep_2",
                    ticketId = "tkt_sample_01",
                    senderName = "Abebe Kebede",
                    senderRole = "Client",
                    message = "Thank you. Let us know if our on-site IT team needs to power cycle the PoE injector.",
                    timestampFormatted = "Yesterday, 4:35 PM",
                    isClientReply = true
                ),
                TicketReply(
                    id = "rep_3",
                    ticketId = "tkt_sample_01",
                    senderName = "Eng. Dawit Tesfaye",
                    senderRole = "Senior Network Engineer",
                    message = "Firmware patch scheduled for 6:00 PM today during maintenance window to resolve the packet buffer lock.",
                    timestampFormatted = "Today, 9:20 AM",
                    isClientReply = false
                )
            )
        )
    )
}
