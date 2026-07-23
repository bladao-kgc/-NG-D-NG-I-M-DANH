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
