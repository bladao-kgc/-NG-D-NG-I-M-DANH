package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.*
import com.example.ui.components.AddClassStudentDialog
import com.example.ui.components.CreateQrSessionDialog
import com.example.ui.components.QrCodeCanvas

@Composable
fun TeacherScreen(
    classStudents: List<ClassStudent>,
    subjects: List<Subject>,
    records: List<AttendanceRecord>,
    activeSession: QrAttendanceSession?,
    onAddStudent: (studentId: String, fullName: String, className: String, phone: String, email: String) -> Unit,
    onDeleteStudent: (id: Long) -> Unit,
    onCreateQrSession: (subject: Subject, title: String) -> Unit,
    onCloseQrSession: (id: Long) -> Unit,
    onManualCheckIn: (subject: Subject, studentId: String, studentName: String, status: AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Class Roster, 1: Attendance Sheet
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showCreateQrDialog by remember { mutableStateOf(false) }
    var showQrFullScreen by remember { mutableStateOf(false) }

    var selectedSubjectFilter by remember { mutableStateOf(subjects.firstOrNull()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Teacher Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bảng Điều Khiển Giảng Viên",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Quản lý lớp CĐ21-TH01 & Tạo mã QR điểm danh",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(22.dp)
                            )
                        }
                    }

                    // Quick Active QR Banner if session is live
                    if (activeSession != null) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showQrFullScreen = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "MÃ QR ĐANG MỞ: ${activeSession.subjectCode}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )
                                        Text(
                                            text = "${activeSession.sessionTitle} • Chạm để phóng to",
                                            fontSize = 11.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }

                                TextButton(onClick = { onCloseQrSession(activeSession.id) }) {
                                    Text("Đóng Mã QR", color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showCreateQrDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("teacher_create_qr_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tạo Mã QR", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { showAddStudentDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("teacher_add_student_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Thêm SV", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Tab Selector (0: Danh sách Lớp, 1: Điểm danh Chi tiết)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Danh Sách Lớp (${classStudents.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Bảng Điểm Danh Buổi",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content 0: Class Roster
        if (selectedTab == 0) {
            items(classStudents) { student ->
                ClassStudentCard(
                    student = student,
                    onDelete = { onDeleteStudent(student.id) }
                )
            }
        } else {
            // Tab Content 1: Live Attendance Sheet per Student
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Chọn môn học để chấm điểm danh:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subjects.forEach { sub ->
                            FilterChip(
                                selected = selectedSubjectFilter?.id == sub.id,
                                onClick = { selectedSubjectFilter = sub },
                                label = { Text(sub.code) }
                            )
                        }
                    }
                }
            }

            selectedSubjectFilter?.let { currentSub ->
                items(classStudents) { student ->
                    // Find latest record for this student & subject
                    val record = records.firstOrNull { it.studentId == student.studentId && it.subjectId == currentSub.id }

                    AttendanceStatusCard(
                        student = student,
                        subject = currentSub,
                        currentRecord = record,
                        onStatusChange = { newStatus ->
                            onManualCheckIn(currentSub, student.studentId, student.fullName, newStatus)
                        }
                    )
                }
            }
        }
    }

    // Dialog 1: Add Student
    if (showAddStudentDialog) {
        AddClassStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onSave = onAddStudent
        )
    }

    // Dialog 2: Create QR Session
    if (showCreateQrDialog) {
        CreateQrSessionDialog(
            subjects = subjects,
            onDismiss = { showCreateQrDialog = false },
            onCreateSession = { subject, title ->
                onCreateQrSession(subject, title)
                showQrFullScreen = true
            }
        )
    }

    // Dialog 3: Display Full Screen Active QR Code
    if (showQrFullScreen && activeSession != null) {
        AlertDialog(
            onDismissRequest = { showQrFullScreen = false },
            confirmButton = {
                Button(onClick = { showQrFullScreen = false }) {
                    Text("Hoàn Tất / Thu Nhỏ")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onCloseQrSession(activeSession.id)
                    showQrFullScreen = false
                }) {
                    Text("Kết Thúc Điểm Danh", color = MaterialTheme.colorScheme.error)
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Mã QR Điểm Danh Lớp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${activeSession.subjectCode} - ${activeSession.subjectName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QrCodeCanvas(
                        content = activeSession.qrToken,
                        size = 220.dp
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Mã Session: ${activeSession.qrToken}\nPhòng: ${activeSession.room} • Ngày: ${activeSession.date}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(
                        text = "Yêu cầu sinh viên mở ứng dụng và quét mã này để hoàn tất điểm danh tự động.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ClassStudentCard(
    student: ClassStudent,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("class_student_card_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = student.fullName.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column {
                    Text(
                        text = student.fullName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "MSSV: ${student.studentId} • Lớp: ${student.className}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (student.phone.isNotBlank()) {
                        Text(
                            text = "SĐT: ${student.phone}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Xóa khỏi lớp",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatusCard(
    student: ClassStudent,
    subject: Subject,
    currentRecord: AttendanceRecord?,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    val currentStatus = currentRecord?.status ?: AttendanceStatus.ABSENT

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_attendance_card_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = student.fullName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MSSV: ${student.studentId}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = Color(currentStatus.colorHex).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = currentStatus.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(currentStatus.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick Toggle Status Buttons for Teacher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttendanceStatus.values().forEach { status ->
                    val isSelected = currentStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusChange(status) },
                        label = { Text(status.displayName, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(status.colorHex),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
