package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.repository.AttendanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    val studentProfile: StateFlow<StudentProfile?>
    val subjects: StateFlow<List<Subject>>
    val records: StateFlow<List<AttendanceRecord>>
    val classStudents: StateFlow<List<ClassStudent>>
    val qrSessions: StateFlow<List<QrAttendanceSession>>
    val activeSession: StateFlow<QrAttendanceSession?>

    private val _userRole = MutableStateFlow(UserRole.STUDENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // Filter for History screen
    private val _selectedFilterStatus = MutableStateFlow<AttendanceStatus?>(null)
    val selectedFilterStatus: StateFlow<AttendanceStatus?> = _selectedFilterStatus.asStateFlow()

    private val _selectedFilterSubjectId = MutableStateFlow<Long?>(null)
    val selectedFilterSubjectId: StateFlow<Long?> = _selectedFilterSubjectId.asStateFlow()

    val filteredRecords: StateFlow<List<AttendanceRecord>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AttendanceRepository(
            database.studentDao(),
            database.subjectDao(),
            database.attendanceRecordDao(),
            database.classStudentDao(),
            database.qrSessionDao()
        )

        studentProfile = repository.studentProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        subjects = repository.allSubjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        records = repository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        classStudents = repository.allClassStudents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        qrSessions = repository.allSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeSession = repository.activeSession.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        filteredRecords = combine(records, _selectedFilterStatus, _selectedFilterSubjectId) { recList, status, subId ->
            recList.filter { record ->
                (status == null || record.status == status) &&
                        (subId == null || record.subjectId == subId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun setFilterStatus(status: AttendanceStatus?) {
        _selectedFilterStatus.value = status
    }

    fun setFilterSubject(subjectId: Long?) {
        _selectedFilterSubjectId.value = subjectId
    }

    fun updateProfile(
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
    ) {
        viewModelScope.launch {
            val current = studentProfile.value ?: StudentProfile()
            val updated = current.copy(
                fullName = fullName,
                studentId = studentId,
                className = className,
                major = major,
                academicYear = academicYear,
                collegeName = collegeName,
                systemType = systemType,
                dateOfBirth = dateOfBirth,
                phone = phone,
                email = email,
                address = address,
                status = status
            )
            repository.updateStudentProfile(updated)
        }
    }

    fun performCheckIn(
        subject: Subject,
        status: AttendanceStatus,
        location: String,
        note: String,
        studentId: String = studentProfile.value?.studentId ?: "CD21-TH0128",
        studentName: String = studentProfile.value?.fullName ?: "Nguyễn Văn An"
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
            val now = Date()

            repository.recordAttendance(
                studentId = studentId,
                studentName = studentName,
                subjectId = subject.id,
                subjectName = subject.name,
                subjectCode = subject.code,
                status = status,
                date = dateFormat.format(now),
                time = timeFormat.format(now),
                location = location.ifBlank { "Phòng ${subject.room} - Wi-Fi KGC_Campus" },
                note = note.ifBlank { "Đã điểm danh sinh viên qua mã QR" }
            )
        }
    }

    fun addNewClassStudent(
        studentId: String,
        fullName: String,
        className: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            repository.addClassStudent(
                ClassStudent(
                    studentId = studentId,
                    fullName = fullName,
                    className = className.ifBlank { "CĐ21-TH01" },
                    phone = phone,
                    email = email
                )
            )
        }
    }

    fun deleteClassStudent(id: Long) {
        viewModelScope.launch {
            repository.deleteClassStudent(id)
        }
    }

    fun createQrSession(
        subject: Subject,
        sessionTitle: String
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
            val now = Date()
            val qrToken = "KGC_${subject.code}_${System.currentTimeMillis()}"

            repository.createQrSession(
                QrAttendanceSession(
                    subjectId = subject.id,
                    subjectCode = subject.code,
                    subjectName = subject.name,
                    sessionTitle = sessionTitle.ifBlank { "Điểm Danh Buổi Học - ${subject.name}" },
                    room = subject.room,
                    date = dateFormat.format(now),
                    createdTime = timeFormat.format(now),
                    qrToken = qrToken,
                    isActive = true
                )
            )
        }
    }

    fun closeQrSession(id: Long) {
        viewModelScope.launch {
            repository.closeQrSession(id)
        }
    }

    fun addNewSubject(
        code: String,
        name: String,
        teacherName: String,
        room: String,
        dayOfWeek: String,
        timeSlot: String,
        totalSessions: Int,
        maxAllowedAbsences: Int,
        creditHours: Int
    ) {
        viewModelScope.launch {
            val newSub = Subject(
                code = code,
                name = name,
                teacherName = teacherName,
                room = room,
                dayOfWeek = dayOfWeek,
                timeSlot = timeSlot,
                totalSessions = totalSessions,
                maxAllowedAbsences = maxAllowedAbsences,
                creditHours = creditHours
            )
            repository.addSubject(newSub)
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            repository.deleteSubject(id)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }
}
