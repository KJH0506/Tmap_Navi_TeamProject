package com.hansung.sherpa.searchscreen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansung.sherpa.busarrivalinfo.ODsayBusArrivalInfoRequest
import com.hansung.sherpa.compose.busarrivalinfo.BusArrivalInfoManager
import com.hansung.sherpa.compose.chart.Chart
import com.hansung.sherpa.itemsetting.BusLane
import com.hansung.sherpa.itemsetting.BusSectionInfo
import com.hansung.sherpa.itemsetting.SubPath
import com.hansung.sherpa.itemsetting.SubwayLane
import com.hansung.sherpa.itemsetting.SubwaySectionInfo
import com.hansung.sherpa.itemsetting.TransportRoute
import java.text.SimpleDateFormat

/**
 * 확장 되는 리스트를 구현하기 위한 Composable
 *
 * 탐색된 경로의 전체(요약) 정보가 담기는 영역이다.
 * ※ (2024-07-30) 리스트 확장 후 화면을 밑으로 내렸다가 올리면 리스트가 자동으로 닫히는 오류가 존재한다.
 *
 * @param route 목적지까지 이동하기 위한 전체 경로가 담겨있는 객체
 * @param searchingTime 경로를 요청한 시간
 * @param timerFlag 대중교통 도착정보를 새로고침하기 위한 Flag이다.
 *
 * ※ Preview는 SearchScreen에서 실행할 것
 */
@Composable
fun ExpandableCard(route: TransportRoute, searchingTime:Long, timerFlag: Boolean) {
    // 확장 정보를 저장하기 위한 변수 TODO: 오류원인 수정 필요
    var expandedState by remember { mutableStateOf(false) }

    // 확장 시 화살표 방향을 돌리기 위함
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardColors(Color.White, Color.DarkGray, Color.White, Color.DarkGray),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.Top){
                Row(verticalAlignment = Alignment.Bottom){
                    /**
                     * Total Time Text
                     *
                     * 경로의 전체 소요 시간이 기술된다.
                     */
                    Text(text = hourOfMinute(route.info.totalTime), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(30.dp))

                    /**
                     * Arrival Time Text
                     *
                     * 경로의 도착 시간이 기술된다.
                     */
                    Text(text = "${SimpleDateFormat("hh:mm").format(searchingTime + route.info.totalTime*60*1000)} 도착" )
                }
                Spacer(modifier = Modifier.weight(1f))
                /**
                 * (Expand/ Collapse) Button
                 * 
                 * Card의 확장, 축소를 시각적으로 보여주는 버튼
                 */
                IconButton(
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        tint = Property.Icon.tint,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            /**
             * Route Chart
             *
             * 대중교통 경로의 시간을 차트로 안내한다.
             */
            Chart(route)

            /**
             * Expand Card
             *
             * Card가 확장된 경우 나타난다.
             */
            if (expandedState) {
                route.subPath.forEach{
                    ExpandItem(it, timerFlag)
                }
            }
        }
    }
}

/**
 * 확장 시 세부 정보를 표시하는 영역
 *
 * 이동수단 단위의 정보를 다룬다.
 *
 * @param subPath 이동수단 단위의 경로 정보
 * @param timer 대중교통 도착정보의 갱신 필요 여부(상위 클래스에서 값이 바뀌면 갱신이 이루어진다.)
 *
 * ※ Preview는 SearchScreen에서 실행할 것
 */
@Composable
fun ExpandItem(subPath: SubPath, timerFlag:Boolean) {
    Row(modifier = Modifier.padding(5.dp)){
        /**
         * Starting Area Text
         * 
         * 이동수단의 출발지 명칭
         * ※ (2024-08-04) 현재 도보 정보가 사전에 들어오지 않아 주변 지역 명칭을 지정할 수 없다.
         */
        Text("${subPath.sectionInfo.startName?:"도보"}")
        Spacer(modifier = Modifier.width(10.dp))

        /**
         * Section Time Text
         *
         * 이동수단을 이용한 총 이동 시간
         */
        Text(hourOfMinute(subPath.sectionInfo.sectionTime!!))
        Spacer(modifier = Modifier.weight(1f))

        /**
         * Traffic Icon
         *
         * 이동수단의 종류를 시각적으로 보여주기 위한 Icon
         */
        Icon(
            imageVector = typeOfIcon(subPath.trafficType),
            contentDescription = "Default Icon: 'X'(Close)"
        )
        Spacer(modifier = Modifier.width(10.dp))

        /**
         * Traffic Name Text
         * 
         * 이동 수단의 명칭 ex) 지하철: n호선, 버스: n-m번, 도보: 도보
         */
        Text(getLaneName(subPath))
        Spacer(modifier = Modifier.width(10.dp))

        /**
         * 이동수단 도착 정보를 갱신하기 위한 영역
         *
         * 상위 Composable에서 timerFlag가 변경된다면 해당 영역이 실행된다.
         */
        var waitingTime by remember { mutableStateOf(-1) }
        LaunchedEffect(timerFlag) {
            //waitingTime = BusArrivalInfoManager().getBusArrivalInfoList(BusArrivalInfoRequest(cityCode = 25, nodeId = "DJB8001793", routeId = "DJB30300002"))?.response?.body?.items?.item?.arrtime?:-1

            // ODsay ArrivalInfo TODO("명칭에 Bus가 작성되어 있는데 수정 예정")
            val (stationID,routeID) = when(subPath.trafficType) {
                // 지하철의 경우
                1 -> {
                    val sectionInfo = subPath.sectionInfo as SubwaySectionInfo
                    val lane = sectionInfo.lane[0] as SubwayLane
                    Pair(sectionInfo.startID, lane.subwayCode)
                }
                // 버스의 경우
                2 -> {
                    val sectionInfo = subPath.sectionInfo as BusSectionInfo
                    val lane = sectionInfo.lane[0] as BusLane
                    Pair(sectionInfo.startID, lane.busID)
                }
                // 예외 처리
                else -> Pair(-1, -1)
            }

            // 도보의 경우 정보가 없어 -1을 반환한다. (-1은 "도착 정보 없음"을 의미한다.)
            waitingTime = if(subPath.trafficType != 3)
                BusArrivalInfoManager().getODsayBusArrivalInfoList(
                    ODsayBusArrivalInfoRequest(stationID = stationID,routeIDs = routeID)
                )?.result?.real?.get(0)?.arrival1?.arrivalSec?:-1 else -1
            Log.d("explain", "stationID: ${stationID}, routeIDS: ${routeID}, 도착 남은 시간: ${waitingTime}")
        }

        /**
         * Transport Waiting Time Text
         *
         * 이동수단 도착 정보
         */
        Text(minuteOfSecond(waitingTime))
    }
}