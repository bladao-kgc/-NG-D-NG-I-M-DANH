package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_students")
data class ClassStudent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val fullName: String,
    val className: String = "CĐ21-TH01",
    val phone: String = "0987 654 321",
    val email: String = "sinhvien@caodang.edu.vn",
    val status: String = "Đang Học"
)
