package com.example.repository

import com.example.data.*
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val studentDao: StudentDao,
    private val subjectDao: SubjectDao,
    private val recordDao: AttendanceRecordDao
) {
    val studentProfile: Flow<StudentProfile?> = studentDao.getStudentProfile()
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    val allRecords: Flow<List<AttendanceRecord>> = recordDao.getAllRecords()

    suspend fun updateStudentProfile(profile: StudentProfile) {
        studentDao.insertOrUpdateProfile(profile)
    }

    suspend fun addSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(id: Long) {
        subjectDao.deleteSubjectById(id)
    }

    suspend fun recordAttendance(
        subjectId: Long,
        subjectName: String,
        subjectCode: String,
        status: AttendanceStatus,
        date: String,
        time: String,
        location: String,
        note: String
    ) {
        // Create Record
        val record = AttendanceRecord(
            subjectId = subjectId,
            subjectName = subjectName,
            subjectCode = subjectCode,
            date = date,
            time = time,
            status = status,
            location = location,
            note = note
        )
        recordDao.insertRecord(record)

        // Update Subject Attendance Stats
        val subject = subjectDao.getSubjectById(subjectId)
        if (subject != null) {
            val updated = when (status) {
                AttendanceStatus.PRESENT -> subject.copy(attendedSessions = subject.attendedSessions + 1)
                AttendanceStatus.LATE -> subject.copy(lateSessions = subject.lateSessions + 1)
                AttendanceStatus.ABSENT -> subject.copy(absentSessions = subject.absentSessions + 1)
                AttendanceStatus.EXCUSED -> subject.copy(absentSessions = subject.absentSessions + 1)
            }
            subjectDao.updateSubject(updated)
        }
    }

    suspend fun deleteRecord(id: Long) {
        recordDao.deleteRecordById(id)
    }
}
