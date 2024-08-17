package com.hansung.sherpa.ui.preference

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansung.sherpa.ui.preference.caregiver.CaregiverSyncScreen
import com.hansung.sherpa.ui.preference.emergency.EmergencySettingsScreen

class PreferenceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferenceScreen { screenName ->
                when(screenName){
                    "캘린더 설정" -> {
                        val intent = Intent(this, CalendarActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreferenceScreen(
    callback : (String) -> Unit
){
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("설정") }
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.DarkGray
                ),

                title = {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        title = "설정"
                        selectedItem = null
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기")
                    }
                },
            )
        },
    ){
            innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = Color.White
        ) {
            // selectedItem의 값에 따라 다른 Composable을 표시
            when (selectedItem) {
                "긴급 연락처" -> {
                    EmergencySettingsScreen()
                    title = "긴급 연락처"
                }
                "알림 설정" -> {
                    AlarmSettingsScreen()
                    title = "알림"
                }
                "보호자 연동" -> {
                    CaregiverSyncScreen()
                    title = "보호자 연동"
                }
                else -> PreferenceItems { item ->
                    if (item == "알림 설정") {
                        selectedItem = item
                    }
                    else if(item == "긴급 연락처"){
                        selectedItem = item
                    }
                    if (item == "보호자 연동") {
                        selectedItem = item
                    }
                    else {
                        callback(item)
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceItems(
    callback : (String) -> Unit
){
    LazyColumn{
        item {
            Divider(text = "사용자")
            PreferenceItem(text = "사용자 설정", icon = Icons.Default.AccountCircle, color = Color.LightGray, callback)
            PreferenceItem(text = "보호자 연동", icon = Icons.Default.Face, color = Color(100,200,190), callback)
            PreferenceItem(text = "긴급 연락처", icon = Icons.Default.FavoriteBorder, color = Color(88,190,230), callback)
            PreferenceItem(text = "캘린더 설정", icon = Icons.Default.DateRange, color = Color(255,180,180), callback)
            PreferenceItem(text = "알림 설정", icon = Icons.Default.Notifications, color = Color(255,200,100), callback)
            Divider(text = "고객지원")
            PreferenceItem(text = "공지사항", icon = Icons.Default.Info, color = Color(15,27,63), callback)
            PreferenceItem(text = "전화 문의하기", icon = Icons.Default.Call, color = Color(177,224,177), callback)
            PreferenceItem(text = "이메일 문의하기", icon = Icons.Default.Email, color = Color(137,210,235), callback)
            Divider(text = "서비스 정보")
            PreferenceItem(text = "업데이트 정보", icon = Icons.Default.Refresh, color = Color.Gray, callback)
            PreferenceItem(text = "개인정보 취급 방침", icon = Icons.Default.Person, color = Color.Black, callback)
        }

    }
}

@Composable
fun Divider(text : String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(240, 240, 240))
    ){
        Text(
            text = text,
            style = TextStyle(
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun PreferenceItem(
    text : String,
    icon : ImageVector,
    color : Color,
    callback : (String) -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(
                onClick = { callback(text) },
                indication = rememberRipple(bounded = true),
                interactionSource = interactionSource
            )
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .drawBehind {
                    val strokeWidth = 1 * density
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        Color(240, 240, 240),
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )
                }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(color = color)
            )
            Spacer(modifier = Modifier.width(8.dp)) // Image와 Text 사이에 공간 추가
            Text(
                text = text,
                style = TextStyle(
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .weight(1f) // Text가 남은 공간을 차지하도록 설정
                    .align(Alignment.CenterVertically)
            )
            Image(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }

    }
}


@Preview
@Composable
fun PreviewPreferenceScreen(){
    PreferenceActivity()
}