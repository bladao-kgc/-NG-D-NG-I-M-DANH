package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceStatus
import com.example.data.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInDialog(
    subject: Subject,
    onDismiss: () -> Unit,
    onConfirm: (
        status: AttendanceStatus,
        location: String,
        note: String
    ) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(AttendanceStatus.PRESENT) }
    var location by remember { mutableStateOf("Phòng ${subject.room} - Wi-Fi KGC_Campus") }
    var note by remember { mutableStateOf("Xác thực điểm danh học viên thành công") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checkin_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Điểm Danh Học Phần",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${subject.code} - ${subject.name}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // QR Mock Animation Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Mã QR Giảng Viên Cung Cấp",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Đã quét thành công mã phòng ${subject.room}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "Chọn trạng thái tham dự:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Status Chips Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttendanceStatus.values().forEach { status ->
                        val isSelected = selectedStatus == status
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = status },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                Color(status.colorHex).copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            border = if (isSelected) {
                                ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color(status.colorHex))
                                )
                            } else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(status.colorHex), CircleShape)
                                    )
                                    Text(
                                        text = status.displayName,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(status.colorHex) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(status.colorHex),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Vị trí / Thiết bị xác thực") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú điểm danh") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedStatus, location, note)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(selectedStatus.colorHex)
                ),
                modifier = Modifier.testTag("submit_checkin_btn")
            ) {
                Text("Xác Nhận Điểm Danh")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
