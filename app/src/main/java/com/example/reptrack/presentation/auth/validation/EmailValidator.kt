package com.example.reptrack.presentation.auth.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

class EmailValidator {
    var email by mutableStateOf<TextFieldValue?>(null)
    var emailError by mutableStateOf<String?>(null)
    var isEmailValid by mutableStateOf(false)

    companion object {
        private const val MAX_EMAIL_LENGTH = 254 // RFC standard

        // Basic email regex pattern
        private val EMAIL_REGEX = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        // SQL injection patterns to block
        private val SQL_INJECTION_PATTERNS = setOf(
            "'", "\"", "--", ";", "DROP", "UNION", "SELECT",
            "INSERT", "DELETE", "UPDATE", "ALTER", "CREATE",
            "<script>", "javascript:", "eval(", "alert(",
            "exec(", "xp_", "sp_", "CREATE", "ALTER", "DROP",
            "TRUNCATE", "GRANT", "REVOKE", "BACKUP", "RESTORE",
            "xp_cmdshell", "sp_oacreate", "sp_adduser", "sp_dropuser",
            "sp_addrole", "sp_droprole", "sp_helpdb", "sp_helpuser",
            "sp_helprole", "sp_helprotect", "sp_password", "sp_setapprole",
            "sp_dbfixedrole", "sp_dboption", "sp_configure", "sp_renamedb",
            "sp_who", "sp_who2", "sp_lock", "sp_help", "sp_helptext",
            "sp_depends", "sp_showadvancedopts", "sp_validatelist"
        )

        // Common disposable email domains (can be expanded)
        private val DISPOSABLE_EMAIL_DOMAINS = setOf(
            "tempmail", "10minutemail", "guerrillamail", "mailinator",
            "trashmail", "fakeinbox", "maildrop", "anonymbox",
            "guerrillamailblock", "sharklasers", "7zap", "60minutemail",
            "5minutemail", "0clickemail", "10minutemailx", "mailexpire"
        )

        // Suspicious patterns that might indicate spam or fake emails
        private val SUSPICIOUS_PATTERNS = setOf(
            "admin@", "support@", "noreply@", "no-reply@",
            "info@", "contact@", "service@", "help@",
            "billing@", "sales@", "marketing@", "abuse@"
        )
    }

    fun validate(): Boolean {
        val emailText = email?.text ?: run {
            emailError = "Email is required"
            return false
        }

        // Check empty
        if (emailText.isBlank()) {
            emailError = "Email is required"
            isEmailValid = false
            return false
        }

        // Check length
        if (emailText.length > MAX_EMAIL_LENGTH) {
            emailError = "Email address is too long (max $MAX_EMAIL_LENGTH characters)"
            isEmailValid = false
            return false
        }

        // Check for SQL injection patterns
        for (pattern in SQL_INJECTION_PATTERNS) {
            if (emailText.contains(pattern, ignoreCase = true)) {
                emailError = "Email contains invalid characters"
                isEmailValid = false
                return false
            }
        }

        // Check basic email format
        if (!EMAIL_REGEX.matches(emailText)) {
            emailError = "Please enter a valid email address"
            isEmailValid = false
            return false
        }

        // Check for suspicious patterns
        if (SUSPICIOUS_PATTERNS.any { emailText.lowercase().startsWith(it) }) {
            emailError = "Please use a personal email address"
            isEmailValid = false
            return false
        }

        // Check for disposable email domains
        val domain = emailText.substringAfterLast('@', "").lowercase()
        if (DISPOSABLE_EMAIL_DOMAINS.any { domain.contains(it) }) {
            emailError = "Please use a permanent email address"
            isEmailValid = false
            return false
        }

        emailError = null
        isEmailValid = true
        return true
    }

    fun validateSilently(): Boolean {
        val emailText = email?.text ?: return false
        return EMAIL_REGEX.matches(emailText) &&
               emailText.length <= MAX_EMAIL_LENGTH &&
               !SQL_INJECTION_PATTERNS.any { emailText.contains(it, ignoreCase = true) }
    }

    fun clear() {
        email = null
        emailError = null
        isEmailValid = false
    }

    fun getDomain(): String? {
        val emailText = email?.text ?: return null
        return emailText.substringAfterLast('@', "").takeIf { it.isNotEmpty() }
    }

    fun isDisposableEmail(): Boolean {
        val domain = getDomain()?.lowercase() ?: return false
        return DISPOSABLE_EMAIL_DOMAINS.any { domain.contains(it) }
    }

    fun getSuspiciousPattern(): String? {
        val emailText = email?.text?.lowercase() ?: return null
        return SUSPICIOUS_PATTERNS.find { emailText.startsWith(it) }
    }
}