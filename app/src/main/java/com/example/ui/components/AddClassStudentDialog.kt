package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassStudentDialog(
    onDismiss: () -> Unit,
    onSave: (
        studentId: String,
        fullName: String,
        className: String,
        phone: String,
        email: String
    ) -> Unit
) {
    var studentId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("CĐ21-TH01") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (studentId.isBlank() || fullName.isBlank()) {
                        isError = true
                    } else {
                        onSave(
                            studentId.trim(),
                            fullName.trim(),
                            className.trim(),
                            phone.trim(),
                            email.trim()
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("submit_add_student_button")
            ) {
                Text("Lưu Sinh Viên")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        title = {
            Text(
                text = "Thêm Sinh Viên Vào Lớp",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = studentId,
                    onValueChange = {
                        studentId = it
                        isError = false
                    },
                    label = { Text("Mã Số Sinh Viên (MSSV) *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    isError = isError && studentId.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("student_id_input")
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        isError = false
                    },
                    label = { Text("Họ và Tên Sinh Viên *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    isError = isError && fullName.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                )

                OutlinedTextField(
                    value = className,
                    onValueChange = { className = it },
                    label = { Text("Lớp Học Phần / Khóa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số Điện Thoại Liên Hệ") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Sinh Viên") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Vui lòng nhập MSSV và Họ Tên sinh viên",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
