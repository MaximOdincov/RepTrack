package com.example.reptrack.data.auth

import java.security.SecureRandom
import java.util.Base64

object PasskeyService {
    private const val PASSKEY_LENGTH = 8
    private val secureRandom = SecureRandom()

    fun generatePasskey(): String {
        val bytes = ByteArray(PASSKEY_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().withoutPadding().encodeToString(bytes).substring(0, PASSKEY_LENGTH)
    }
}
