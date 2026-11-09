package com.example.data.model

/**
 * Centralized branding, configuration, and configurable placeholders
 * for Voltavia Technologies.
 */
object CompanyConfig {
    const val APP_NAME = "Voltavia Client Portal"
    const val SHORT_NAME = "Voltavia"
    const val COMPANY_NAME = "Voltavia Technologies"
    const val TAGLINE = "Smart Technology & Energy Solutions"
    const val LEAD_DEVELOPER = "Hayyu Ragea Chewaka"
    const val PRIMARY_MARKET = "Ethiopia"
    const val CURRENCY = "ETB"

    // Configurable public contact placeholders (can be replaced with client's live details)
    const val PHONE_PRIMARY = "+251911234567"
    const val PHONE_DISPLAY_PRIMARY = "+251 91 123 4567"
    const val PHONE_OFFICE = "+251116543210"
    const val PHONE_DISPLAY_OFFICE = "+251 11 654 3210"
    const val EMAIL_SUPPORT = "support@voltavia.com"
    const val WEBSITE_URL = "https://voltavia.com"
    const val HEADQUARTERS_ADDRESS = "Bole Sub-City, Addis Ababa, Ethiopia"
    const val BUSINESS_HOURS = "Mon - Fri: 8:30 AM - 5:30 PM (EAT) | Sat: 9:00 AM - 1:00 PM"
    
    // Tax handling configuration (Ethiopian VAT is typically 15%, kept configurable)
    const val DEFAULT_VAT_RATE = 0.15
}

enum class AppMode {
    OFFLINE_DEMO,
    PRODUCTION_CLOUD
}
