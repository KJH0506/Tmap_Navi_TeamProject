package com.hansung.sherpa.ui.searchscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hansung.sherpa.itemsetting.TransportRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 경로 요청 후 실직적으로 보여줄 경로 리스트 영역
 *
 * @param routeList 요청한 경로 결과 리스트
 * @param searchingTime 검색을 한 시간
 *
 * ※ Preview는 SearchScreen에서 실행할 것
 */
@Composable
fun RouteListArea(navController: NavHostController, routeList:List<TransportRoute>, searchingTime:Long){
    /**
     * 30초 단위로 대중교통 도착 정보를 갱신하기 위한 Coroutine이다.
     */
    var timerFlag by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            while(true){
                delay(1000 * 30)
                timerFlag = !timerFlag
            }
        }
    }

    /**
     * Route List LazyColumn
     *
     * 이동할 수 있는 전체 대중교통 리스트가 나온다.
     */
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(routeList){
            ExpandableCard(navController, it, searchingTime, timerFlag)
        }
    }
}