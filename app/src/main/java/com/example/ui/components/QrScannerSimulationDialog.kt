package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceStatus
import com.example.data.QrAttendanceSession
import com.example.data.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerSimulationDialog(
    activeSession: QrAttendanceSession?,
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onSuccess: (
        subject: Subject,
        status: AttendanceStatus,
        location: String,
        note: String
    ) -> Unit
) {
    var scannedSuccessfully by remember { mutableStateOf(false) }

    // Pulsing laser animation for QR viewfinder
    val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
    val laserYOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    val targetSubject = subjects.firstOrNull { it.code == activeSession?.subjectCode } ?: subjects.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (scannedSuccessfully) {
                Button(
                    onClick = {
                        targetSubject?.let { sub ->
                            onSuccess(
                                sub,
                                AttendanceStatus.PRESENT,
                                "Phòng ${sub.room} - Quét Mã QR Trực Tiếp",
                                "Điểm danh thành công qua quét mã QR buổi học"
                            )
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.testTag("confirm_qr_scan_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Xác Nhận Điểm Danh")
                }
            } else {
                Button(
                    onClick = { scannedSuccessfully = true },
                    modifier = Modifier.testTag("simulate_qr_scan_button")
                ) {
                    Text("Giả Lập Khớp Mã QR")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng Camera")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = if (scannedSuccessfully) "Đã Khớp Mã QR Điểm Danh!" else "Ống Kính Quét Mã QR Lớp",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!scannedSuccessfully) {
                    // Animated Viewfinder Box
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner Laser Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = laserYOffset.dp)
                                .background(Color(0xFF00E676))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Đang quét mã QR...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (activeSession != null) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Đã phát hiện mã điểm danh: ${activeSession.subjectCode} - ${activeSession.sessionTitle}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Hướng ống kính camera vào mã QR đang hiển thị trên màn hình Giảng viên.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Success View
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(48.dp)
                            )

                            Text(
                                text = "XÁC NHẬN MÃ QR THÀNH CÔNG",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )

                            targetSubject?.let { sub ->
                                Text(
                                    text = "Môn: ${sub.code} - ${sub.name}\nPhòng: ${sub.room} • Trạng thái: CÓ MẶT",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
