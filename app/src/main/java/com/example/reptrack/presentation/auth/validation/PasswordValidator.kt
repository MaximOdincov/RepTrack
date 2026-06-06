package com.example.reptrack.presentation.auth.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

class PasswordValidator {
    var password by mutableStateOf<TextFieldValue?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var passwordStrength by mutableStateOf<Int>(0) // 0-4 scale
    var isPasswordValid by mutableStateOf(false)

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 128

        // List of weak passwords to block
        private val WEAK_PASSWORDS = setOf(
            "password", "Password", "PASSWORD",
            "qwerty", "QWERTY", "qwerty123",
            "12345678", "87654321", "123456",
            "abc123", "abcdefgh", "abcdefg",
            "letmein", "welcome", "admin",
            "football", "baseball", "dragon",
            "master", "monkey", "sunshine",
            "iloveyou", "123123", "123456789",
            "jordan23", "michael23", "batman",
            "superman", "hello123", "starwars",
            "pokemon", "ninja", "matrix",
            "trustno1", "computer", "password1",
            "michael", "shadow", "alpha",
            "passw0rd", "Pa\$\$w0rd", "P@ssw0rd"
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

        // Special characters that might be problematic
        private val DANGEROUS_CHARS = setOf('<', '>', '&', '|', ';', '`', '$', '#')
    }

    fun validate(): Boolean {
        val passwordText = password?.text ?: run {
            passwordError = "Password is required"
            return false
        }

        // Check length
        if (passwordText.length < MIN_LENGTH) {
            passwordError = "Password must be at least $MIN_LENGTH characters long"
            isPasswordValid = false
            updatePasswordStrength(passwordText)
            return false
        }

        if (passwordText.length > MAX_LENGTH) {
            passwordError = "Password must be less than $MAX_LENGTH characters"
            isPasswordValid = false
            updatePasswordStrength(passwordText)
            return false
        }

        // Check for weak passwords
        if (WEAK_PASSWORDS.contains(passwordText.lowercase())) {
            passwordError = "This is a commonly used password. Please choose a stronger one."
            isPasswordValid = false
            updatePasswordStrength(passwordText)
            return false
        }

        // Check for SQL injection patterns
        for (pattern in SQL_INJECTION_PATTERNS) {
            if (passwordText.contains(pattern, ignoreCase = true)) {
                passwordError = "Password contains potentially dangerous characters"
                isPasswordValid = false
                updatePasswordStrength(passwordText)
                return false
            }
        }

        // Check for dangerous characters
        for (char in DANGEROUS_CHARS) {
            if (passwordText.contains(char)) {
                passwordError = "Password contains invalid characters"
                isPasswordValid = false
                updatePasswordStrength(passwordText)
                return false
            }
        }

        // Check character requirements
        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false
        var hasSpecialChar = false

        for (char in passwordText) {
            when {
                char.isUpperCase() -> hasUpperCase = true
                char.isLowerCase() -> hasLowerCase = true
                char.isDigit() -> hasDigit = true
                else -> hasSpecialChar = true
            }
        }

        val missingRequirements = mutableListOf<String>()
        if (!hasUpperCase) missingRequirements.add("uppercase letter")
        if (!hasLowerCase) missingRequirements.add("lowercase letter")
        if (!hasDigit) missingRequirements.add("digit")

        if (missingRequirements.isNotEmpty()) {
            passwordError = "Password must contain: ${missingRequirements.joinToString(", ")}"
            isPasswordValid = false
            updatePasswordStrength(passwordText)
            return false
        }

        passwordError = null
        isPasswordValid = true
        updatePasswordStrength(passwordText)
        return true
    }

    private fun updatePasswordStrength(passwordText: String) {
        var score = 0

        // Length points (max 2)
        if (passwordText.length >= 12) score += 2
        else if (passwordText.length >= 8) score += 1

        // Character type points
        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false
        var hasSpecialChar = false

        for (char in passwordText) {
            when {
                char.isUpperCase() -> hasUpperCase = true
                char.isLowerCase() -> hasLowerCase = true
                char.isDigit() -> hasDigit = true
                else -> hasSpecialChar = true
            }
        }

        if (hasUpperCase) score += 1
        if (hasLowerCase) score += 1
        if (hasDigit) score += 1
        if (hasSpecialChar) score += 1

        // Bonus for very long passwords
        if (passwordText.length >= 16) score += 1

        passwordStrength = minOf(score, 4)
    }

    fun getStrengthText(): String {
        return when (passwordStrength) {
            0 -> "Very Weak"
            1 -> "Weak"
            2 -> "Medium"
            3 -> "Strong"
            4 -> "Very Strong"
            else -> "Unknown"
        }
    }

    fun getStrengthColor(): String {
        return when (passwordStrength) {
            0 -> "#FF5252" // Red
            1 -> "#FF9800" // Orange
            2 -> "#FFC107" // Yellow
            3 -> "#4CAF50" // Green
            4 -> "#2E7D32" // Dark Green
            else -> "#9E9E9E" // Gray
        }
    }

    fun clear() {
        password = null
        passwordError = null
        passwordStrength = 0
        isPasswordValid = false
    }
}