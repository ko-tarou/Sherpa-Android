package com.example.application

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.theme.ApplicationTheme
import java.util.Calendar

class TaskActivity: ComponentActivity() {
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
fun TaskScreen() {
    var isFormVisible by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val columnWidth = (configuration.screenWidthDp * 0.85).dp
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row( //Event Architect
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box( //赤丸
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFFEF2324), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Event Architect",
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
                text = "タスク管理",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* AIタスク生成処理 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3E1F25)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "AIタスク生成",
                        color = Color(0xFFEF2324),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {isFormVisible = true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF2324)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "新規タスク",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        AnimatedVisibility( //入力フォーム
            visible = isFormVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            TaskEntryForm(
                taskName = taskName,
                onNameChange = { taskName = it },
                linkUrl = linkUrl,
                onLinkChange = { linkUrl = it },
                onCancel = { isFormVisible = false },
                onAdd = {
                    isFormVisible = false
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { pageIndex ->
            when (pageIndex) {
                0 -> TaskBoard( //未着手
                    painter = painterResource(id = R.drawable.outline_circle_24),
                    title = "未着手",
                    count = "2",
                    tasks = listOf(
                    TaskData(
                        title = "AIタスクの精度を上げる",
                        tag = "緊急",
                        tagTextColor = Color.Red,
                        deadline = "11/12",
                        sideBarColor = Color.Red
                    ),
                    TaskData(
                        title = "ミーティングの実装",
                        tag = "通常",
                        tagTextColor = Color.White,
                        deadline = "11/15",
                        sideBarColor = Color.Transparent
                    )
                ))
                1 -> TaskBoard( //進行中
                    painter = painterResource(id = R.drawable.outline_circle_circle_24),
                    title = "進行中",
                    count = "1",
                    tasks = listOf(
                    TaskData(
                        title = "見積もり依頼",
                        tag = "通常",
                        tagTextColor = Color.White,
                        deadline = "11/14",
                        sideBarColor = Color.Transparent
                    )
                ))
                2 -> TaskBoard( //完了
                    painter = painterResource(id = R.drawable.outline_check_circle_24),
                    title = "完了",
                    count = "1",
                    tasks = listOf(
                    TaskData(
                        title = "ミーティング",
                        tag = "通常",
                        tagTextColor = Color.White,
                        deadline = "11/01",
                        sideBarColor = Color.Gray
                    )
                ))
            }
        }
        Row( //3つの白い点
            Modifier
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(color, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryForm(
    taskName: String,
    onNameChange: (String) -> Unit,
    linkUrl: String,
    onLinkChange: (String) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit
) {
    val context = LocalContext.current

    // 表示用の日付文字列の状態
    var startDateText by remember { mutableStateOf("日時を選択") }
    var deadlineDateText by remember { mutableStateOf("2026/2/8") } // 初期値
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E2E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "タスク名を入力",
                color = Color.Gray,
                fontSize = 12.sp
            )
            TextField(
                value = taskName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "タスク名を入力",
                        color = Color.DarkGray
                    ) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "リンク",
                color = Color.Gray,
                fontSize = 12.sp
            )
            TextField(
                value = linkUrl,
                onValueChange = onLinkChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "https://...",
                        color = Color.DarkGray
                    ) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "開始日",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Surface(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        startDateText = "$y/${m + 1}/$d"
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = startDateText,
                                color = if(startDateText == "日時を選択") Color.Gray else Color.White,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                            Icon(
                                painterResource(id = R.drawable.outline_calendar_month_24),
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "締め切り",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Surface(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d ->
                                    deadlineDateText = "$y/${m + 1}/$d"
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = deadlineDateText,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                            Icon(
                                painterResource(id = R.drawable.outline_calendar_month_24),
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "担当者",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(all = 8.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                ) {
                    Text(
                        text = "田中太郎",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF2324)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "追加",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 18.dp)

                    )
                }
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "キャンセル",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TaskBoard(
    painter: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    count: String,
    tasks: List<TaskData>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E3740), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(22.dp)
                    .background(Color(0xFFEF2324), shape = CircleShape), contentAlignment = Alignment.Center) {
                Text(
                    text = count,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF242424), shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .padding(8.dp)
        ) {
            tasks.forEach { task ->
                TaskCard(
                    task.title,
                    task.tag,
                    tagColor = Color.DarkGray,
                    task.tagTextColor,
                    task.deadline,
                    task.sideBarColor
                )
            }
        }
    }
}

data class TaskData(
    val title: String,
    val tag: String,
    val tagTextColor: Color,
    val deadline: String,
    val sideBarColor: Color
)

@Composable
fun TaskCard(title: String, tag: String, tagColor: Color, tagTextColor: Color, deadline: String, sideBarColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(sideBarColor))
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween)
                {
                    Surface(color = tagColor, shape = RoundedCornerShape(2.dp)) {
                        Text(
                            text = tag,
                            color = tagTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Icon(
                        Icons.Default.MoreVert,
                        null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                Text(
                    text = "期限: $deadline",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
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