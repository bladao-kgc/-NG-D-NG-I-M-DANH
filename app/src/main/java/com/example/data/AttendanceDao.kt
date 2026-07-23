package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getStudentProfile(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    suspend fun getStudentProfileDirect(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: StudentProfile)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Long): Subject?
}

@Dao
interface AttendanceRecordDao {
    @Query("SELECT * FROM attendance_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE subjectId = :subjectId ORDER BY id DESC")
    fun getRecordsForSubject(subjectId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord): Long

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT COUNT(*) FROM attendance_records WHERE status = :status")
    fun countByStatus(status: AttendanceStatus): Flow<Int>
}

@Dao
interface ClassStudentDao {
    @Query("SELECT * FROM class_students ORDER BY fullName ASC")
    fun getAllClassStudents(): Flow<List<ClassStudent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: ClassStudent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<ClassStudent>)

    @Query("DELETE FROM class_students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("SELECT * FROM class_students WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentByStudentId(studentId: String): ClassStudent?
}

@Dao
interface QrSessionDao {
    @Query("SELECT * FROM qr_sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<QrAttendanceSession>>

    @Query("SELECT * FROM qr_sessions WHERE isActive = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveSession(): Flow<QrAttendanceSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: QrAttendanceSession): Long

    @Query("UPDATE qr_sessions SET isActive = 0 WHERE id = :id")
    suspend fun closeSession(id: Long)
}
