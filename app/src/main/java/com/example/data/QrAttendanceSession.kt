package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_sessions")
data class QrAttendanceSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val subjectCode: String,
    val subjectName: String,
    val sessionTitle: String,
    val room: String,
    val date: String,
    val createdTime: String,
    val qrToken: String,
    val isActive: Boolean = true
)
