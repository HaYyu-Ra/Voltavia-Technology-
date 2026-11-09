package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VoltaviaDatabase
import com.example.data.repository.VoltaviaRepository
import com.example.ui.screens.QuoteFormScreen
import com.example.ui.theme.VoltaviaTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VoltaviaComposeFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: VoltaviaDatabase
    private lateinit var repository: VoltaviaRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoltaviaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = VoltaviaRepository(db.voltaviaDao())
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testCompleteDemoQuoteFlow() {
        var submittedCalled = false

        composeTestRule.setContent {
            VoltaviaTheme {
                QuoteFormScreen(
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onQuoteSubmitted = { submittedCalled = true },
                    onSwitchMode = {}
                )
            }
        }

        // Provide input via ViewModel form state
        viewModel.updateQuoteFormField(
            customerName = "Hayyu Ragea",
            phone = "0911234567",
            cityOrSite = "Addis Ababa, Bole",
            requirements = "50kVA commercial solar microgrid installation."
        )
        composeTestRule.waitForIdle()

        // Click Review Details button in the bottom action bar
        composeTestRule.onNodeWithTag("action_review_submit_button").performClick()
        composeTestRule.waitForIdle()

        // Review card should now be displayed in Compose UI
        composeTestRule.onNodeWithTag("quote_review_card").assertIsDisplayed()

        // Click Submit / Save Locally button
        composeTestRule.onNodeWithTag("submit_quote_button").performClick()
        composeTestRule.waitForIdle()

        runBlocking {
            var saved: com.example.data.model.QuoteRequest? = null
            for (i in 1..25) {
                org.robolectric.shadows.ShadowLooper.idleMainLooper()
                val quotes = repository.getAllQuotes().first()
                saved = quotes.firstOrNull { it.customerName == "Hayyu Ragea" }
                if (saved != null) break
                kotlinx.coroutines.delay(50)
            }
            org.junit.Assert.assertNotNull("Submitted quote should be persisted in repository", saved)
            assertEquals("+251 91 123 4567", saved?.phone) // normalized
            assertEquals("Addis Ababa, Bole", saved?.cityOrSite)
            assertTrue(saved?.referenceNumber?.startsWith("VLT-DEMO-2026-") == true)
        }
    }
}
