package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VoltaviaDatabase
import com.example.data.model.AppMode
import com.example.data.model.QuoteRequest
import com.example.data.model.SubmissionStatus
import com.example.data.model.SupportTicket
import com.example.data.model.TicketCategory
import com.example.data.model.TicketPriority
import com.example.data.repository.VoltaviaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VoltaviaPersistenceAndDeduplicationTest {

    private lateinit var db: VoltaviaDatabase
    private lateinit var repository: VoltaviaRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoltaviaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = VoltaviaRepository(db.voltaviaDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testRepeatableSeedingWithoutDuplicates() = runBlocking {
        // First seeding
        repository.seedSampleDataIfEmpty()
        val projectsFirst = repository.getAllProjects().first()
        val ticketsFirst = repository.getAllTickets().first()
        assertTrue(projectsFirst.isNotEmpty())
        assertTrue(ticketsFirst.isNotEmpty())
        val initialProjectCount = projectsFirst.size

        // Second seeding call (should be idempotent and not create duplicate projects)
        repository.seedSampleDataIfEmpty()
        val projectsSecond = repository.getAllProjects().first()
        assertEquals(initialProjectCount, projectsSecond.size)
    }

    @Test
    fun testDraftPersistenceAndReopening() = runBlocking {
        val draft = QuoteRequest(
            id = "draft_test_1",
            referenceNumber = "VLT-DEMO-2026-9999",
            customerName = "Dawit Alamu",
            phone = "+251 91 234 5678",
            email = "dawit@company.et",
            organization = "Alamu Logistics",
            cityOrSite = "Addis Ababa, Bole",
            serviceId = "svc_solar",
            serviceTitle = "Solar Energy Design & Installation",
            requirements = "Need 20kW rooftop solar system with LiFePO4 batteries.",
            isDraft = true,
            status = SubmissionStatus.DRAFT
        )

        repository.saveDraft(draft)

        val retrievedDraft = repository.getActiveDraft()
        assertNotNull(retrievedDraft)
        assertEquals("Dawit Alamu", retrievedDraft?.customerName)
        assertEquals("+251 91 234 5678", retrievedDraft?.phone)
        assertTrue(retrievedDraft?.isDraft == true)
        assertEquals(SubmissionStatus.DRAFT, retrievedDraft?.status)
    }

    @Test
    fun testDemoVsProductionSubmissionStatus() = runBlocking {
        // Offline Demo Submission: status must be SAVED_ON_DEVICE
        val demoQuote = QuoteRequest(
            id = "quote_demo_1",
            referenceNumber = "VLT-DEMO-2026-1234",
            customerName = "Tadesse Gemechu",
            phone = "+251 97 123 4567",
            email = "tadesse@ethio.et",
            cityOrSite = "Hawassa",
            serviceId = "svc_cctv",
            serviceTitle = "CCTV Installation & Surveillance",
            requirements = "16 IP cameras with NVR."
        )

        val demoResult = repository.submitQuote(demoQuote, isProduction = false)
        assertTrue(demoResult.isSuccess)
        assertEquals(SubmissionStatus.SAVED_ON_DEVICE, demoResult.getOrNull()?.status)

        // Production Submission: status must be SUBMITTED
        val prodQuote = QuoteRequest(
            id = "quote_prod_1",
            referenceNumber = "VLT-PROD-2026-5678",
            customerName = "Sara Bekele",
            phone = "+251 91 999 8888",
            email = "sara@company.et",
            cityOrSite = "Addis Ababa",
            serviceId = "svc_network",
            serviceTitle = "Office Networking & Structured Cabling",
            requirements = "Cat6A structured cabling for 32 drops."
        )

        val prodResult = repository.submitQuote(prodQuote, isProduction = true)
        assertTrue(prodResult.isSuccess)
        assertEquals(SubmissionStatus.SUBMITTED, prodResult.getOrNull()?.status)
    }

    @Test
    fun testSupportTicketAndReplyThreadPersistence() = runBlocking {
        val ticket = SupportTicket(
            id = "test_tkt_1",
            referenceCode = "TKT-DEMO-1111",
            category = TicketCategory.SOLAR_POWER,
            priority = TicketPriority.HIGH,
            subject = "Hybrid inverter error code 04",
            description = "Inverter restarted twice during heavy morning load.",
            contactPerson = "Kenenisa",
            createdAtFormatted = "Today, 10:00 AM"
        )

        val ticketResult = repository.createTicket(ticket, isProduction = false)
        assertTrue(ticketResult.isSuccess)

        // Post a client reply
        repository.addTicketReply(
            ticketId = "test_tkt_1",
            senderName = "Kenenisa",
            isClient = true,
            message = "Battery voltage reads 52.4V currently."
        )

        val replies = repository.getTicketReplies("test_tkt_1").first()
        assertEquals(1, replies.size)
        assertEquals("Battery voltage reads 52.4V currently.", replies.first().message)
        assertTrue(replies.first().isClientReply)
    }

    @Test
    fun testResetDemoDataRestoresSampleData() = runBlocking {
        // Insert custom quote
        val customQuote = QuoteRequest(
            id = "custom_temp",
            referenceNumber = "VLT-DEMO-TEMP",
            customerName = "Temporary User",
            phone = "+251 91 000 0000",
            email = "temp@temp.et",
            cityOrSite = "Adama",
            serviceId = "svc_software",
            serviceTitle = "Website & Software",
            requirements = "Test request"
        )
        repository.submitQuote(customQuote, isProduction = false)
        val quotesBefore = repository.getAllQuotes().first()
        assertTrue(quotesBefore.any { it.customerName == "Temporary User" })

        // Reset demo data
        repository.resetDemoData()

        val quotesAfter = repository.getAllQuotes().first()
        // Custom quote should be wiped, sample projects restored
        assertTrue(quotesAfter.none { it.customerName == "Temporary User" })
        val projects = repository.getAllProjects().first()
        assertTrue(projects.isNotEmpty())
    }
}
