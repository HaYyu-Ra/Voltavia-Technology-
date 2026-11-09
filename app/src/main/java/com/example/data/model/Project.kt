package com.example.data.model

enum class ProjectStage(val stepNumber: Int, val displayName: String) {
    SURVEY(1, "Site Survey & Load Audit"),
    DESIGN(2, "Engineering Blueprint & BOQ"),
    APPROVAL(3, "Client Sign-off & Procurement"),
    DEPLOYMENT(4, "Field Deployment & Wiring"),
    COMMISSIONING(5, "Testing & Commissioning"),
    HANDOVER(6, "Handover & SLA Signoff")
}

data class ProjectMilestone(
    val id: String,
    val projectId: String,
    val stage: ProjectStage,
    val isCompleted: Boolean,
    val isInProgress: Boolean,
    val completionDate: String? = null,
    val engineerNotes: String
)

data class ProjectItem(
    val id: String,
    val referenceCode: String,
    val title: String,
    val clientName: String,
    val serviceCategory: ServiceCategory,
    val siteLocation: String,
    val currentStage: ProjectStage,
    val progressPercent: Int,
    val startDate: String,
    val estimatedCompletionDate: String,
    val assignedLeadEngineer: String,
    val isSample: Boolean = true,
    val milestones: List<ProjectMilestone> = emptyList()
)

object SampleProjects {
    val sampleProjects: List<ProjectItem> = listOf(
        ProjectItem(
            id = "proj_sample_01",
            referenceCode = "PRJ-AA-2026-041",
            title = "Sheger Commercial Center Solar Hybrid 30kVA",
            clientName = "Sheger Plaza Management",
            serviceCategory = ServiceCategory.SOLAR,
            siteLocation = "Addis Ababa, Bole Medhanialem",
            currentStage = ProjectStage.DEPLOYMENT,
            progressPercent = 65,
            startDate = "Jan 12, 2026",
            estimatedCompletionDate = "Mar 30, 2026",
            assignedLeadEngineer = "Eng. Dawit Tesfaye",
            isSample = true,
            milestones = listOf(
                ProjectMilestone("m1_1", "proj_sample_01", ProjectStage.SURVEY, true, false, "Jan 15, 2026", "Roof structural inspection and 28.5 kW peak power audit completed."),
                ProjectMilestone("m1_2", "proj_sample_01", ProjectStage.DESIGN, true, false, "Jan 28, 2026", "Tier-1 solar array layout and 48V 600Ah LiFePO4 battery schematic approved."),
                ProjectMilestone("m1_3", "proj_sample_01", ProjectStage.APPROVAL, true, false, "Feb 05, 2026", "Import documentation and client contract executed."),
                ProjectMilestone("m1_4", "proj_sample_01", ProjectStage.DEPLOYMENT, false, true, null, "Roof racking anchored. Inverter cabinet installation in progress."),
                ProjectMilestone("m1_5", "proj_sample_01", ProjectStage.COMMISSIONING, false, false, null, "Scheduled 72-hour full load test and ATS switchover simulation."),
                ProjectMilestone("m1_6", "proj_sample_01", ProjectStage.HANDOVER, false, false, null, "Facility manager safety orientation and warranty documentation.")
            )
        ),
        ProjectItem(
            id = "proj_sample_02",
            referenceCode = "PRJ-AA-2026-088",
            title = "Abyssinia Logistics Cat6A Cabling & Wi-Fi 6",
            clientName = "Abyssinia Freight & Logistics",
            serviceCategory = ServiceCategory.NETWORKING,
            siteLocation = "Kality Industrial Zone, Addis Ababa",
            currentStage = ProjectStage.COMMISSIONING,
            progressPercent = 85,
            startDate = "Feb 02, 2026",
            estimatedCompletionDate = "Feb 28, 2026",
            assignedLeadEngineer = "Eng. Selamawit Hailu",
            isSample = true,
            milestones = listOf(
                ProjectMilestone("m2_1", "proj_sample_02", ProjectStage.SURVEY, true, false, "Feb 04, 2026", "Warehouse floor plan and 48 network drops mapped."),
                ProjectMilestone("m2_2", "proj_sample_02", ProjectStage.DESIGN, true, false, "Feb 08, 2026", "Server room rack topology and PoE distribution diagram verified."),
                ProjectMilestone("m2_3", "proj_sample_02", ProjectStage.APPROVAL, true, false, "Feb 11, 2026", "BOQ approved and materials dispatched from warehouse."),
                ProjectMilestone("m2_4", "proj_sample_02", ProjectStage.DEPLOYMENT, true, false, "Feb 20, 2026", "Cat6A pulling, trunking, patch panel termination completed."),
                ProjectMilestone("m2_5", "proj_sample_02", ProjectStage.COMMISSIONING, false, true, null, "Fluke DTX channel testing: 46 of 48 ports certified at 10Gbps."),
                ProjectMilestone("m2_6", "proj_sample_02", ProjectStage.HANDOVER, false, false, null, "Pending client IT sign-off and network diagram handover.")
            )
        ),
        ProjectItem(
            id = "proj_sample_03",
            referenceCode = "PRJ-AA-2026-102",
            title = "Finfinnee Agro ERP & Inventory Web Portal",
            clientName = "Finfinnee Agricultural Exports",
            serviceCategory = ServiceCategory.SOFTWARE,
            siteLocation = "Bishoftu / Addis Ababa Office",
            currentStage = ProjectStage.APPROVAL,
            progressPercent = 40,
            startDate = "Feb 15, 2026",
            estimatedCompletionDate = "May 15, 2026",
            assignedLeadEngineer = "Lead Dev. Hayyu Ragea Chewaka",
            isSample = true,
            milestones = listOf(
                ProjectMilestone("m3_1", "proj_sample_03", ProjectStage.SURVEY, true, false, "Feb 18, 2026", "Warehouse workflow and inventory audit requirements documented."),
                ProjectMilestone("m3_2", "proj_sample_03", ProjectStage.DESIGN, true, false, "Feb 27, 2026", "Database ERD, REST API spec, and Figma prototype validated."),
                ProjectMilestone("m3_3", "proj_sample_03", ProjectStage.APPROVAL, false, true, null, "Sprint backlog approval and cloud infrastructure provisioning."),
                ProjectMilestone("m3_4", "proj_sample_03", ProjectStage.DEPLOYMENT, false, false, null, "Frontend & backend development sprints."),
                ProjectMilestone("m3_5", "proj_sample_03", ProjectStage.COMMISSIONING, false, false, null, "UAT testing with finance and warehouse officers."),
                ProjectMilestone("m3_6", "proj_sample_03", ProjectStage.HANDOVER, false, false, null, "Data migration from spreadsheets and staff onboarding.")
            )
        )
    )
}
