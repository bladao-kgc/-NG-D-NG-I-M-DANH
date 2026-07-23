package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Subject
import com.example.ui.components.AddSubjectDialog

@Composable
fun SubjectsScreen(
    subjects: List<Subject>,
    onAddSubject: (
        code: String,
        name: String,
        teacherName: String,
        room: String,
        dayOfWeek: String,
        timeSlot: String,
        totalSessions: Int,
        maxAllowedAbsences: Int,
        creditHours: Int
    ) -> Unit,
    onDeleteSubject: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("subjects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Danh Sách Môn Học Cao Đẳng",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Theo dõi số buổi chuyên cần & giới hạn vắng",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_subject_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm môn học",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm Môn", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(subjects) { subject ->
            SubjectDetailCard(
                subject = subject,
                onDelete = { onDeleteSubject(subject.id) }
            )
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onSave = onAddSubject
        )
    }
}

@Composable
private fun SubjectDetailCard(
    subject: Subject,
    onDelete: () -> Unit
) {
    val total = subject.totalSessions.coerceAtLeast(1)
    val percentage = ((subject.attendedSessions * 100f) / total).toInt()
    val isWarning = subject.absentSessions >= subject.maxAllowedAbsences

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) Color(0xFFFFF8F8) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = subject.code,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${subject.creditHours} Tín chỉ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subject.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Xóa môn học",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Giảng viên: ${subject.teacherName} • ${subject.room}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tiến độ tham dự: ${subject.attendedSessions}/${subject.totalSessions} buổi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage >= 80) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                }

                LinearProgressIndicator(
                    progress = { subject.attendedSessions / total.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
                    color = if (percentage >= 80) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
            }

            // Stats breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBadge(title = "Có Mặt", count = subject.attendedSessions, color = Color(0xFF2E7D32))
                StatBadge(title = "Đi Trễ", count = subject.lateSessions, color = Color(0xFFF57C00))
                StatBadge(title = "Vắng Mặt", count = subject.absentSessions, color = Color(0xFFC62828))
                StatBadge(title = "Tối Đa Vắng", count = subject.maxAllowedAbsences, color = Color.Gray)
            }

            if (isWarning) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "CẢNH BÁO: Đã chạm ngưỡng ${subject.absentSessions}/${subject.maxAllowedAbsences} buổi vắng! Nguy cơ cấm thi học phần này.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(title: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
