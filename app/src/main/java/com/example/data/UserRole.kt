package com.example.data

enum class UserRole(val displayName: String, val subtitle: String) {
    STUDENT("Sinh Viên", "Quét mã QR điểm danh & Xem kết quả"),
    TEACHER("Giảng Viên", "Tạo mã QR & Quản lý danh sách lớp")
}
