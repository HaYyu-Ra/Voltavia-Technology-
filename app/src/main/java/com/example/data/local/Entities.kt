package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val id: String,
    val referenceNumber: String,
    val customerName: String,
    val phone: String,
    val email: String,
    val organization: String,
    val cityOrSite: String,
    val serviceId: String,
    val serviceTitle: String,
    val requirements: String,
    val budgetEtb: Long?,
    val targetDate: String,
    val isDraft: Boolean,
    val status: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val referenceCode: String,
    val category: String,
    val priority: String,
    val subject: String,
    val description: String,
    val contactPerson: String,
    val status: String,
    val submissionStatus: String,
    val createdAtFormatted: String,
    val createdAtEpochMs: Long
)

@Entity(
    tableName = "ticket_replies",
    indices = [Index(value = ["ticketId"])]
)
data class TicketReplyEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val timestampFormatted: String,
    val isClientReply: Boolean,
    val timestampEpochMs: Long
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val referenceCode: String,
    val title: String,
    val clientName: String,
    val serviceCategory: String,
    val siteLocation: String,
    val currentStage: String,
    val progressPercent: Int,
    val startDate: String,
    val estimatedCompletionDate: String,
    val assignedLeadEngineer: String,
    val isSample: Boolean
)

@Entity(
    tableName = "project_milestones",
    indices = [Index(value = ["projectId"])]
)
data class ProjectMilestoneEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val stage: String,
    val isCompleted: Boolean,
    val isInProgress: Boolean,
    val completionDate: String?,
    val engineerNotes: String,
    val stepOrder: Int
)
