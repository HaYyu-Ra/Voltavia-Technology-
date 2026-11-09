package com.example.data.model

data class DeliveryStep(
    val stepNumber: Int,
    val title: String,
    val description: String
)

data class ServiceFaq(
    val question: String,
    val answer: String
)

data class ServiceItem(
    val id: String,
    val title: String,
    val category: ServiceCategory,
    val shortSummary: String,
    val fullDescription: String,
    val typicalScope: List<String>,
    val deliverySteps: List<DeliveryStep>,
    val indicativeStartingPriceEtb: Long,
    val estimatedDuration: String,
    val technologies: List<String>,
    val keyDeliverables: List<String>,
    val faqs: List<ServiceFaq> = emptyList(),
    val priceNote: String = "Indicative estimate pending site survey or detailed scope review. Not a binding quote."
)

enum class ServiceCategory(val displayName: String) {
    ALL("All Services"),
    SOFTWARE("Website & Software"),
    SOLAR("Solar Energy"),
    CCTV("CCTV Security"),
    NETWORKING("Networking"),
    ICT_SUPPORT("ICT Support")
}

object ServiceCatalog {
    val services: List<ServiceItem> = listOf(
        ServiceItem(
            id = "svc_software",
            title = "Website & Software Development",
            category = ServiceCategory.SOFTWARE,
            shortSummary = "Custom web portals, enterprise management software, and reliable business applications.",
            fullDescription = "Voltavia Technologies crafts tailored software solutions for Ethiopian enterprises, government agencies, and growing businesses. From cloud-native web portals to robust local ERP backends, we build for high performance, security, and scalability. We specialize in seamless integrations with Ethiopian financial systems including Telebirr, CBE Birr, and Chapa.",
            typicalScope = listOf(
                "Modern responsive frontend (Web, Portal, Admin Dashboards)",
                "Secure REST / GraphQL backend APIs & database architecture",
                "Integration with local payment gateways (Telebirr, CBE Birr, Chapa)",
                "Role-based access control (RBAC) and audit trail logging",
                "Automated encrypted backups and deployment on cloud or on-premise servers"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "Discovery & Specification", "Detailed requirements workshop, functional user stories, system architecture, and API schema."),
                DeliveryStep(2, "UI/UX & Interactive Prototype", "High-fidelity clickable Figma prototype and stakeholder approval before coding."),
                DeliveryStep(3, "Agile Sprints & Testing", "Bi-weekly sprint demos, automated unit testing, code quality audits, and payment sandbox tests."),
                DeliveryStep(4, "User Acceptance & Staging", "Staging environment validation, user acceptance testing (UAT), and data migration."),
                DeliveryStep(5, "Production Launch & SLA", "Domain DNS setup, SSL certification, staff onboarding, and 90-day warranty coverage.")
            ),
            indicativeStartingPriceEtb = 45000L,
            estimatedDuration = "3 – 6 Weeks",
            technologies = listOf("Kotlin", "Jetpack Compose", "React / Next.js", "Node.js", "PostgreSQL", "Telebirr API", "Chapa"),
            keyDeliverables = listOf(
                "Full source code ownership and repository access",
                "Responsive client portal and role-based admin panel",
                "Production deployment documentation and system architecture map",
                "90-day post-launch technical warranty and bug fixes"
            ),
            faqs = listOf(
                ServiceFaq(
                    "Do you integrate local Ethiopian payment gateways?",
                    "Yes, we build official integrations with Telebirr, Commercial Bank of Ethiopia (CBE Birr), and Chapa payment gateways with automated reconciliation."
                ),
                ServiceFaq(
                    "Can the software run on local servers without constant internet?",
                    "Yes, we engineer offline-capable architectures with local synchronization so operations continue smoothly during internet outages."
                ),
                ServiceFaq(
                    "Do we own the intellectual property and source code?",
                    "Yes, 100% of the custom source code and database assets are transferred to your organization upon project handover."
                )
            )
        ),
        ServiceItem(
            id = "svc_solar",
            title = "Solar Energy Design & Installation",
            category = ServiceCategory.SOLAR,
            shortSummary = "Clean, uninterrupted photovoltaic power systems for commercial centers, offices, and residences.",
            fullDescription = "Voltavia delivers turnkey photovoltaic (PV) solar energy solutions engineered for Ethiopian grid conditions. We design rooftop solar arrays, pure sine wave hybrid inverters, and long-life Lithium Iron Phosphate (LiFePO4) battery banks that eliminate costly generator fuel expenses and power outages.",
            typicalScope = listOf(
                "Comprehensive on-site electrical load audit and peak usage profiling",
                "Tier-1 high-efficiency monocrystalline solar panels with 25-year performance warranty",
                "Pure sine wave hybrid inverters (5kVA to 50kVA+ modular configurations)",
                "Safe, fire-rated Lithium-ion (LiFePO4) battery storage banks (6,000+ lifecycle)",
                "Automatic Transfer Switches (ATS), DC/AC disconnects, surge arrestors, and copper grounding rods"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "Site Load Audit", "On-site energy consumption measurement, electrical panel inspection, and roof sun exposure survey."),
                DeliveryStep(2, "Engineering Blueprint & BOQ", "Exact sizing calculation of solar PV array, battery capacity, inverter sizing, and bill of quantities."),
                DeliveryStep(3, "Equipment Procurement & Mounting", "Delivery of genuine certified components, heavy-duty anti-corrosion roof racks, and panel mounting."),
                DeliveryStep(4, "Inverter & Battery Wiring", "DC cabling in metal conduits, lightning/surge protection, and battery management system (BMS) integration."),
                DeliveryStep(5, "Commissioning & Safety Handover", "72-hour full-load stress test, ATS grid failover simulation, client operator training, and warranty certification.")
            ),
            indicativeStartingPriceEtb = 120000L,
            estimatedDuration = "5 – 10 Days",
            technologies = listOf("Tier-1 Monocrystalline PV", "Pure Sine Hybrid Inverters", "LiFePO4 Lithium Batteries", "MPPT Charge Controllers", "Class-I Surge Arrestors"),
            keyDeliverables = listOf(
                "Complete turnkey photovoltaic system installed and tested",
                "Automatic Transfer Switch (ATS) for seamless 10ms power switchover",
                "Comprehensive electrical single-line diagram (SLD) and load chart",
                "Manufacturer component warranties (up to 25 years on panels, 5 years on batteries)"
            ),
            faqs = listOf(
                ServiceFaq(
                    "How fast does the system switch over during an Ethiopian Electric Utility (EEU) outage?",
                    "Our pure sine hybrid inverters switch within 10 milliseconds—fast enough that computers, servers, and sensitive electronics never reset."
                ),
                ServiceFaq(
                    "How long do the Lithium (LiFePO4) batteries last?",
                    "Unlike traditional lead-acid batteries that fail after 1-2 years, our LiFePO4 batteries deliver 6,000+ charge cycles, lasting 10 to 15 years with minimal maintenance."
                ),
                ServiceFaq(
                    "Can we expand the system capacity later as our business grows?",
                    "Yes, all Voltavia solar solutions are modular. You can add extra solar panels and battery modules without replacing the central inverter."
                )
            )
        ),
        ServiceItem(
            id = "svc_cctv",
            title = "CCTV Installation & Surveillance",
            category = ServiceCategory.CCTV,
            shortSummary = "Ultra-HD IP surveillance, full-color night vision, and secure remote monitoring for facilities.",
            fullDescription = "Safeguard your commercial premises, warehouses, and offices with professional security surveillance. Voltavia installs commercial-grade IP camera networks featuring 4K Ultra-HD resolution, ColorVu 24/7 full-color night vision, Network Video Recorders (NVR), and remote smartphone live monitoring.",
            typicalScope = listOf(
                "4MP / 8MP 4K Ultra-HD Weatherproof (IP67) IP bullet, dome, and PTZ cameras",
                "ColorVu / Full-color 24/7 night vision and advanced infrared illumination",
                "Centralized Network Video Recorder (NVR) with surveillance-grade Western Digital Purple hard drives",
                "Power over Ethernet (PoE) cabling with surge protection and concealed conduits",
                "Remote mobile app viewing on Android/iOS and multi-screen security monitoring station setup"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "Site Security Survey", "Perimeter walk, identifying critical entry points, blind spots, lighting conditions, and cable routes."),
                DeliveryStep(2, "Camera Layout & Network Design", "Optimized field of view (FOV) planning, lens selection, and bandwidth/storage calculation."),
                DeliveryStep(3, "Structured Cabling & Mounting", "Concealed conduit installation, anti-vandal camera mounting, and PoE termination."),
                DeliveryStep(4, "NVR Configuration & Remote Link", "Continuous/motion recording schedules, AI human/vehicle intrusion zones, and encrypted mobile app link."),
                DeliveryStep(5, "Staff Training & Documentation", "Operator walkthrough, incident playback retrieval tutorial, and system documentation handover.")
            ),
            indicativeStartingPriceEtb = 38000L,
            estimatedDuration = "3 – 7 Days",
            technologies = listOf("4K Ultra-HD IP Cameras", "ColorVu Night Vision", "PoE Switching", "H.265+ Compression", "AI Perimeter Detection"),
            keyDeliverables = listOf(
                "Configured camera system with zero blind spots on vital premises",
                "Centralized NVR with minimum 30 days continuous video retention",
                "Mobile surveillance app configured on authorized staff smartphones",
                "1-year comprehensive hardware warranty and support SLA"
            ),
            faqs = listOf(
                ServiceFaq(
                    "Can I view camera feeds on my phone when I am away from the office?",
                    "Yes, we configure secure remote monitoring on your Android/iOS smartphones with push alerts when human or vehicle motion is detected after business hours."
                ),
                ServiceFaq(
                    "Do the cameras continue recording when the power cuts off?",
                    "When paired with a dedicated UPS or Voltavia solar backup system, the CCTV surveillance network records 24/7 without interruption."
                ),
                ServiceFaq(
                    "How long is video footage stored before being overwritten?",
                    "We size the surveillance hard drives based on your requirements, typically providing 30 to 90 days of high-definition recording."
                )
            )
        ),
        ServiceItem(
            id = "svc_mobile",
            title = "Mobile App Development",
            category = ServiceCategory.SOFTWARE,
            shortSummary = "Native Android and cross-platform mobile apps built for African connectivity environments.",
            fullDescription = "Our mobile engineering team builds responsive Android applications designed to perform reliably under variable network conditions with robust offline caching and fast sync.",
            typicalScope = listOf(
                "Native Kotlin / Jetpack Compose Android client development",
                "Offline-first architecture with local SQLite/Room data caching",
                "Push notifications, SMS OTP verification, and biometric auth",
                "Optimized data consumption for Ethiopian mobile carriers (Ethio Telecom/Safaricom)",
                "Google Play Store publishing and version maintenance"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "UX Journey & Edge Cases", "Screen flow mapping and offline state planning."),
                DeliveryStep(2, "API Contracts & Mocking", "Establishing secure endpoint payloads and data synchronization schemas."),
                DeliveryStep(3, "Native Implementation", "Iterative UI builds, Material 3 theming, and device testing."),
                DeliveryStep(4, "Field Testing & Store Release", "Testing across low-end and high-end devices, followed by Play Store release."),
                DeliveryStep(5, "Monitoring & Updates", "Crashlytics monitoring and seamless patch updates.")
            ),
            indicativeStartingPriceEtb = 55000L,
            estimatedDuration = "4 – 8 Weeks",
            technologies = listOf("Kotlin", "Jetpack Compose", "Room Database", "Coroutines & Flow", "Retrofit", "Material 3"),
            keyDeliverables = listOf(
                "Signed production APK and AAB for Google Play Store",
                "Clean native Kotlin source code with MVVM architecture",
                "Offline data cache integration and sync worker"
            )
        ),
        ServiceItem(
            id = "svc_network",
            title = "Office Networking & Structured Cabling",
            category = ServiceCategory.NETWORKING,
            shortSummary = "Enterprise Cat6/Cat6A cabling, server racks, managed switches, and seamless Wi-Fi 6 coverage.",
            fullDescription = "Build a fast, resilient network backbone for your enterprise. We engineer structured cabling, server rack patch panels, managed VLAN switching, firewalls, and enterprise-grade wireless access points with zero dead zones.",
            typicalScope = listOf(
                "Cat6 / Cat6A UTP/FTP horizontal cabling and trunking/conduits",
                "Server rack assembly, cable management trays, and labeled patch panels",
                "Managed Gigabit / 10G PoE switches with VLAN segmentation for voice & data",
                "Enterprise Wi-Fi 6 mesh access points with guest portal isolation",
                "Fiber optic uplink termination, fusion splicing, and cable Fluke certification"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "Blueprint & Density Audit", "Reviewing office floor plans, workstation counts, and server room location."),
                DeliveryStep(2, "Trunking & Pathway Installation", "Installing clean PVC/metal cable ducts and conduit pathways."),
                DeliveryStep(3, "Cable Pulling & Termination", "High-spec cable routing, keystone jack punching, and patch panel termination."),
                DeliveryStep(4, "Rack Mounting & Switch Config", "Switch VLAN setup, router firewall rules, and Wi-Fi SSID configuration."),
                DeliveryStep(5, "Fluke Testing & Certification", "End-to-end cable continuity/speed testing, labeling, and network topology map.")
            ),
            indicativeStartingPriceEtb = 32000L,
            estimatedDuration = "4 – 8 Days",
            technologies = listOf("Cat6A UTP/FTP", "Managed PoE Switches", "Wi-Fi 6 Access Points", "Server Racks", "Fluke DTX"),
            keyDeliverables = listOf(
                "Certified structured cabling drops with numbered faceplates",
                "Assembled server rack with labeled patch panels and cable management",
                "Network topology documentation and Wi-Fi coverage map"
            )
        ),
        ServiceItem(
            id = "svc_ict_support",
            title = "ICT Support & Maintenance",
            category = ServiceCategory.ICT_SUPPORT,
            shortSummary = "Proactive IT maintenance, rapid helpdesk resolution, and scheduled preventive care.",
            fullDescription = "Keep your team productive without dedicated in-house IT overhead. Voltavia provides SLA-backed ICT support covering workstations, printers, network routers, data backups, antivirus, and software troubleshooting.",
            typicalScope = listOf(
                "Monthly on-site preventive maintenance and hardware cleaning",
                "Rapid helpdesk remote support via AnyDesk / TeamViewer / Phone",
                "Windows / macOS / Linux OS maintenance, patching, and antivirus management",
                "Automated off-site and local backup scheduling for critical financial data",
                "Printer sharing, scanner configuration, and local network troubleshooting"
            ),
            deliverySteps = listOf(
                DeliveryStep(1, "IT Asset Inventory", "Complete audit and serial tracking of all company computers, network gear, and printers."),
                DeliveryStep(2, "Preventive Baseline Cleanup", "OS updates, security hardening, malware scanning, and thermal cleaning."),
                DeliveryStep(3, "Helpdesk Onboarding", "Providing staff with direct ticketing channels and fast response contacts."),
                DeliveryStep(4, "Routine Visits & Audits", "Scheduled monthly on-site checkups and proactive hardware diagnostics."),
                DeliveryStep(5, "Monthly Executive Reports", "Health status summary, incident resolution log, and upgrade recommendations.")
            ),
            indicativeStartingPriceEtb = 12500L,
            estimatedDuration = "Annual SLA / Monthly",
            technologies = listOf("AnyDesk Helpdesk", "Active Directory", "Automated NAS Backup", "Hardware Diagnostics", "Antivirus Management"),
            keyDeliverables = listOf(
                "Guaranteed SLA response times (30 mins to 4 hours)",
                "Monthly hardware preventive maintenance visits",
                "Monthly IT infrastructure health report and upgrade roadmap"
            )
        )
    )

    fun getById(id: String): ServiceItem? = services.find { it.id == id }
}
