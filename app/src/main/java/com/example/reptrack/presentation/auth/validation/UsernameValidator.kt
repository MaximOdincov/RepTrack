package com.example.reptrack.presentation.auth.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

class UsernameValidator {
    var username by mutableStateOf<TextFieldValue?>(null)
    var usernameError by mutableStateOf<String?>(null)
    var isUsernameValid by mutableStateOf(false)
    var usernameLength by mutableStateOf(0)

    companion object {
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 20

        // Valid characters: letters, numbers, underscore
        private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]+$")

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

        // Reserved words that should not be used as usernames
        private val RESERVED_WORDS = setOf(
            "admin", "administrator", "root", "user", "users",
            "guest", "anonymous", "system", "test", "demo",
            "example", "sample", "default", "public", "private",
            "login", "signin", "signup", "register", "logout",
            "password", "email", "username", "name", "first",
            "last", "profile", "account", "settings", "home",
            "dashboard", "stats", "stats", "reports", "logs",
            "api", "auth", "oauth", "firebase", "google",
            "facebook", "twitter", "instagram", "github",
            "support", "help", "contact", "contact", "info",
            "about", "terms", "privacy", "policy", "legal",
            "blog", "news", "updates", "changelog", "version",
            "version", "build", "release", "beta", "alpha",
            "dev", "development", "debug", "error", "error",
            "success", "fail", "failed", "pass", "pass",
            "win", "win", "lose", "loss", "loss",
            "easy", "hard", "simple", "complex", "basic",
            "advanced", "expert", "beginner", "pro", "pro",
            "free", "paid", "premium", "trial", "demo",
            "download", "upload", "import", "export", "sync",
            "backup", "restore", "reset", "clear", "clean"
        )

        // Common profanity filter (basic - can be enhanced)
        private val PROFANITY_FILTER = setOf(
            "fuck", "shit", "crap", "damn", "hell", "bitch",
            "asshole", "dick", "pussy", "cunt", "nigger",
            "fag", "faggot", "whore", "slut", "bastard",
            "idiot", "moron", "retard", "stupid", "dumb",
            "hate", "kill", "death", "violence", "war",
            "rape", "abuse", "drug", "drugs", "alcohol",
            "beer", "wine", "vodka", "whiskey", "cocaine",
            "heroin", "marijuana", "weed", "porn", "sex",
            "xxx", "adult", "nude", "naked", "sexy",
            "horny", "hardcore", "lesbian", "gay", "homo",
            "terror", "terrorist", "bomb", "attack", "weapon"
        )
    }

    fun validate(): Boolean {
        val usernameText = username?.text ?: run {
            usernameError = "Username is required"
            return false
        }

        usernameLength = usernameText.length

        // Check empty
        if (usernameText.isBlank()) {
            usernameError = "Username is required"
            isUsernameValid = false
            return false
        }

        // Check length
        if (usernameText.length < MIN_LENGTH) {
            usernameError = "Username must be at least $MIN_LENGTH characters long"
            isUsernameValid = false
            return false
        }

        if (usernameText.length > MAX_LENGTH) {
            usernameError = "Username must be less than $MAX_LENGTH characters"
            isUsernameValid = false
            return false
        }

        // Check valid characters
        if (!USERNAME_REGEX.matches(usernameText)) {
            usernameError = "Username can only contain letters, numbers, and underscores"
            isUsernameValid = false
            return false
        }

        // Check if starts with number
        if (usernameText.first().isDigit()) {
            usernameError = "Username cannot start with a number"
            isUsernameValid = false
            return false
        }

        // Check if only underscore
        if (usernameText == "_") {
            usernameError = "Username cannot be just an underscore"
            isUsernameValid = false
            return false
        }

        // Check for SQL injection patterns
        for (pattern in SQL_INJECTION_PATTERNS) {
            if (usernameText.contains(pattern, ignoreCase = true)) {
                usernameError = "Username contains invalid characters"
                isUsernameValid = false
                return false
            }
        }

        // Check reserved words
        if (RESERVED_WORDS.contains(usernameText.lowercase())) {
            usernameError = "This username is not available"
            isUsernameValid = false
            return false
        }

        // Check profanity
        for (word in PROFANITY_FILTER) {
            if (usernameText.lowercase().contains(word)) {
                usernameError = "Username contains inappropriate content"
                isUsernameValid = false
                return false
            }
        }

        usernameError = null
        isUsernameValid = true
        return true
    }

    fun validateSilently(): Boolean {
        val usernameText = username?.text ?: return false
        return usernameText.length in MIN_LENGTH..MAX_LENGTH &&
               USERNAME_REGEX.matches(usernameText) &&
               !usernameText.first().isDigit() &&
               usernameText != "_"
    }

    fun getAvailabilityMessage(): String? {
        if (!isUsernameValid) return usernameError

        val usernameText = username?.text?.lowercase() ?: return null

        return when {
            RESERVED_WORDS.contains(usernameText) -> "This username is reserved"
            PROFANITY_FILTER.any { usernameText.contains(it) } -> "This username contains inappropriate content"
            usernameText.first().isDigit() -> "Username cannot start with a number"
            else -> null
        }
    }

    fun getFormattedUsername(): String? {
        return username?.text?.trim()?.takeIf { it.isNotEmpty() }
    }

    fun clear() {
        username = null
        usernameError = null
        isUsernameValid = false
        usernameLength = 0
    }

    fun isReservedWord(): Boolean {
        val usernameText = username?.text?.lowercase() ?: return false
        return RESERVED_WORDS.contains(usernameText)
    }

    fun containsProfanity(): Boolean {
        val usernameText = username?.text?.lowercase() ?: return false
        return PROFANITY_FILTER.any { usernameText.contains(it) }
    }
}