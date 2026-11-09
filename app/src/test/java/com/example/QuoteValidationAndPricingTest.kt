package com.example

import com.example.data.model.BillingCycle
import com.example.data.model.PlanCatalog
import com.example.data.model.QuoteValidationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteValidationAndPricingTest {

    @Test
    fun testEthiopianPhoneValidation() {
        // Valid international formats
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("+251911234567"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("+251 91 123 4567"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("+251712345678"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("251911234567"))

        // Valid local formats
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("0911234567"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("0712345678"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("0911-234-567"))
        assertTrue(QuoteValidationHelper.isValidEthiopianPhone("911234567"))

        // Invalid phone numbers
        assertFalse(QuoteValidationHelper.isValidEthiopianPhone(""))
        assertFalse(QuoteValidationHelper.isValidEthiopianPhone("12345"))
        assertFalse(QuoteValidationHelper.isValidEthiopianPhone("0811234567")) // 08 is not Ethiopian mobile
        assertFalse(QuoteValidationHelper.isValidEthiopianPhone("+254712345678")) // Kenya code
        assertFalse(QuoteValidationHelper.isValidEthiopianPhone("abc1234567"))
    }

    @Test
    fun testEthiopianPhoneNormalization() {
        val norm1 = QuoteValidationHelper.normalizeEthiopianPhone("0911234567")
        assertEquals("+251 91 123 4567", norm1)

        val norm2 = QuoteValidationHelper.normalizeEthiopianPhone("+251911234567")
        assertEquals("+251 91 123 4567", norm2)

        val norm3 = QuoteValidationHelper.normalizeEthiopianPhone("0712345678")
        assertEquals("+251 71 234 5678", norm3)

        val norm4 = QuoteValidationHelper.normalizeEthiopianPhone("911234567")
        assertEquals("+251 91 123 4567", norm4)
    }

    @Test
    fun testEmailValidation() {
        assertTrue(QuoteValidationHelper.isValidEmail("abebe@voltavia.com"))
        assertTrue(QuoteValidationHelper.isValidEmail("client.contact@company.et"))
        assertFalse(QuoteValidationHelper.isValidEmail("invalid-email"))
        assertFalse(QuoteValidationHelper.isValidEmail("@nodomain.com"))
        assertFalse(QuoteValidationHelper.isValidEmail(""))
    }

    @Test
    fun testReferenceGenerationDistinctPrefixes() {
        val demoRef = QuoteValidationHelper.generateReference(isProduction = false)
        assertTrue("Demo ref must start with VLT-DEMO-2026-", demoRef.startsWith("VLT-DEMO-2026-"))

        val prodRef = QuoteValidationHelper.generateReference(isProduction = true)
        assertTrue("Prod ref must start with VLT-PROD-2026-", prodRef.startsWith("VLT-PROD-2026-"))
    }

    @Test
    fun testAnnualPlanSavingsCalculations() {
        val standardPlan = PlanCatalog.getById("plan_standard")!!
        // Standard: 12500 * 12 = 150000; yearly = 120000; savings = 30000
        val savings = standardPlan.calculateAnnualSavings()
        assertEquals(30000L, savings)
        assertEquals(20, standardPlan.calculateAnnualSavingsPercent())

        val enterprisePlan = PlanCatalog.getById("plan_enterprise")!!
        // Enterprise: 28000 * 12 = 336000; yearly = 270000; savings = 66000
        assertEquals(66000L, enterprisePlan.calculateAnnualSavings())

        // Test configurable tax calculation (15% VAT)
        val billingMonthly = standardPlan.computeBilledAmount(BillingCycle.MONTHLY, includeVat = true)
        assertEquals(12500L, billingMonthly.baseAmountEtb)
        assertEquals(1875L, billingMonthly.vatAmountEtb) // 12500 * 0.15 = 1875
        assertEquals(14375L, billingMonthly.totalAmountEtb)

        val billingYearlyNoVat = standardPlan.computeBilledAmount(BillingCycle.YEARLY, includeVat = false)
        assertEquals(120000L, billingYearlyNoVat.baseAmountEtb)
        assertEquals(0L, billingYearlyNoVat.vatAmountEtb)
        assertEquals(120000L, billingYearlyNoVat.totalAmountEtb)
        assertEquals(30000L, billingYearlyNoVat.annualSavingsEtb)
    }
}
