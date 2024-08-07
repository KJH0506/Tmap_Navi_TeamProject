package com.hansung.sherpa.ui.searchscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 컴포넌트의 속성(modifier)을 관리
 * 값들을 통일하기 위해서 사용하였다.
 * 현재는 싱글톤을 이용하여 제작해두었지만 나중에 별개 클래스로 분리할 예정
 */
object Property {
    // Departure TextField, Destination TextField에 사용할 공통 속성
    object TextField {
        val height = 50.dp
        val modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(height)
        val textStyle = TextStyle.Default.copy(fontSize = 12.sp)
        val shape = RoundedCornerShape(12.dp)
        val containerColor = Color.LightGray
        val textColor = Color.DarkGray
        val singleLine = true
    }

    // Search, Back, Change Button에 사용할 공통 속성
    object Button{
        val modifier = Modifier
            .width(60.dp)
            .height(50.dp)
        val shape = RoundedCornerShape(0.dp)
        val colors = Color.Transparent
    }

    // Search, Back, Change Button에 사용할 공통 속성
    object Icon{
        val modifier = Modifier.size(30.dp)
        val tint = Color.Gray
    }
}