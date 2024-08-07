package com.hansung.sherpa.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.SherpaScreen

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

@Composable
fun CaregiverArea(navController: NavController) {
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column {
        Row(
           verticalAlignment = Alignment.CenterVertically
        ){
            Text("아이디")
            TextField(
                value = idValue,
                onValueChange = {idValue = it}
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("비밀번호")
            TextField(
                value = passwordValue,
                onValueChange = {passwordValue = it}
            )
        }

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

@Composable
fun ProtegeArea(navController: NavController) {

    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("아이디")
            TextField(
                value = idValue,
                onValueChange = {idValue = it}
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("비밀번호")
            TextField(
                value = passwordValue,
                onValueChange = {passwordValue = it}
            )
        }

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