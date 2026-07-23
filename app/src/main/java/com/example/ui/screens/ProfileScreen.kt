package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudentProfile
import com.example.data.Subject
import com.example.data.UserRole
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.StudentCard

@Composable
fun ProfileScreen(
    profile: StudentProfile?,
    subjects: List<Subject>,
    userRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    onUpdateProfile: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val totalAttended = subjects.sumOf { it.attendedSessions }
    val totalSessions = subjects.sumOf { it.totalSessions }.coerceAtLeast(1)
    val overallPercentage = (totalAttended * 100f / totalSessions).toInt()

    val totalAbsences = subjects.sumOf { it.absentSessions }
    val warningSubjectsCount = subjects.count { it.absentSessions >= it.maxAllowedAbsences }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Role Switcher Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Chế Độ Sử Dụng Ứng Dụng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = userRole == role
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onRoleChange(role) }
                                    .testTag("role_button_${role.name}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = role.displayName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = role.subtitle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ID Card
        item {
            profile?.let {
                StudentCard(
                    profile = it,
                    onEditClick = { showEditDialog = true }
                )
            } ?: CircularProgressIndicator()
        }

        // Attendance Overview Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thống Kê Chuyên Cần Tổng Quan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Surface(
                            shape = CircleShape,
                            color = if (overallPercentage >= 80) Color(0xFF2E7D32) else Color(0xFFC62828)
                        ) {
                            Text(
                                text = "$overallPercentage%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { overallPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color.White, CircleShape),
                        color = if (overallPercentage >= 80) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(
                            title = "Tổng Môn Học",
                            value = "${subjects.size} môn",
                            color = MaterialTheme.colorScheme.primary
                        )
                        MetricItem(
                            title = "Đã Tham Gia",
                            value = "$totalAttended buổi",
                            color = Color(0xFF2E7D32)
                        )
                        MetricItem(
                            title = "Tổng Số Vắng",
                            value = "$totalAbsences buổi",
                            color = if (totalAbsences > 0) Color(0xFFD32F2F) else Color.Gray
                        )
                    }

                    if (warningSubjectsCount > 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828)
                                )
                                Text(
                                    text = "Có $warningSubjectsCount môn học vượt quá ngưỡng vắng cho phép! Cần chú ý chuyên cần.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // College Regulations & Notes Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Quy Định Chuyên Cần Hệ Cao Đẳng",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    RegulationBullet(text = "Sinh viên phải có mặt tại lớp trước 05 phút giờ học chính thức.")
                    RegulationBullet(text = "Tỷ lệ tham dự đạt từ 80% trở lên mới đủ điều kiện thi kết thúc học phần.")
                    RegulationBullet(text = "Sinh viên vắng quá 20% số tiết quy định sẽ bị ngưng học phần (Cấm thi).")
                    RegulationBullet(text = "Nghỉ có phép phải nộp đơn kèm xác nhận y tế/gia đình cho giảng viên.")
                }
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            profile = profile,
            onDismiss = { showEditDialog = false },
            onSave = onUpdateProfile
        )
    }
}

@Composable
private fun MetricItem(title: String, value: String, color: Color) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun RegulationBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}
