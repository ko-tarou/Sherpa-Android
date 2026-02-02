package com.example.application

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class TaskData(
    val title: String,
    val tag: String,
    val tagTextColor: Color,
    val startDate: String,
    val deadline: String,
    val sideBarColor: Color
)

class TaskViewModel : ViewModel() { //TaskViewModel
    private val _toDoTasks = MutableStateFlow<List<TaskData>>(emptyList())
    val toDoTasks: StateFlow<List<TaskData>> = _toDoTasks.asStateFlow()

    private val _inProgressTasks = MutableStateFlow<List<TaskData>>(emptyList())
    val inProgressTasks: StateFlow<List<TaskData>> = _inProgressTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<TaskData>>(emptyList())
    val completedTasks: StateFlow<List<TaskData>> = _completedTasks.asStateFlow()

    fun addTask(name: String, isUrgent: Boolean, start: String, deadline: String) {
        val newTask = TaskData(
            title = name,
            tag = if (isUrgent) "緊急" else "通常",
            tagTextColor = if (isUrgent) Color.Red else Color.White,
            startDate = start,
            deadline = deadline,
            sideBarColor = if (isUrgent) Color.Red else Color.Transparent
        )
        _toDoTasks.value += newTask
    }

    fun moveTask(task: TaskData, from: String, to: String) {
        when (from) {
            "未着手" -> _toDoTasks.value -= task
            "進行中" -> _inProgressTasks.value -= task
            "完了" -> _completedTasks.value -= task
        }
        val updatedTask = when (to) {
            "完了" -> task.copy(sideBarColor = Color.Gray)
            else -> task.copy(sideBarColor = if (task.tag == "緊急") Color.Red else Color.Transparent)
        }
        when (to) {
            "未着手" -> _toDoTasks.value += updatedTask
            "進行中" -> _inProgressTasks.value += updatedTask
            "完了" -> _completedTasks.value += updatedTask
        }
    }

    fun deleteTask(task: TaskData, from: String) {
        when (from) {
            "未着手" -> _toDoTasks.value -= task
            "進行中" -> _inProgressTasks.value -= task
            "完了" -> _completedTasks.value -= task
        }
    }
}

