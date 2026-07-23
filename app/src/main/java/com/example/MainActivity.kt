package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AttendanceViewModel
import com.example.ui.screens.CheckInScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SubjectsScreen
import com.example.ui.theme.MyApplicationTheme

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    PROFILE(
        title = "Cá Nhân",
        selectedIcon = Icons.Filled.Badge,
        unselectedIcon = Icons.Outlined.Badge,
        testTag = "tab_profile"
    ),
    CHECKIN(
        title = "Điểm Danh",
        selectedIcon = Icons.Filled.HowToReg,
        unselectedIcon = Icons.Outlined.HowToReg,
        testTag = "tab_checkin"
    ),
    SUBJECTS(
        title = "Môn Học",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
        testTag = "tab_subjects"
    ),
    HISTORY(
        title = "Lịch Sử",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
        testTag = "tab_history"
    )
}

class MainActivity : ComponentActivity() {

    private val viewModel: AttendanceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentTab by remember { mutableStateOf(NavigationTab.PROFILE) }

                val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
                val subjects by viewModel.subjects.collectAsStateWithLifecycle()
                val records by viewModel.records.collectAsStateWithLifecycle()
                val filteredRecords by viewModel.filteredRecords.collectAsStateWithLifecycle()
                val selectedStatus by viewModel.selectedFilterStatus.collectAsStateWithLifecycle()
                val selectedSubjectId by viewModel.selectedFilterSubjectId.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "Điểm Danh Sinh Viên Cao Đẳng",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = studentProfile?.collegeName ?: "Hệ Đào Tạo Cao Đẳng",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            actions = {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Text(
                                        text = studentProfile?.className ?: "CĐ-K21",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            windowInsets = WindowInsets.navigationBars,
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationTab.values().forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_animation"
                    ) { targetTab ->
                        val paddingModifier = Modifier.padding(innerPadding)
                        when (targetTab) {
                            NavigationTab.PROFILE -> ProfileScreen(
                                profile = studentProfile,
                                subjects = subjects,
                                onUpdateProfile = viewModel::updateProfile,
                                modifier = paddingModifier
                            )
                            NavigationTab.CHECKIN -> CheckInScreen(
                                profile = studentProfile,
                                subjects = subjects,
                                recentRecords = records,
                                onCheckInSubmit = viewModel::performCheckIn,
                                modifier = paddingModifier
                            )
                            NavigationTab.SUBJECTS -> SubjectsScreen(
                                subjects = subjects,
                                onAddSubject = viewModel::addNewSubject,
                                onDeleteSubject = viewModel::deleteSubject,
                                modifier = paddingModifier
                            )
                            NavigationTab.HISTORY -> HistoryScreen(
                                records = filteredRecords,
                                subjects = subjects,
                                selectedStatus = selectedStatus,
                                selectedSubjectId = selectedSubjectId,
                                onSelectStatus = viewModel::setFilterStatus,
                                onSelectSubject = viewModel::setFilterSubject,
                                onDeleteRecord = viewModel::deleteRecord,
                                modifier = paddingModifier
                            )
                        }
                    }
                }
            }
        }
    }
}
