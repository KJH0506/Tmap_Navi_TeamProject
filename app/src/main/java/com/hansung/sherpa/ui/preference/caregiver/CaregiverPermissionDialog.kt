package com.hansung.sherpa.ui.preference.caregiver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import com.hansung.sherpa.ui.common.ErrorDialogSample
import com.hansung.sherpa.ui.common.SherpaButton1
import com.hansung.sherpa.ui.common.SherpaButton2
import com.hansung.sherpa.ui.theme.PretendardVariable
import com.hansung.sherpa.user.UpdateUserRelationRequest
import com.hansung.sherpa.user.UserManager

val backgroundCardColor = Color(0xFFFFFFFF)
val nomalTextColor = Color.Black
val lightTextColor = Color(0xFF8F8F8F)
val sherpaThemeColor = Color(0xFF34DFD5)

/**
 * SherpaDialog 객체를 하위 Area에서도 매개변수 하나로 조작할 수 있도록 만든 클래스
 *
 * @property show 화면을 보이게 하는 flag
 * @property title Dialog 제목
 * @property message Dialog 내용 ※ String 하나가 한 줄을 의미한다.
 * @property confirmButtonText 승인 버튼 글씨
 * @property dismissButtonText 거절 버튼 글씨
 * @property onDismissRequest 거절 버튼 터치 혹은 Dialog 밖 터치 시 실행
 * @property onConfirmation 승인 버튼 터치 시 실행
 */
data class SherpaDialogParm(
    var show: MutableState<Boolean> = mutableStateOf(false),
    var title: String = "",
    var message:List<String> = listOf(),
    var confirmButtonText:String = "",
    var dismissButtonText:String = "",
    var onDismissRequest: () -> Unit = {},
    var onConfirmation: () -> Unit = {},
){
    /**
     * 생성할 다이얼로그의 내용을 작성하는 함수
     *
     * @param title Dialog 제목
     * @param message Dialog 내용 ※ String 하나가 한 줄을 의미한다.
     * @param confirmButtonText 승인 버튼 글씨 ※ Default: "확인"
     * @param dismissButtonText 거절 버튼 글씨 ※ Defalut: ""(버튼이 보이지 않는다.)
     * @param onDismissRequest 거절 버튼 터치 혹은 Dialog 밖 터치 시 실행 ※ Defalut: 화면 닫기
     * @param onConfirmation 승인 버튼 터치 시 실행 ※ Defalut: 화면 닫기
     */
    fun setParm(
        title:String,
        message: List<String>,
        confirmButtonText: String = "확인",
        dismissButtonText: String = "",
        onDismissRequest: () -> Unit = {show.value = false},
        onConfirmation: () -> Unit = {show.value = false}
    ){
        this.title = title
        this.message = message
        this.confirmButtonText = confirmButtonText
        if(dismissButtonText != "") this.dismissButtonText = dismissButtonText
        if(onDismissRequest != {show.value = false}) this.onDismissRequest = onDismissRequest
        if(onConfirmation != {show.value = false}) this.onConfirmation = onConfirmation
        this.show.value = true
    }
}
/**
 * SherpaDialog 확장 버전
 *
 * @param title 제목
 * @param message 본문 인덱스 하나당 줄 바꿈으로 생각하면 됨
 * @param confirmButtonText 확인 버튼에 나올 메세지
 * @param dissmissButtonText 닫기 버튼에 나올 메세지 Default: ""(None)
 * @param onDismissRequest 닫기 버튼 누를 시 동작할 작업, Dialog 밖을 눌러 닫을 시 수행할 작업
 * @param onConfirmation 확인 버튼 누를 시 동작할 작업
 *
 * @sample ErrorDialogSample
 */
@Composable
fun CaregiverPermissionDialog(
    title:String,
    message:List<String>,
    confirmButtonText:String,
    dismissButtonText:String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit = {},
    caregiverId: Int,
    caretakerId: Int,
    caregiverEmail: String
) {
    var inputText by remember { mutableStateOf("") }
    Dialog (
        onDismissRequest = onDismissRequest
    ) {
        Card (
            modifier = Modifier.size(320.dp, 250.dp),
            colors = CardColors(backgroundCardColor, backgroundCardColor, backgroundCardColor, backgroundCardColor),
            shape = RoundedCornerShape(12.dp)
        ){
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /**
                 * Title Text
                 *
                 * 다이얼로그 제목
                 */
                Text(
                    text = "사용자 인증",
                    color = nomalTextColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PretendardVariable
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                            text = "보호자를 인증합니다.",
                            color = sherpaThemeColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PretendardVariable
                        )
                        Text(
                            text = "아이디: " + caregiverEmail,
                            color = lightTextColor,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PretendardVariable
                        )
                    TextField(inputText, { inputText = it },Modifier.padding(10.dp), placeholder = { Text("사용자와의 관계를 입력하시오") })
                }

                Row (horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    /**
                     * Dismiss Button
                     *
                     * 거절 버튼
                     * ※ 텍스트 내용이 존재하지 않다면 버튼이 보이지 않는다.
                     */
                    if(dismissButtonText != "") SherpaButton1(
                        dismissButtonText = dismissButtonText,
                        onDismissRequest = onDismissRequest
                    )
                    /**
                     * Confirm Button
                     *
                     * 승인 버튼
                     */
                    SherpaButton2(
                        confirmButtonText = confirmButtonText,
                        onConfirmation = {
                                val request = UpdateUserRelationRequest(
                                    caregiverId = caregiverId,
                                    caretakerId = caretakerId,
                                    relation = inputText
                                )
                                UserManager().updateRelation(request)
                                onConfirmation()
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SherpaDialogPreview(){
    CaregiverPermissionDialog("로그인 실패", listOf("이메일 혹은 비밀번호를","확인해 주세요"), "확인", "", {}, {}, 0, 0, "")
}