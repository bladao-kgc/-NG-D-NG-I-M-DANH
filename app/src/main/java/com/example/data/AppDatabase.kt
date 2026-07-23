package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [StudentProfile::class, Subject::class, AttendanceRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diem_danh_cao_dang_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                // Initial Student Profile
                val studentDao = db.studentDao()
                studentDao.insertOrUpdateProfile(
                    StudentProfile(
                        id = 1,
                        fullName = "Nguyễn Văn An",
                        studentId = "CD21-TH0128",
                        className = "CĐ21-TH01",
                        major = "Công Nghệ Thông Tin",
                        academicYear = "2021 - 2024",
                        collegeName = "Trường Cao Đẳng Kỹ Thuật Công Nghệ",
                        systemType = "Cao Đẳng Chính Quy",
                        dateOfBirth = "15/08/2003",
                        phone = "0987 654 321",
                        email = "an.nguyen.cd21th01@caodang.edu.vn",
                        address = "Số 120 Đường Nguyễn Văn Cừ, Quận 5, TP. HCM",
                        status = "Đang Học"
                    )
                )

                // Initial College Subjects
                val subjectDao = db.subjectDao()
                val sub1Id = subjectDao.insertSubject(
                    Subject(
                        code = "MOB306",
                        name = "Lập Trình Lập Trình Di Động",
                        teacherName = "ThS. Trần Hoàng Nam",
                        room = "Phòng C201",
                        dayOfWeek = "Thứ 2 & Thứ 5",
                        timeSlot = "07:30 - 10:45",
                        totalSessions = 15,
                        attendedSessions = 8,
                        lateSessions = 1,
                        absentSessions = 1,
                        maxAllowedAbsences = 3,
                        creditHours = 3
                    )
                )

                val sub2Id = subjectDao.insertSubject(
                    Subject(
                        code = "DAT201",
                        name = "Cơ Sở Dữ Liệu SQL Server",
                        teacherName = "ThS. Nguyễn Thị Minh",
                        room = "Phòng Lab 04",
                        dayOfWeek = "Thứ 3 & Thứ 6",
                        timeSlot = "13:00 - 16:15",
                        totalSessions = 15,
                        attendedSessions = 10,
                        lateSessions = 0,
                        absentSessions = 0,
                        maxAllowedAbsences = 3,
                        creditHours = 3
                    )
                )

                val sub3Id = subjectDao.insertSubject(
                    Subject(
                        code = "NET102",
                        name = "Mạng Máy Tính & An Ninh Mạng",
                        teacherName = "Kỹ sư Lê Quốc Bảo",
                        room = "Phòng B102",
                        dayOfWeek = "Thứ 4",
                        timeSlot = "07:30 - 11:30",
                        totalSessions = 12,
                        attendedSessions = 7,
                        lateSessions = 2,
                        absentSessions = 1,
                        maxAllowedAbsences = 3,
                        creditHours = 2
                    )
                )

                val sub4Id = subjectDao.insertSubject(
                    Subject(
                        code = "ENG202",
                        name = "Tiếng Anh Chuyên Ngành CNTT",
                        teacherName = "Cô Phạm Khánh Linh",
                        room = "Phòng A305",
                        dayOfWeek = "Thứ 7",
                        timeSlot = "08:00 - 11:15",
                        totalSessions = 10,
                        attendedSessions = 6,
                        lateSessions = 0,
                        absentSessions = 0,
                        maxAllowedAbsences = 2,
                        creditHours = 2
                    )
                )

                // Initial Attendance Records
                val recordDao = db.attendanceRecordDao()
                recordDao.insertRecord(
                    AttendanceRecord(
                        subjectId = sub1Id,
                        subjectName = "Lập Trình Lập Trình Di Động",
                        subjectCode = "MOB306",
                        date = "21/07/2026",
                        time = "07:35 AM",
                        status = AttendanceStatus.PRESENT,
                        location = "Phòng C201 - Wi-Fi KGC_Campus",
                        note = "Điểm danh tự động qua mã QR"
                    )
                )
                recordDao.insertRecord(
                    AttendanceRecord(
                        subjectId = sub2Id,
                        subjectName = "Cơ Sở Dữ Liệu SQL Server",
                        subjectCode = "DAT201",
                        date = "22/07/2026",
                        time = "13:05 PM",
                        status = AttendanceStatus.PRESENT,
                        location = "Phòng Lab 04 - Wi-Fi KGC_Campus",
                        note = "Đúng giờ - Có làm bài tập lab"
                    )
                )
                recordDao.insertRecord(
                    AttendanceRecord(
                        subjectId = sub3Id,
                        subjectName = "Mạng Máy Tính & An Ninh Mạng",
                        subjectCode = "NET102",
                        date = "18/07/2026",
                        time = "07:48 AM",
                        status = AttendanceStatus.LATE,
                        location = "Phòng B102",
                        note = "Đến trễ 18 phút do kẹt xe"
                    )
                )
            }
        }
    }
}
