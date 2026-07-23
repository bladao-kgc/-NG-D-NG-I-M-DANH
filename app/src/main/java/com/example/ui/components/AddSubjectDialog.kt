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

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onSave: (
        code: String,
        name: String,
        teacherName: String,
        room: String,
        dayOfWeek: String,
        timeSlot: String,
        totalSessions: Int,
        maxAllowedAbsences: Int,
        creditHours: Int
    ) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("Phòng C201") }
    var dayOfWeek by remember { mutableStateOf("Thứ 2 & Thứ 5") }
    var timeSlot by remember { mutableStateOf("07:30 - 10:45") }
    var totalSessionsText by remember { mutableStateOf("15") }
    var maxAllowedAbsencesText by remember { mutableStateOf("3") }
    var creditHoursText by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_subject_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Thêm Môn Học Mới",
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
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Mã Môn Học (vd: MOB306)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_code"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên Tên Môn Học") },
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("Giảng Viên Giảng Dạy") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Phòng Học") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dayOfWeek,
                        onValueChange = { dayOfWeek = it },
                        label = { Text("Lịch Học") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text("Khung Giờ Học") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalSessionsText,
                        onValueChange = { totalSessionsText = it },
                        label = { Text("Tổng Số Buổi") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maxAllowedAbsencesText,
                        onValueChange = { maxAllowedAbsencesText = it },
                        label = { Text("Số Buổi Vắng Tối Đa") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = creditHoursText,
                    onValueChange = { creditHoursText = it },
                    label = { Text("Số Tín Chỉ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onSave(
                            code,
                            name,
                            teacherName,
                            room,
                            dayOfWeek,
                            timeSlot,
                            totalSessionsText.toIntOrNull() ?: 15,
                            maxAllowedAbsencesText.toIntOrNull() ?: 3,
                            creditHoursText.toIntOrNull() ?: 3
                        )
                        onDismiss()
                    }
                },
                enabled = code.isNotBlank() && name.isNotBlank(),
                modifier = Modifier.testTag("save_subject_btn")
            ) {
                Text("Lưu Môn Học")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
