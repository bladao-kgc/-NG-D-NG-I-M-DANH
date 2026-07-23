package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val teacherName: String,
    val room: String,
    val dayOfWeek: String,
    val timeSlot: String,
    val totalSessions: Int = 15,
    val attendedSessions: Int = 0,
    val lateSessions: Int = 0,
    val absentSessions: Int = 0,
    val maxAllowedAbsences: Int = 3,
    val creditHours: Int = 3
)