class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApplicationTheme {
                TaskScreen()
            }
        }
    }
}

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val toDoTasks by taskViewModel.toDoTasks.collectAsState()
    val inProgressTasks by taskViewModel.inProgressTasks.collectAsState()
    val completedTasks by taskViewModel.completedTasks.collectAsState()

    var isFormVisible by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier.
        fillMaxSize()
            .background(Color.Black)
            .padding(top = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(24.dp)
                    .background(Color(0xFFEF2324),
                        shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "A",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Event Architect",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "タスク管理",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E1F25)), shape = RoundedCornerShape(8.dp), modifier = Modifier.height(36.dp)) {
                    Text(
                        "AIタスク生成",
                        color = Color(0xFFEF2324),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(onClick = { isFormVisible = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF2324)), shape = RoundedCornerShape(8.dp), modifier = Modifier.height(36.dp)) {
                    Text(
                        "新規タスク",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        AnimatedVisibility(visible = isFormVisible, enter = expandVertically(), exit = shrinkVertically()) {
            TaskEntryForm(
                taskName = taskName,
                onNameChange = { taskName = it },
                isUrgent = isUrgent,
                onUrgentChange = { isUrgent = it },
                onCancel = { isFormVisible = false },
                onAdd = { fullStart, fullDeadline ->
                    if (taskName.isNotBlank()) {
                        taskViewModel.addTask(taskName, isUrgent, fullStart, fullDeadline)
                        taskName = ""
                        isUrgent = false
                        isFormVisible = false
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 24.dp), pageSpacing = 16.dp) { pageIndex ->
            when (pageIndex) {
                0 -> TaskBoard("未着手", toDoTasks.size.toString(), toDoTasks, painterResource(id = R.drawable.outline_circle_24), { t, d -> taskViewModel.moveTask(t, "未着手", d) }, { t -> taskViewModel.deleteTask(t, "未着手") })
                1 -> TaskBoard("進行中", inProgressTasks.size.toString(), inProgressTasks, painterResource(id = R.drawable.outline_circle_circle_24), { t, d -> taskViewModel.moveTask(t, "進行中", d) }, { t -> taskViewModel.deleteTask(t, "進行中") })
                2 -> TaskBoard("完了", completedTasks.size.toString(), completedTasks, painterResource(id = R.drawable.outline_check_circle_24), { t, d -> taskViewModel.moveTask(t, "完了", d) }, { t -> taskViewModel.deleteTask(t, "完了") })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryForm(
    taskName: String,
    onNameChange: (String) -> Unit,
    isUrgent: Boolean,
    onUrgentChange: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    val context = LocalContext.current
    var startDateText by remember { mutableStateOf("日時を入力") }
    var deadlineDateText by remember { mutableStateOf("日時を入力") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E2E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("タスク名 *", color = Color.Gray, fontSize = 12.sp)
            TextField(value = taskName, onValueChange = onNameChange, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White))

            Spacer(modifier = Modifier.height(16.dp))

            Text("優先度", color = Color.Gray, fontSize = 12.sp)
            Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(false, true).forEach { urgent ->
                    val isSelected = isUrgent == urgent
                    Surface(modifier = Modifier.clickable { onUrgentChange(urgent) }, color = if (isSelected) Color(0xFFEF2324) else Color(0xFF2E2E2E), shape = RoundedCornerShape(4.dp)) {
                        Text(if (urgent) "緊急" else "通常", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("開始日", color = Color.Gray, fontSize = 12.sp)
                    Surface(
                        color = Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp).clickable {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(context, { _, y, m, d -> //日付ダイアログ
                                val date = "${m + 1}/$d"
                                TimePickerDialog(context, { _, h, min -> //時刻ダイアログ
                                    startDateText = "$date " + String.format("%02d:%02d", h, min)
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                        }
                    ) { Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) { Text(startDateText, color = Color.White, fontSize = 12.sp) } }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("締め切り", color = Color.Gray, fontSize = 12.sp)
                    Surface(
                        color = Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp).clickable {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(context, { _, y, m, d ->
                                val date = "${m + 1}/$d"
                                TimePickerDialog(context, { _, h, min ->
                                    deadlineDateText = "$date " + String.format("%02d:%02d", h, min)
                                }, 18, 0, true).show()
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                        }
                    ) { Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) { Text(deadlineDateText, color = Color.White, fontSize = 12.sp) } }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("担当者", color = Color.Gray, fontSize = 12.sp)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(all = 8.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                ) {
                    Text("田中太郎", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(5.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onAdd(startDateText, deadlineDateText) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF2324)), shape = RoundedCornerShape(8.dp)) {
                    Text("追加", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 18.dp))
                }
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)), shape = RoundedCornerShape(8.dp)) {
                    Text("キャンセル", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TaskBoard(
    title: String,
    count: String,
    tasks: List<TaskData>,
    painter: androidx.compose.ui.graphics.painter.Painter,
    onMove: (TaskData, String) -> Unit,
    onDelete: (TaskData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(Color(0xFF2E3740), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(painter, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(22.dp)
                    .background(Color(0xFFEF2324),
                        shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    count,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFF242424), shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(8.dp)
        ) {
            tasks.forEach { TaskCard(it, title, onMove, onDelete) }
        }
    }
}

@Composable
fun TaskCard(task: TaskData, currentStatus: String, onMove: (TaskData, String) -> Unit, onDelete: (TaskData) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(4.dp)) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(task.sideBarColor))
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(color = Color.DarkGray, shape = RoundedCornerShape(2.dp)) {
                        Text(task.tag, color = task.tagTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Box {
                        IconButton(onClick = { expanded = true }, modifier = Modifier.size(18.dp)) { Icon(Icons.Default.MoreVert, null, tint = Color.LightGray) }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            if (currentStatus != "未着手") DropdownMenuItem(text = { Text("未着手へ移動") }, onClick = { onMove(task, "未着手"); expanded = false })
                            if (currentStatus != "進行中") DropdownMenuItem(text = { Text("進行中へ移動") }, onClick = { onMove(task, "進行中"); expanded = false })
                            if (currentStatus != "完了") DropdownMenuItem(text = { Text("完了へ移動") }, onClick = { onMove(task, "完了"); expanded = false })
                            DropdownMenuItem(text = { Text("削除", color = Color.Red) }, onClick = { onDelete(task); expanded = false })
                        }
                    }
                }
                Text(task.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("開始: ${task.startDate}", color = Color.Gray, fontSize = 10.sp)
                    Text("期限: ${task.deadline}", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskPreview() {
    ApplicationTheme {
        TaskScreen()
    }
}