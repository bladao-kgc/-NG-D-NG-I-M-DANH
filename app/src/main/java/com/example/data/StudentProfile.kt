package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Nguyễn Văn An",
    val studentId: String = "CD21-TH0128",
    val className: String = "CĐ21-TH01",
    val major: String = "Công Nghệ Thông Tin",
    val academicYear: String = "2021 - 2024 (Khóa 21)",
    val collegeName: String = "Trường Cao Đẳng Kỹ Thuật & Công Nghệ",
    val systemType: String = "Hệ Cao Đẳng Chính Quy",
    val dateOfBirth: String = "15/08/2003",
    val phone: String = "0987 654 321",
    val email: String = "an.nguyen.cd21th01@caodang.edu.vn",
    val address: String = "Số 120 Đường Nguyễn Văn Cừ, Quận 5, TP. HCM",
    val status: String = "Đang Học",
    val avatarPath: String = ""
)
