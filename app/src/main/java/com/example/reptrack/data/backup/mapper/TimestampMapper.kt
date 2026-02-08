package com.example.reptrack.data.backup.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object TimestampMapper {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toTimestamp(ldt: LocalDateTime): Long {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
