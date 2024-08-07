package com.hansung.sherpa.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat

/**
 * 결과 경로 리스트를 정렬하여 보여주기 설정 영역
 *
 * @param searchingTime 경로를 요청한 시간
 * 
 * ※ Preview는 SearchScreen에서 실행할 것
 */
@Composable
fun SortingArea(searchingTime:Long) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(Color.White)
        .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        /**
         * SearchingTime
         *
         * 대중교통 경로가 검색 된 시간
         */
        Text("${SimpleDateFormat("hh:mm").format(searchingTime)}" )

        /**
         * SortingAlgorithm
         * 
         * 대중교통 리스트가 정렬되는 기준
         */
        Text("최적경로 순")
    }
}