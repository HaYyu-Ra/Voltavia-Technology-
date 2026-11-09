package com.example.data.model

import java.util.UUID

enum class SubmissionStatus {
    SAVED_ON_DEVICE, // Offline demo local record
    DRAFT,           // Unsubmitted draft
    SYNCING,         // In transit
    SUBMITTED,       // Confirmed by backend
    FAILED           // Network error
}

data class QuoteRequest(
    val id: String = UUID.randomUUID().toString(),
    val referenceNumber: String,
    val customerName: String,
    val phone: String,
    val email: String,
    val organization: String = "",
    val cityOrSite: String,
    val serviceId: String,
    val serviceTitle: String,
    val requirements: String,
    val budgetEtb: Long? = null,
    val targetDate: String = "",
    val isDraft: Boolean = false,
    val status: SubmissionStatus = SubmissionStatus.SAVED_ON_DEVICE,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

object QuoteValidationHelper {
    /**
     * Ethiopian phone validation:
     * Accepts:
     * - +251 9... / +251 7... (with optional spaces/dashes)
     * - 09... / 07... (10 digits)
     * - 9... / 7... (9 digits)
     */
    fun isValidEthiopianPhone(raw: String): Boolean {
        val clean = raw.replace("[\\s\\-\\(\\)]".toRegex(), "")
        if (clean.isBlank()) return false
        
        // Pattern 1: +251 followed by 9 or 7 and 8 more digits (total 13 chars)
        if (clean.startsWith("+251")) {
            val rest = clean.substring(4)
            return rest.length == 9 && (rest.startsWith("9") || rest.startsWith("7")) && rest.all { it.isDigit() }
        }
        // Pattern 2: 251 followed by 9 or 7 and 8 more digits (total 12 chars)
        if (clean.startsWith("251")) {
            val rest = clean.substring(3)
            return rest.length == 9 && (rest.startsWith("9") || rest.startsWith("7")) && rest.all { it.isDigit() }
        }
        // Pattern 3: 09... or 07... (10 digits)
        if (clean.startsWith("09") || clean.startsWith("07")) {
            return clean.length == 10 && clean.all { it.isDigit() }
        }
        // Pattern 4: 9... or 7... (9 digits)
        if (clean.startsWith("9") || clean.startsWith("7")) {
            return clean.length == 9 && clean.all { it.isDigit() }
        }
        return false
    }

    /**
     * Normalizes an accepted Ethiopian phone number to standard format: "+251 9XX XXX XXX"
     */
    fun normalizeEthiopianPhone(raw: String): String {
        val clean = raw.replace("[^0-9+]".toRegex(), "")
        val digits = if (clean.startsWith("+251")) {
            clean.substring(4)
        } else if (clean.startsWith("251")) {
            clean.substring(3)
        } else if (clean.startsWith("0")) {
            clean.substring(1)
        } else {
            clean
        }

        return if (digits.length == 9 && (digits.startsWith("9") || digits.startsWith("7"))) {
            "+251 ${digits.substring(0, 2)} ${digits.substring(2, 5)} ${digits.substring(5)}"
        } else {
            raw.trim()
        }
    }

    /**
     * Basic standard email validation.
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email.trim())
    }

    /**
     * Generates a unique reference ID.
     * Offline demo references begin with VLT-DEMO-2026-XXXX.
     * Production cloud references begin with VLT-PROD-2026-XXXX.
     */
    fun generateReference(isProduction: Boolean): String {
        val prefix = if (isProduction) "VLT-PROD-2026" else "VLT-DEMO-2026"
        val randomSuffix = (1000..9999).random()
        return "$prefix-$randomSuffix"
    }
}
