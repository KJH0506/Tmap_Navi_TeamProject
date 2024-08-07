package com.hansung.sherpa.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val titleColor = Color.Black
val contentColor = Color.Black
val buttonColor = Color(0xFF34DFD5)

/**
 * 보행자 내비게이션 공용 에러 Dialog
 * 참고: https://developer.android.com/develop/ui/compose/components/dialog?hl=ko
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
fun SherpaDialog(title:String, message:List<String>, confirmButtonText:String, dissmissButtonText:String = "", onDismissRequest: () -> Unit = {}, onConfirmation: () -> Unit) {
    AlertDialog(
        icon = {},
        onDismissRequest = onDismissRequest,
        title = {Text(
            text = title,
            fontWeight = FontWeight.Bold
        )},
        text = {
            Row(
                modifier = Modifier.width(400.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Column{
                    message.forEach {
                        Text(it)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(
                    text = confirmButtonText,
                    fontWeight = FontWeight.Bold,
                    color = buttonColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = dissmissButtonText,
                    fontWeight = FontWeight.Bold,
                    color = buttonColor
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = titleColor,
        textContentColor = contentColor
    )
}

@Composable
fun ErrorDialogSample() {
    // 거절 기능 안넣고 싶은 경우
    SherpaDialog("제목", listOf("내용1", "내용2"), "확인") {
        //TODO: 확인 버튼을 누를 시 실행될 람다 함수
    }

    // 거절 기능 넣고 싶은 경우
    SherpaDialog("제목", listOf("내용1", "내용2"), "확인", "거부", { println("화면 닫을 때 실행할 람다 함수")}) {
        //TODO: 확인 버튼을 누를 시 실행될 람다 함수
    }
}

@Preview
@Composable
fun SherpaDialogPreview(){
    SherpaDialog("제목", listOf("내용1: 프리뷰 화면", "내용2: 테스트 텍스트 입니다."), "확인", "거부") { println("test") }
}