package com.example.data.model

enum class BillingCycle {
    MONTHLY,
    YEARLY
}

data class IctPlan(
    val id: String,
    val name: String,
    val tag: String,
    val monthlyPriceEtb: Long,
    val yearlyPriceEtb: Long,
    val coverageHours: String,
    val responseSla: String,
    val maxWorkstations: Int,
    val preventiveVisitsPerMonth: Int,
    val includedFeatures: List<String>,
    val isPopular: Boolean = false
) {
    /**
     * Calculates annual savings if yearly price is lower than 12 * monthly price.
     * Returns 0 if no savings exist or if prices do not support that calculation.
     */
    fun calculateAnnualSavings(): Long {
        val fullYearMonthlyCost = monthlyPriceEtb * 12
        return if (yearlyPriceEtb < fullYearMonthlyCost) {
            fullYearMonthlyCost - yearlyPriceEtb
        } else {
            0L
        }
    }

    /**
     * Calculates annual savings percentage.
     */
    fun calculateAnnualSavingsPercent(): Int {
        val savings = calculateAnnualSavings()
        val fullYear = monthlyPriceEtb * 12
        return if (fullYear > 0 && savings > 0) {
            ((savings.toDouble() / fullYear.toDouble()) * 100).toInt()
        } else {
            0
        }
    }

    /**
     * Computes total price for selected cycle and optional tax.
     */
    fun computeBilledAmount(cycle: BillingCycle, includeVat: Boolean, vatRate: Double = CompanyConfig.DEFAULT_VAT_RATE): PlanBillingSummary {
        val baseAmount = when (cycle) {
            BillingCycle.MONTHLY -> monthlyPriceEtb
            BillingCycle.YEARLY -> yearlyPriceEtb
        }
        val vatAmount = if (includeVat) (baseAmount * vatRate).toLong() else 0L
        val totalAmount = baseAmount + vatAmount
        val annualSavings = if (cycle == BillingCycle.YEARLY) calculateAnnualSavings() else 0L

        return PlanBillingSummary(
            planName = name,
            cycle = cycle,
            baseAmountEtb = baseAmount,
            vatRate = if (includeVat) vatRate else 0.0,
            vatAmountEtb = vatAmount,
            totalAmountEtb = totalAmount,
            annualSavingsEtb = annualSavings
        )
    }
}

data class PlanBillingSummary(
    val planName: String,
    val cycle: BillingCycle,
    val baseAmountEtb: Long,
    val vatRate: Double,
    val vatAmountEtb: Long,
    val totalAmountEtb: Long,
    val annualSavingsEtb: Long
)

object PlanCatalog {
    val plans: List<IctPlan> = listOf(
        IctPlan(
            id = "plan_standard",
            name = "Standard Office ICT",
            tag = "Essential for small offices",
            monthlyPriceEtb = 12500L,
            yearlyPriceEtb = 120000L, // 12 * 12500 = 150,000 -> Saves 30,000 ETB (20%)
            coverageHours = "8x5 (Mon-Fri 8:30 AM - 5:30 PM)",
            responseSla = "Within 4 business hours",
            maxWorkstations = 15,
            preventiveVisitsPerMonth = 1,
            includedFeatures = listOf(
                "Up to 15 PC / Laptop workstations supported",
                "1 Office router & up to 2 network switches",
                "Monthly on-site hardware check & OS optimization",
                "Remote desktop helpdesk via AnyDesk/Phone",
                "Local data backup verification"
            )
        ),
        IctPlan(
            id = "plan_enterprise",
            name = "Business Enterprise",
            tag = "Most Popular for Growing Firms",
            monthlyPriceEtb = 28000L,
            yearlyPriceEtb = 270000L, // 12 * 28000 = 336,000 -> Saves 66,000 ETB (~19.6%)
            coverageHours = "12x6 (Mon-Sat 8:00 AM - 8:00 PM)",
            responseSla = "Within 1 hr critical / 2 hrs standard",
            maxWorkstations = 50,
            preventiveVisitsPerMonth = 2,
            includedFeatures = listOf(
                "Up to 50 workstations + servers + CCTV NVR",
                "Full office network, Wi-Fi 6 APs & printer fleet",
                "Bi-weekly on-site maintenance & thermal cleaning",
                "Priority helpdesk queue with dedicated technician",
                "Automated encrypted cloud/NAS data backup audit",
                "Quarterly IT infrastructure security review"
            ),
            isPopular = true
        ),
        IctPlan(
            id = "plan_mission_critical",
            name = "Mission-Critical 24/7 SLA",
            tag = "For 24/7 Ops & Multi-Branch Facilities",
            monthlyPriceEtb = 55000L,
            yearlyPriceEtb = 530000L, // 12 * 55000 = 660,000 -> Saves 130,000 ETB (~19.7%)
            coverageHours = "24/7/365 Round-the-clock availability",
            responseSla = "30 minutes emergency response",
            maxWorkstations = 150,
            preventiveVisitsPerMonth = 4,
            includedFeatures = listOf(
                "Unlimited workstations, server racks & multi-branch link",
                "Assigned Senior Systems & Network Engineer",
                "Active 24/7 uptime monitoring & rapid failover",
                "Weekly physical inspections & emergency on-site dispatch",
                "Disaster recovery planning & hot-standby equipment",
                "Monthly executive SLA performance scorecards"
            )
        )
    )

    fun getById(id: String): IctPlan? = plans.find { it.id == id }
}
