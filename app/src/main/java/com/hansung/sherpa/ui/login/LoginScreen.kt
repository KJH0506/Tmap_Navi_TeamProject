package com.hansung.sherpa.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.R
import com.hansung.sherpa.SherpaScreen
import com.hansung.sherpa.StaticValue
import com.hansung.sherpa.ui.signup.InfomationGroupSample
import com.hansung.sherpa.user.UserManager

val SherpaColor = Color(0xFF64FCD9)
val bmHanna = FontFamily(Font(R.font.bm_hanna_pro, FontWeight.Bold))

@Composable
fun LoginScreen(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
        .background(Color.White)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            Box(
                modifier = Modifier.fillMaxWidth().height(500.dp).clip(RoundedCornerShape(0.dp,0.dp,800.dp,800.dp)).background(SherpaColor)
            )
            Box(
                modifier = Modifier.fillMaxWidth().height(500.dp).background(Color.White)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleArea()
            LoginArea(navController)
            FindAccountArea(navController)
        }
    }
}

@Composable
fun TitleArea() {
    Row(verticalAlignment = Alignment.CenterVertically){
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "디자인을 위한 사람 아이콘",
            modifier = Modifier.size(60.dp,60.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "로그인",
            fontSize = 40.sp,
            fontFamily = bmHanna
        )
    }
}

@Composable
fun FindAccountArea(navController: NavController) {
    // 비밀번호 찾기, 회원가입 이동
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = {},
            colors= ButtonColors(
                contentColor = Color.Black,
                containerColor = Color.Transparent,
                disabledContentColor = Color.Black,
                disabledContainerColor =  Color.Transparent
            ),
            modifier = Modifier.wrapContentWidth()
        ){
            Text(
                text = "아이디/비밀번호를 잃어버리셨나요?",
                fontSize = 18.sp,
                fontFamily = bmHanna,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
        TextButton(
            onClick = {navController.navigate("${SherpaScreen.SignUp.name}")},
            colors= ButtonColors(
                contentColor = Color(0xFF34DFD5),
                containerColor = Color.Transparent,
                disabledContentColor = Color(0xFF34DFD5),
                disabledContainerColor =  Color.Transparent
            ),
            modifier = Modifier.wrapContentWidth()
        ){
            Text(
                text = "계정이 없으신가요?",
                fontSize = 18.sp,
                fontFamily = bmHanna,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
    }
}

/**
 * 보호자 로그인 구성품
 */
@Composable
fun LoginArea(navController: NavController) {
    val context = LocalContext.current
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White).padding(20.dp, 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        InfomationGroup("아이디", false) {idValue = it}
        InfomationGroup("비밀번호", false) {passwordValue = it}

        TextButton(
            // TODO: 로그인 정보로 계정 역할 분기해야 됨
            // TODO: 값 전송 시 공백 제거할 것
            onClick = { if(login(navController, idValue, passwordValue)){
                Toast.makeText(context,"로그인 실패! 아이디 비밀번호를 확인해 주세요",Toast.LENGTH_SHORT).show()
                return@TextButton }
                      },
            colors= ButtonColors(
                contentColor = Color.Black,
                containerColor = SherpaColor,
                disabledContentColor = Color.Black,
                disabledContainerColor =  SherpaColor
            ),
            modifier = Modifier.width(200.dp)
        ){
            Text(
                text = "로그인",
                fontFamily = bmHanna
            )
        }
    }
}

/**
 * 나중에 로그인 만드실 분 생각해서 모듈화해둠 알아서 변경해서 쓸 것
 *
 * @param titleText
 * @param buttonToggle
 * @param buttonText 버튼에 들어갈 텍스트 ex) (아이디) 중복검사
 * @param modifier Row modifier와 연결 되어있다. 혹시 설정하고 싶으면 하시오.
 * @param buttonClick 람다함수. buttonToggle을 true로 설정했을 때 사용하면 된다. 버튼 눌렀을 때 필요한 동작 하면됨
 * @param update 연결시켜둔 문자열에 최종적으로 값이 들어감.
 *
 * @sample InfomationGroupSample
 */
@Composable
fun InfomationGroup(titleText:String, buttonToggle:Boolean, buttonText:String = "", buttonClick: (String) -> Unit = {}, modifier:Modifier = Modifier, update: (String) -> Unit) {
    var value by remember { mutableStateOf("")}
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = titleText,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(50.dp),
            fontSize = 15.sp,
            fontFamily = bmHanna,
        )
        Spacer(modifier = Modifier.width(20.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {
                value = it
                update(value)
            },
            modifier = Modifier.size(200.dp, 50.dp),
            shape = RoundedCornerShape(15.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        if(buttonToggle){
            Button(onClick = { buttonClick(value) }) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun InfomationGroupSample() {
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var confirmPasswordValue by remember { mutableStateOf("") }
    var telValue by remember { mutableStateOf("") }

    InfomationGroup("아이디", true, "중복검사", {/*중복검사 API*/}) { idValue = it }
    InfomationGroup("비밀번호", false) { passwordValue = it }
    InfomationGroup("비밀번호 확인", false) { confirmPasswordValue = it }
    InfomationGroup("전화번호", true, "인증하기", {/* 전화 인증 API */}) { telValue = it }
}

fun login(navController: NavController, email: String, password: String) : Boolean {
    val loginResponse = UserManager().login(email, password)!!
    val data = loginResponse.data!!
    if(data.userId == 0 || data.userId == null) {
        return true
    } else {
        StaticValue.userInfo = data
        UserManager().updateFcm()
        navController.navigate("${SherpaScreen.Home.name}")
    }
    return false
}

@Composable
@Preview
fun LoginPreview() {
    LoginScreen()
}