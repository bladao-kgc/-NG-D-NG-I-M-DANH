package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AttendanceStatus(val displayName: String, val colorHex: Long) {
    PRESENT("Có Mặt", 0xFF2E7D32),
    LATE("Đi Trễ", 0xFFF57C00),
    ABSENT("Vắng Mặt", 0xFFC62828),
    EXCUSED("Có Phép", 0xFF0288D1)
}

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String = "CD21-TH0128",
    val studentName: String = "Nguyễn Văn An",
    val subjectId: Long,
    val subjectName: String,
    val subjectCode: String,
    val date: String,
    val time: String,
    val status: AttendanceStatus,
    val location: String = "Phòng C201 - Wi-Fi KGC_Campus",
    val note: String = ""
)
