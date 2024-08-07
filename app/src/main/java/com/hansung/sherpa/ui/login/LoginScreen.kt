package com.hansung.sherpa.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.SherpaScreen
import com.hansung.sherpa.ui.signup.InfomationGroupSample

@Composable
fun LoginScreen(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Text(text = "Login Screen")
        Spacer(modifier = Modifier.height(50.dp))

        // 보호자 입력란
        Text("보호자 로그인 입력란")
        CaregiverArea(navController)
        Spacer(modifier = Modifier.height(50.dp))

        // 사용자 입력란
        Text("사용자 로그인 입력란")
        ProtegeArea(navController)
        Spacer(modifier = Modifier.height(50.dp))

        // 비밀번호 찾기, 회원가입 이동
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
                fontWeight = FontWeight.Bold
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
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 보호자 로그인 구성품
 */
@Composable
fun CaregiverArea(navController: NavController) {
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column {
        InfomationGroup("아이디", false) {idValue = it}
        InfomationGroup("비밀번호", false) {passwordValue = it}

        TextButton(
            // TODO: 로그인 정보로 보호자 역할 분기해야 됨
            onClick = {navController.navigate("${SherpaScreen.Home.name}")},
            colors= ButtonColors(
                contentColor = Color.Black,
                containerColor = Color(0xFF64FCD9),
                disabledContentColor = Color.Black,
                disabledContainerColor =  Color(0xFF64FCD9)
            ),
            modifier = Modifier.width(200.dp)
        ){
            Text(
                text = "로그인",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 사용자 로그인 구성품
 */
@Composable
fun ProtegeArea(navController: NavController) {

    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column {
        InfomationGroup("아이디", false) {idValue = it}
        InfomationGroup("비밀번호", false) {passwordValue = it}

        TextButton(
            // TODO: 로그인 정보로 사용자 역할 분기해야 됨
            onClick = {navController.navigate("${SherpaScreen.Home.name}")},
            colors= ButtonColors(
                contentColor = Color.Black,
                containerColor = Color(0xFF64FCD9),
                disabledContentColor = Color.Black,
                disabledContainerColor =  Color(0xFF64FCD9)
            ),
            modifier = Modifier.width(200.dp)
        ){
            Text(
                text = "로그인",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview
fun LoginPreview() {
    LoginScreen()
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
            fontSize = 10.sp,
            modifier = Modifier.width(50.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextField(
            value = value,
            onValueChange = {
                value = it
                update(value)
            },
            modifier = Modifier.width(200.dp)
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