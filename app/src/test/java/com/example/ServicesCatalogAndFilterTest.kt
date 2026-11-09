package com.example

import com.example.data.model.ServiceCatalog
import com.example.data.model.ServiceCategory
import com.example.data.repository.VoltaviaRepository
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ServiceSortOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VoltaviaDatabase
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ServicesCatalogAndFilterTest {

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
    fun testCoreServicesCatalogIntegrity() {
        // Verify the 3 major requested services exist and are richly populated
        val softwareSvc = ServiceCatalog.getById("svc_software")
        assertNotNull("Software service must exist", softwareSvc)
        assertEquals(ServiceCategory.SOFTWARE, softwareSvc?.category)
        assertTrue(softwareSvc?.technologies?.contains("Telebirr API") == true)
        assertTrue(softwareSvc?.faqs?.isNotEmpty() == true)

        val solarSvc = ServiceCatalog.getById("svc_solar")
        assertNotNull("Solar service must exist", solarSvc)
        assertEquals(ServiceCategory.SOLAR, solarSvc?.category)
        assertTrue(solarSvc?.technologies?.any { it.contains("LiFePO4") } == true)
        assertTrue(solarSvc?.faqs?.isNotEmpty() == true)

        val cctvSvc = ServiceCatalog.getById("svc_cctv")
        assertNotNull("CCTV service must exist", cctvSvc)
        assertEquals(ServiceCategory.CCTV, cctvSvc?.category)
        assertTrue(cctvSvc?.typicalScope?.any { it.contains("ColorVu") } == true)
        assertTrue(cctvSvc?.faqs?.isNotEmpty() == true)
    }

    @Test
    fun testCategoryFilteringInViewModel() = runBlocking {
        val viewModel = MainViewModel(repository)

        // Filter by SOFTWARE
        val softwareResults = viewModel.filterServices(category = ServiceCategory.SOFTWARE)
        assertTrue(softwareResults.all { it.category == ServiceCategory.SOFTWARE })
        assertTrue(softwareResults.any { it.id == "svc_software" })

        // Filter by SOLAR
        val solarResults = viewModel.filterServices(category = ServiceCategory.SOLAR)
        assertTrue(solarResults.all { it.category == ServiceCategory.SOLAR })
        assertTrue(solarResults.any { it.id == "svc_solar" })

        // Filter by CCTV
        val cctvResults = viewModel.filterServices(category = ServiceCategory.CCTV)
        assertTrue(cctvResults.all { it.category == ServiceCategory.CCTV })
        assertTrue(cctvResults.any { it.id == "svc_cctv" })

        // Reset to ALL
        val allResults = viewModel.filterServices(category = ServiceCategory.ALL)
        assertEquals(ServiceCatalog.services.size, allResults.size)
    }

    @Test
    fun testSearchFilteringAcrossTechnologiesAndScope() = runBlocking {
        val viewModel = MainViewModel(repository)

        // Search for "Telebirr"
        val telebirrResults = viewModel.filterServices(query = "Telebirr", category = ServiceCategory.ALL)
        assertTrue(telebirrResults.any { it.id == "svc_software" })

        // Search for "Inverter"
        val solarResults = viewModel.filterServices(query = "inverter", category = ServiceCategory.ALL)
        assertTrue(solarResults.any { it.id == "svc_solar" })

        // Search for "night vision"
        val cctvResults = viewModel.filterServices(query = "night vision", category = ServiceCategory.ALL)
        assertTrue(cctvResults.any { it.id == "svc_cctv" })
    }

    @Test
    fun testPriceSortingInViewModel() = runBlocking {
        val viewModel = MainViewModel(repository)

        // Sort Price Low to High
        val lowToHigh = viewModel.filterServices(query = "", category = ServiceCategory.ALL, sort = ServiceSortOrder.PRICE_LOW_HIGH)
        for (i in 0 until lowToHigh.size - 1) {
            assertTrue(lowToHigh[i].indicativeStartingPriceEtb <= lowToHigh[i + 1].indicativeStartingPriceEtb)
        }

        // Sort Price High to Low
        val highToLow = viewModel.filterServices(query = "", category = ServiceCategory.ALL, sort = ServiceSortOrder.PRICE_HIGH_LOW)
        for (i in 0 until highToLow.size - 1) {
            assertTrue(highToLow[i].indicativeStartingPriceEtb >= highToLow[i + 1].indicativeStartingPriceEtb)
        }
    }
}
