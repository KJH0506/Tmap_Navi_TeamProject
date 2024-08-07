package com.hansung.sherpa.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hansung.sherpa.SherpaScreen

@Composable
fun SignupScreen(navController: NavController = rememberNavController(), modifier: Modifier = Modifier){
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)){
        item{
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
        }
    }
}

@Composable
fun CaregiverArea(navController: NavController){
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var confirmPasswordValue by remember { mutableStateOf("") }
    var userNameValue by remember { mutableStateOf("") }
    var telValue by remember { mutableStateOf("") }
    var addressValue by remember { mutableStateOf("") }
    var detailAddressValue by remember { mutableStateOf("") }

    Column {
        InfomationGroup("아이디", true, "중복검사", {/*중복검사 API*/}) { idValue = it }
        InfomationGroup("비밀번호", false) { passwordValue = it }
        InfomationGroup("비밀번호 확인", false) { confirmPasswordValue = it }
        InfomationGroup("이름", false) { userNameValue = it }
        InfomationGroup("전화번호", true, "인증하기", {/* 전화 인증 API */}) { telValue = it }
        InfomationGroup("주소", true, "주소검색", {/* 주소 검색 API */}) { addressValue = it }
        InfomationGroup("상세주소", false) { detailAddressValue = it }
        Text("이용약관1")
        Text("이용약관2")
        Text("이용약관3")
        Spacer(modifier = Modifier.height(10.dp))
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
                text = "확인",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProtegeArea(navController: NavController) {
    var idValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var confirmPasswordValue by remember { mutableStateOf("") }
    var userNameValue by remember { mutableStateOf("") }
    var telValue by remember { mutableStateOf("") }
    var addressValue by remember { mutableStateOf("") }
    var detailAddressValue by remember { mutableStateOf("") }
    var caregiverId by remember { mutableStateOf("") }

    Column {
        InfomationGroup("아이디", true, "중복검사", {/*중복검사 API*/}) { idValue = it }
        InfomationGroup("비밀번호", false) { passwordValue = it }
        InfomationGroup("비밀번호 확인", false) { confirmPasswordValue = it }
        InfomationGroup("이름", false) { userNameValue = it }
        InfomationGroup("전화번호", true, "인증하기", {/* 전화 인증 API */}) { telValue = it }
        InfomationGroup("주소", true, "주소검색", {/* 주소 검색 API */}) { addressValue = it }
        InfomationGroup("상세주소", false) { detailAddressValue = it }
        InfomationGroup("보호자 아이디", true, "연동하기" ,{/* 보호자 인증 API */}) { caregiverId = it }
        Text("이용약관1")
        Text("이용약관2")
        Text("이용약관3")
        Spacer(modifier = Modifier.height(10.dp))
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
                text = "확인",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview
fun SignupPreview() {
    SignupScreen()
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
        Text(titleText)
        Spacer(modifier = Modifier.width(10.dp))
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