package com.example.data.repository

import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectMilestoneEntity
import com.example.data.local.QuoteEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TicketReplyEntity
import com.example.data.local.VoltaviaDao
import com.example.data.model.AppMode
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectMilestone
import com.example.data.model.ProjectStage
import com.example.data.model.QuoteRequest
import com.example.data.model.QuoteValidationHelper
import com.example.data.model.SampleProjects
import com.example.data.model.SampleTickets
import com.example.data.model.ServiceCategory
import com.example.data.model.SubmissionStatus
import com.example.data.model.SupportTicket
import com.example.data.model.TicketCategory
import com.example.data.model.TicketPriority
import com.example.data.model.TicketReply
import com.example.data.model.TicketStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VoltaviaRepository(
    private val dao: VoltaviaDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _appMode = MutableStateFlow(AppMode.OFFLINE_DEMO)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    // In-memory set to prevent rapid concurrent tap double-submissions
    private val activeSubmissions = mutableSetOf<String>()

    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    /**
     * Seeds initial demo projects and sample support ticket idempotently.
     */
    suspend fun seedSampleDataIfEmpty() = withContext(ioDispatcher) {
        val existingDraft = dao.getActiveDraft()
        // Check if sample projects exist
        val projects = SampleProjects.sampleProjects
        val projectEntities = projects.map { p ->
            ProjectEntity(
                id = p.id,
                referenceCode = p.referenceCode,
                title = p.title,
                clientName = p.clientName,
                serviceCategory = p.serviceCategory.name,
                siteLocation = p.siteLocation,
                currentStage = p.currentStage.name,
                progressPercent = p.progressPercent,
                startDate = p.startDate,
                estimatedCompletionDate = p.estimatedCompletionDate,
                assignedLeadEngineer = p.assignedLeadEngineer,
                isSample = p.isSample
            )
        }
        dao.insertProjects(projectEntities)

        val milestoneEntities = mutableListOf<ProjectMilestoneEntity>()
        projects.forEach { p ->
            p.milestones.forEachIndexed { idx, m ->
                milestoneEntities.add(
                    ProjectMilestoneEntity(
                        id = m.id,
                        projectId = p.id,
                        stage = m.stage.name,
                        isCompleted = m.isCompleted,
                        isInProgress = m.isInProgress,
                        completionDate = m.completionDate,
                        engineerNotes = m.engineerNotes,
                        stepOrder = idx
                    )
                )
            }
        }
        dao.insertMilestones(milestoneEntities)

        // Seed initial sample ticket if no tickets exist
        SampleTickets.sampleTickets.forEach { tkt ->
            dao.insertTicket(
                SupportTicketEntity(
                    id = tkt.id,
                    referenceCode = tkt.referenceCode,
                    category = tkt.category.name,
                    priority = tkt.priority.name,
                    subject = tkt.subject,
                    description = tkt.description,
                    contactPerson = tkt.contactPerson,
                    status = tkt.status.name,
                    submissionStatus = tkt.submissionStatus.name,
                    createdAtFormatted = tkt.createdAtFormatted,
                    createdAtEpochMs = System.currentTimeMillis() - 86400000L
                )
            )
            val replyEntities = tkt.replies.mapIndexed { index, r ->
                TicketReplyEntity(
                    id = r.id,
                    ticketId = tkt.id,
                    senderName = r.senderName,
                    senderRole = r.senderRole,
                    message = r.message,
                    timestampFormatted = r.timestampFormatted,
                    isClientReply = r.isClientReply,
                    timestampEpochMs = System.currentTimeMillis() - 86400000L + (index * 1800000L)
                )
            }
            dao.insertReplies(replyEntities)
        }
    }

    /**
     * Resets demo database back to clean sample data.
     */
    suspend fun resetDemoData() = withContext(ioDispatcher) {
        dao.clearQuotes()
        dao.clearTickets()
        dao.clearReplies()
        dao.clearProjects()
        dao.clearMilestones()
        seedSampleDataIfEmpty()
    }

    // --- Quotes Flow ---
    fun getAllQuotes(): Flow<List<QuoteRequest>> {
        return dao.getAllQuotes().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getActiveDraft(): QuoteRequest? = withContext(ioDispatcher) {
        dao.getActiveDraft()?.toModel()
    }

    suspend fun saveDraft(quote: QuoteRequest) = withContext(ioDispatcher) {
        val draftEntity = quote.copy(
            isDraft = true,
            status = SubmissionStatus.DRAFT,
            updatedAtEpochMs = System.currentTimeMillis()
        ).toEntity()
        dao.insertQuote(draftEntity)
    }

    suspend fun discardDraft(id: String) = withContext(ioDispatcher) {
        dao.deleteQuoteById(id)
    }

    /**
     * Submits a quote request with duplicate submission prevention.
     */
    suspend fun submitQuote(quote: QuoteRequest, isProduction: Boolean): Result<QuoteRequest> = withContext(ioDispatcher) {
        val submissionKey = quote.id
        if (activeSubmissions.contains(submissionKey)) {
            return@withContext Result.failure(IllegalStateException("Submission is already in progress."))
        }

        activeSubmissions.add(submissionKey)
        try {
            val status = if (isProduction) SubmissionStatus.SUBMITTED else SubmissionStatus.SAVED_ON_DEVICE
            val finalQuote = quote.copy(
                isDraft = false,
                status = status,
                updatedAtEpochMs = System.currentTimeMillis()
            )
            dao.insertQuote(finalQuote.toEntity())
            Result.success(finalQuote)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            activeSubmissions.remove(submissionKey)
        }
    }

    // --- Projects Flow ---
    fun getAllProjects(): Flow<List<ProjectItem>> {
        return dao.getAllProjects().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getProjectMilestones(projectId: String): Flow<List<ProjectMilestone>> {
        return dao.getMilestonesForProject(projectId).map { list ->
            list.map { it.toModel() }
        }
    }

    // --- Support Tickets Flow ---
    fun getAllTickets(): Flow<List<SupportTicket>> {
        return dao.getAllTickets().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getTicketReplies(ticketId: String): Flow<List<TicketReply>> {
        return dao.getRepliesForTicket(ticketId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun createTicket(ticket: SupportTicket, isProduction: Boolean): Result<SupportTicket> = withContext(ioDispatcher) {
        try {
            val status = if (isProduction) SubmissionStatus.SUBMITTED else SubmissionStatus.SAVED_ON_DEVICE
            val finalTicket = ticket.copy(submissionStatus = status)
            val entity = SupportTicketEntity(
                id = finalTicket.id,
                referenceCode = finalTicket.referenceCode,
                category = finalTicket.category.name,
                priority = finalTicket.priority.name,
                subject = finalTicket.subject,
                description = finalTicket.description,
                contactPerson = finalTicket.contactPerson,
                status = finalTicket.status.name,
                submissionStatus = finalTicket.submissionStatus.name,
                createdAtFormatted = finalTicket.createdAtFormatted,
                createdAtEpochMs = System.currentTimeMillis()
            )
            dao.insertTicket(entity)
            Result.success(finalTicket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTicketReply(ticketId: String, senderName: String, isClient: Boolean, message: String) = withContext(ioDispatcher) {
        val now = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date())
        val reply = TicketReplyEntity(
            id = UUID.randomUUID().toString(),
            ticketId = ticketId,
            senderName = senderName,
            senderRole = if (isClient) "Client" else "Voltavia Support",
            message = message.trim(),
            timestampFormatted = now,
            isClientReply = isClient,
            timestampEpochMs = System.currentTimeMillis()
        )
        dao.insertReply(reply)
    }

    // --- Entity Mappers ---
    private fun QuoteEntity.toModel(): QuoteRequest {
        return QuoteRequest(
            id = id,
            referenceNumber = referenceNumber,
            customerName = customerName,
            phone = phone,
            email = email,
            organization = organization,
            cityOrSite = cityOrSite,
            serviceId = serviceId,
            serviceTitle = serviceTitle,
            requirements = requirements,
            budgetEtb = budgetEtb,
            targetDate = targetDate,
            isDraft = isDraft,
            status = try { SubmissionStatus.valueOf(status) } catch (e: Exception) { SubmissionStatus.SAVED_ON_DEVICE },
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs
        )
    }

    private fun QuoteRequest.toEntity(): QuoteEntity {
        return QuoteEntity(
            id = id,
            referenceNumber = referenceNumber,
            customerName = customerName,
            phone = phone,
            email = email,
            organization = organization,
            cityOrSite = cityOrSite,
            serviceId = serviceId,
            serviceTitle = serviceTitle,
            requirements = requirements,
            budgetEtb = budgetEtb,
            targetDate = targetDate,
            isDraft = isDraft,
            status = status.name,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs
        )
    }

    private fun ProjectEntity.toModel(): ProjectItem {
        return ProjectItem(
            id = id,
            referenceCode = referenceCode,
            title = title,
            clientName = clientName,
            serviceCategory = try { ServiceCategory.valueOf(serviceCategory) } catch (e: Exception) { ServiceCategory.SOFTWARE },
            siteLocation = siteLocation,
            currentStage = try { ProjectStage.valueOf(currentStage) } catch (e: Exception) { ProjectStage.SURVEY },
            progressPercent = progressPercent,
            startDate = startDate,
            estimatedCompletionDate = estimatedCompletionDate,
            assignedLeadEngineer = assignedLeadEngineer,
            isSample = isSample
        )
    }

    private fun ProjectMilestoneEntity.toModel(): ProjectMilestone {
        return ProjectMilestone(
            id = id,
            projectId = projectId,
            stage = try { ProjectStage.valueOf(stage) } catch (e: Exception) { ProjectStage.SURVEY },
            isCompleted = isCompleted,
            isInProgress = isInProgress,
            completionDate = completionDate,
            engineerNotes = engineerNotes
        )
    }

    private fun SupportTicketEntity.toModel(): SupportTicket {
        return SupportTicket(
            id = id,
            referenceCode = referenceCode,
            category = try { TicketCategory.valueOf(category) } catch (e: Exception) { TicketCategory.ICT_HELP },
            priority = try { TicketPriority.valueOf(priority) } catch (e: Exception) { TicketPriority.MEDIUM },
            subject = subject,
            description = description,
            contactPerson = contactPerson,
            status = try { TicketStatus.valueOf(status) } catch (e: Exception) { TicketStatus.OPEN },
            submissionStatus = try { SubmissionStatus.valueOf(submissionStatus) } catch (e: Exception) { SubmissionStatus.SAVED_ON_DEVICE },
            createdAtFormatted = createdAtFormatted
        )
    }

    private fun TicketReplyEntity.toModel(): TicketReply {
        return TicketReply(
            id = id,
            ticketId = ticketId,
            senderName = senderName,
            senderRole = senderRole,
            message = message,
            timestampFormatted = timestampFormatted,
            isClientReply = isClientReply
        )
    }
}
