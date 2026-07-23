package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudentProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    profile: StudentProfile?,
    onDismiss: () -> Unit,
    onSave: (
        fullName: String,
        studentId: String,
        className: String,
        major: String,
        academicYear: String,
        collegeName: String,
        systemType: String,
        dateOfBirth: String,
        phone: String,
        email: String,
        address: String,
        status: String
    ) -> Unit
) {
    val current = profile ?: StudentProfile()

    var fullName by remember { mutableStateOf(current.fullName) }
    var studentId by remember { mutableStateOf(current.studentId) }
    var className by remember { mutableStateOf(current.className) }
    var major by remember { mutableStateOf(current.major) }
    var academicYear by remember { mutableStateOf(current.academicYear) }
    var collegeName by remember { mutableStateOf(current.collegeName) }
    var systemType by remember { mutableStateOf(current.systemType) }
    var dateOfBirth by remember { mutableStateOf(current.dateOfBirth) }
    var phone by remember { mutableStateOf(current.phone) }
    var email by remember { mutableStateOf(current.email) }
    var address by remember { mutableStateOf(current.address) }
    var status by remember { mutableStateOf(current.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_profile_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Cập Nhật Thông Tin Cá Nhân",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và Tên") },
                    modifier = Modifier.fillMaxWidth().testTag("input_full_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Mã Số Sinh Viên (MSSV)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_student_id"),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Lớp") },
                        modifier = Modifier.weight(1f).testTag("input_class_name"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = major,
                        onValueChange = { major = it },
                        label = { Text("Ngành / Khoa") },
                        modifier = Modifier.weight(1f).testTag("input_major"),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = systemType,
                    onValueChange = { systemType = it },
                    label = { Text("Hệ Đào Tạo (vd: Cao Đẳng Chính Quy)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = collegeName,
                    onValueChange = { collegeName = it },
                    label = { Text("Trường Cao Đẳng") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it },
                    label = { Text("Khóa / Niên Khóa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Ngày Sinh (dd/MM/yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số Điện Thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Sinh Viên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa Chỉ Nơi Ở") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Trạng Thái Đào Tạo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        fullName, studentId, className, major, academicYear,
                        collegeName, systemType, dateOfBirth, phone, email,
                        address, status
                    )
                    onDismiss()
                },
                modifier = Modifier.testTag("save_profile_btn")
            ) {
                Text("Lưu Thay Đổi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
