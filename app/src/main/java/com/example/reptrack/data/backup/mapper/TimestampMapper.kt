package com.example.reptrack.data.backup.mapper

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object TimestampMapper {

    fun toTimestamp(ldt: LocalDateTime): Long {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun fromTimestamp(timestamp: Long?): LocalDateTime {
        return if (timestamp != null && timestamp > 0) {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
            )
        } else {
            LocalDateTime.now()
        }
    }
}
