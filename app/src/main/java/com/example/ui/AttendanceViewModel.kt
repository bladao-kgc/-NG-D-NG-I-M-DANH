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
            database.attendanceRecordDao()
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
        note: String
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
            val now = Date()

            repository.recordAttendance(
                subjectId = subject.id,
                subjectName = subject.name,
                subjectCode = subject.code,
                status = status,
                date = dateFormat.format(now),
                time = timeFormat.format(now),
                location = location.ifBlank { "Phòng ${subject.room} - Wi-Fi KGC_Campus" },
                note = note.ifBlank { "Đã điểm danh sinh viên thành công" }
            )
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
