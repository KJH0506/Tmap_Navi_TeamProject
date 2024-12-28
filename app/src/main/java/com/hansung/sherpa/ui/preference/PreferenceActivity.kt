package com.hansung.sherpa.ui.preference

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.StaticValue
import com.hansung.sherpa.ui.preference.calendar.CalendarScreen
import com.hansung.sherpa.ui.preference.caregiver.CaregiverSyncScreen
import com.hansung.sherpa.ui.preference.emergency.EmergencySettingsScreen
import com.hansung.sherpa.ui.preference.policyinformation.PolicyComposable
import com.hansung.sherpa.ui.preference.updateinformation.UpdateInfoComposable
import com.hansung.sherpa.ui.preference.usersetting.UserSettingScreen
import com.hansung.sherpa.ui.theme.lightScheme

data class PreferenceItemData(val title : String, val screenOption : PreferenceScreenOption)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreferenceComposable(navController: NavController){
    PreferenceScreen {
        navController.popBackStack()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreferenceScreen(
    onFinish : () -> Unit
){

    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold (

        topBar = {
            if(currentRoute == PreferenceScreenOption.PreferenceScreen.name) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.DarkGray
                    ),

                    title = {
                        Text(
                            text = "설정",
                            style = TextStyle(
                                fontFamily = FontFamily.Cursive,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onFinish() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "뒤로가기")
                        }
                    },
                )
            }
        },
    ){
    innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = Color.White
        ) {
            PreferenceItems(navController)

            NavHost(navController = navController, startDestination = PreferenceScreenOption.PreferenceScreen.name) {
                composable(route = PreferenceScreenOption.CALENDAR.name){
                    CalendarScreen(navController = navController)
                }
                composable(route = PreferenceScreenOption.CAREGIVER.name){
                    MaterialTheme(colorScheme = lightScheme) {
                        TopAppBarScreen( title = "보호자 연동",
                            { navController.popBackStack() }, { CaregiverSyncScreen() }
                        )
                    }
                }
                composable(route = PreferenceScreenOption.EMERGENCY.name){
                    EmergencySettingsScreen { navController.popBackStack() }
                }
                composable(route = PreferenceScreenOption.PRIVACY_POLICY.name){
                    PolicyComposable{navController.popBackStack()}
                }
                composable(route = PreferenceScreenOption.APP_INFORMATION.name){
                    UpdateInfoComposable { navController.popBackStack() }
                }
                composable(route = PreferenceScreenOption.USER.name){
                    UserSettingScreen(
                        pickMediaCallback = { StaticValue.pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        onFinish = { navController.popBackStack()}
                    )
                }
                composable(route = PreferenceScreenOption.PreferenceScreen.name) {
                    // TODO: 추후 수정 
                }
            }
        }
    }
}

@Composable
fun PreferenceItems(
    navController: NavController
){
    val callback : (PreferenceScreenOption) -> Unit = { preferenceScreenOption ->
        navController.navigate(preferenceScreenOption.name)
    }
    LazyColumn{
        item {
            Divider(text = "사용자")
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "사용자 설정", screenOption = PreferenceScreenOption.USER),
                    icon = Icons.Default.AccountCircle, color = Color.LightGray, callback)

            PreferenceItem(preferenceItemData = PreferenceItemData(title = "보호자 연동", screenOption = PreferenceScreenOption.CAREGIVER),
                icon = Icons.Default.Face, color = Color(100,200,190), callback)

            PreferenceItem(preferenceItemData = PreferenceItemData(title = "긴급 연락처", screenOption = PreferenceScreenOption.EMERGENCY),
                icon = Icons.Default.FavoriteBorder, color = Color(88,190,230), callback)

            PreferenceItem(preferenceItemData = PreferenceItemData(title = "캘린더 설정", screenOption = PreferenceScreenOption.CALENDAR),
                icon = Icons.Default.DateRange, color = Color(255,180,180), callback)

            PreferenceItem(preferenceItemData = PreferenceItemData(title = "알림 설정", screenOption = PreferenceScreenOption.NOTIFICATION),
                icon = Icons.Default.Notifications, color = Color(255,200,100), callback)

            Divider(text = "고객지원")
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "공지사항", screenOption = PreferenceScreenOption.NOTICEBOARD),
                icon = Icons.Default.Info, color = Color(15,27,63), callback)
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "전화 문의하기", screenOption = PreferenceScreenOption.CALL_INQUIRY),
                icon = Icons.Default.Call, color = Color(177,224,177), callback)
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "이메일 문의하기", screenOption = PreferenceScreenOption.EMAIL_INQUIRY),
                icon = Icons.Default.Email, color = Color(137,210,235), callback)
            Divider(text = "서비스 정보")
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "업데이트 정보", screenOption = PreferenceScreenOption.APP_INFORMATION),
                icon = Icons.Default.Refresh, color = Color.Gray, callback)
            PreferenceItem(preferenceItemData = PreferenceItemData(title = "개인정보 취급 방침", screenOption = PreferenceScreenOption.PRIVACY_POLICY),
                icon = Icons.Default.Person, color = Color.Black, callback)
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
    preferenceItemData: PreferenceItemData,
    icon : ImageVector,
    color : Color,
    callback : (PreferenceScreenOption) -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(
                onClick = { callback(preferenceItemData.screenOption) },
                indication = ripple(bounded = true),
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
                text = preferenceItemData.title,
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
}