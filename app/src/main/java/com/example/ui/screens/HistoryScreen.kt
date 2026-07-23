package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceRecord
import com.example.data.AttendanceStatus
import com.example.data.Subject

@Composable
fun HistoryScreen(
    records: List<AttendanceRecord>,
    subjects: List<Subject>,
    selectedStatus: AttendanceStatus?,
    selectedSubjectId: Long?,
    onSelectStatus: (AttendanceStatus?) -> Unit,
    onSelectSubject: (Long?) -> Unit,
    onDeleteRecord: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Lịch Sử Điểm Danh Sinh Viên",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Filter by Status Chips
                Text(
                    text = "Lọc theo trạng thái:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedStatus == null,
                            onClick = { onSelectStatus(null) },
                            label = { Text("Tất Cả (${records.size})") }
                        )
                    }
                    items(AttendanceStatus.values()) { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { onSelectStatus(if (selectedStatus == status) null else status) },
                            label = { Text(status.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(status.colorHex).copy(alpha = 0.2f),
                                selectedLabelColor = Color(status.colorHex)
                            )
                        )
                    }
                }

                // Filter by Subject Chips
                if (subjects.isNotEmpty()) {
                    Text(
                        text = "Lọc theo môn học:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubjectId == null,
                                onClick = { onSelectSubject(null) },
                                label = { Text("Tất Cả Môn") }
                            )
                        }
                        items(subjects) { subject ->
                            FilterChip(
                                selected = selectedSubjectId == subject.id,
                                onClick = { onSelectSubject(if (selectedSubjectId == subject.id) null else subject.id) },
                                label = { Text(subject.code) }
                            )
                        }
                    }
                }
            }
        }

        if (records.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Chưa có nhật ký điểm danh tương ứng",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        } else {
            items(records) { record ->
                HistoryRecordCard(
                    record = record,
                    onDelete = { onDeleteRecord(record.id) }
                )
            }
        }
    }
}

@Composable
private fun HistoryRecordCard(
    record: AttendanceRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_record_card_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = record.subjectCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = record.subjectName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa nhật ký",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.date} lúc ${record.time}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = Color(record.status.colorHex).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = record.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(record.status.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = record.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (record.note.isNotBlank()) {
                Text(
                    text = "Ghi chú: ${record.note}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
