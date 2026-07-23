package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQrSessionDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onCreateSession: (subject: Subject, sessionTitle: String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()) }
    var sessionTitle by remember { mutableStateOf("Điểm Danh Buổi Học Trực Tiếp") }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    selectedSubject?.let { sub ->
                        onCreateSession(sub, sessionTitle.trim())
                        onDismiss()
                    }
                },
                enabled = selectedSubject != null,
                modifier = Modifier.testTag("submit_create_qr_session_button")
            ) {
                Text("Tạo Mã QR Ngay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Tạo Mã QR Điểm Danh Lớp",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Chọn môn học và nhập tên buổi học để tạo mã QR điểm danh cho sinh viên quét:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.let { "${it.code} - ${it.name}" } ?: "Chọn môn học",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Môn Học Điểm Danh") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text("${sub.code} - ${sub.name}") },
                                onClick = {
                                    selectedSubject = sub
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = sessionTitle,
                    onValueChange = { sessionTitle = it },
                    label = { Text("Tiêu Đề Buổi Học") },
                    leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
