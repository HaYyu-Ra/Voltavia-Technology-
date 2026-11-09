package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VoltaviaDao {
    // --- Quotes ---
    @Query("SELECT * FROM quotes ORDER BY updatedAtEpochMs DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE isDraft = 1 ORDER BY updatedAtEpochMs DESC LIMIT 1")
    suspend fun getActiveDraft(): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE id = :id LIMIT 1")
    suspend fun getQuoteById(id: String): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE referenceNumber = :referenceNumber LIMIT 1")
    suspend fun getQuoteByReference(referenceNumber: String): QuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Query("DELETE FROM quotes WHERE id = :id")
    suspend fun deleteQuoteById(id: String)

    @Query("DELETE FROM quotes WHERE isDraft = 1")
    suspend fun deleteDrafts()

    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets ORDER BY createdAtEpochMs DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE id = :id LIMIT 1")
    suspend fun getTicketById(id: String): SupportTicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateTicket(ticket: SupportTicketEntity)

    @Query("SELECT * FROM ticket_replies WHERE ticketId = :ticketId ORDER BY timestampEpochMs ASC")
    fun getRepliesForTicket(ticketId: String): Flow<List<TicketReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: TicketReplyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<TicketReplyEntity>)

    // --- Projects ---
    @Query("SELECT * FROM projects ORDER BY isSample DESC, startDate DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Query("SELECT * FROM project_milestones WHERE projectId = :projectId ORDER BY stepOrder ASC")
    fun getMilestonesForProject(projectId: String): Flow<List<ProjectMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<ProjectMilestoneEntity>)

    // --- Demo Data Reset ---
    @Query("DELETE FROM quotes")
    suspend fun clearQuotes()

    @Query("DELETE FROM support_tickets")
    suspend fun clearTickets()

    @Query("DELETE FROM ticket_replies")
    suspend fun clearReplies()

    @Query("DELETE FROM projects")
    suspend fun clearProjects()

    @Query("DELETE FROM project_milestones")
    suspend fun clearMilestones()
}
