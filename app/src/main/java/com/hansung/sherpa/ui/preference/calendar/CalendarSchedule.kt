package com.hansung.sherpa.ui.preference.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansung.sherpa.StaticValue
import com.hansung.sherpa.schedule.Location
import com.hansung.sherpa.schedule.Route
import com.hansung.sherpa.schedule.RouteData
import com.hansung.sherpa.schedule.RouteManager
import com.hansung.sherpa.schedule.ScheduleManager
import com.hansung.sherpa.schedule.Schedules
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * 캘린더 하단 현재 날짜에 해당하는 날짜 출력
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentDateColumn(
    selectedDate: LocalDate
){
    var str = selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 "))
    str += when(selectedDate.dayOfWeek){
        java.time.DayOfWeek.MONDAY -> "(월)"
        java.time.DayOfWeek.TUESDAY -> "(화)"
        java.time.DayOfWeek.WEDNESDAY -> "(수)"
        java.time.DayOfWeek.THURSDAY -> "(목)"
        java.time.DayOfWeek.FRIDAY -> "(금)"
        java.time.DayOfWeek.SATURDAY -> "(토)"
        java.time.DayOfWeek.SUNDAY -> "(일)"
        else -> ""
    }

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 4.dp
        )
    ) {
        Text(
            text = str,
            style = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/**
 * 캘린더 하단에 스케줄 출력되는 Composable
 *
 * @param scheduleDataList 스케줄 리스트
 * @param updateScheduleData 스케줄 업데이트 시 callback
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleColumns(
    scheduleDataList: SnapshotStateList<ScheduleData>,
    updateScheduleData : (LocalDate?) -> Unit
){
    var isEmpty by remember { mutableStateOf(true) }
    val routeManager = RouteManager()
    val scheduleManager = ScheduleManager()

    /**
     * 하루 종일 스케줄 -> 제일 상단
     * it not -> 시간 순
     */
    scheduleDataList.sortWith(
        compareBy<ScheduleData> { !it.isWholeDay.value }
            .thenBy { if (it.isWholeDay.value) it.title.value else "" }
            .thenBy { if (!it.isWholeDay.value) it.startDateTime.longValue else Long.MAX_VALUE }
    )

    /**
     * 일정 삭제 시 callback
     */
    val onDelete : (ScheduleData) -> Unit = { deleteItem ->
        // TODO: 삭제한 deleteItem -> 서버에서도 삭제
        scheduleManager.deleteSchedules(deleteItem.scheduleId)
        if(deleteItem.routeId != null)
            routeManager.deleteRoute(deleteItem.routeId!!)
        updateScheduleData(null)
    }

    /**
     * isEmpty = (스케줄 사이즈 == 0)
     */
    LaunchedEffect(scheduleDataList.size) {
        isEmpty = when(scheduleDataList.size){
            0 -> true
            else -> false
        }
    }

    when(isEmpty){
        // 스케줄 리스트가 비었을 때 텍스트 출력
        true -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 90.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "등록된 일정이 없습니다.")
            }
        }
        // 비지 않았을 때 스케줄 출력
        false -> {
            LazyColumn(modifier = Modifier
                .heightIn(max = 700.dp)
                .wrapContentSize()
                .fillMaxWidth()
                .padding(bottom = 80.dp)
            ){
                items(scheduleDataList, key = { it.scheduleId }) { item ->
                    ScheduleColumn(
                        updateScheduleData = updateScheduleData,
                        onDelete = onDelete,
                        scheduleData = item
                    )
                }
            }
        }
    }
}

/**
 * 날짜에 해당하는 스케줄
 *
 * @param updateScheduleData 스케줄 업데이트 시 callback
 * @param scheduleData 각 스케줄 데이터
 * @param onDelete 삭제 시 호출할 callback
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleColumn(
    updateScheduleData: (LocalDate?) -> Unit,
    scheduleData: ScheduleData,
    onDelete : (ScheduleData) -> Unit
){
    // API 클래스
    val scheduleManager = ScheduleManager()
    val routeManager = RouteManager()

    val scheduleDeleteDialog = remember { mutableStateOf(false) } // 삭제 모달 flag
    var state by remember { mutableStateOf(true) } // 스케줄 있으면 true 없으면 false
    var showEditSheet by remember { mutableStateOf(false) } // 수정 창 on / off
    val textStyle = androidx.compose.ui.text.TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    val columnModifier = Modifier
        .padding(
            horizontal = 8.dp,
            vertical = 8.dp
        )
        .fillMaxHeight()

    /**
     * 스케줄 수정하였을 때 callback 하는 함수
     */
    val modifySchedule : (ScheduleData) -> Unit = { updatedScheduleData ->
        /**
         * 원래 경로 안내가 등록 되어 있었을 경우
         */
        if(updatedScheduleData.routeId != null && updatedScheduleData.routeId != 0){
            // 삭제한 경우
            if(updatedScheduleData.scheduledLocation.name.isEmpty()){
                routeManager.deleteRoute(updatedScheduleData.routeId!!)
                updatedScheduleData.routeId = null
            }
            // 갱신된 경우
            else if(scheduleData.scheduledLocation.name != updatedScheduleData.scheduledLocation.name) {
                routeManager.updateRoute(
                    routeId = updatedScheduleData.routeId!!,
                    Route(
                        cron = "",
                        location = Location(
                            name = updatedScheduleData.scheduledLocation.name,
                            latitude = updatedScheduleData.scheduledLocation.lat,
                            longitude = updatedScheduleData.scheduledLocation.lon
                        )
                    )
                )
            }
        }
        /**
         * 원래 경로가 없었을 경우
         */
        else {
            // 위치가 새로 등록된 경우
            if(updatedScheduleData.scheduledLocation.name.isNotEmpty()){
                val routeData : RouteData? = routeManager.insertRoute(scheduleData = updatedScheduleData)
                routeData?.let { updatedScheduleData.routeId = it.routeId }
            }
        }

        /**
         * 수정된 스케줄 내용도 업데이트
         */
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val start = dateFormat.format(Date(updatedScheduleData.startDateTime.value))
        val end = dateFormat.format(Date(updatedScheduleData.endDateTime.value))
        scheduleManager.updateSchedule(
            Schedules(
                userId = StaticValue.userInfo.userId!!,
                routeId = updatedScheduleData.routeId,
                scheduleId = updatedScheduleData.scheduleId,
                guideDatetime = if(updatedScheduleData.scheduledLocation.isGuide) updatedScheduleData.scheduledLocation.guideDatetime.toString()
                    else null,
                address = updatedScheduleData.scheduledLocation.address,
                description = updatedScheduleData.comment.value,
                isWholeday = updatedScheduleData.isWholeDay.value,
                title = updatedScheduleData.title.value,
                dateBegin = start,
                dateEnd = end,
            )
        )
        // null 이면 현재 날짜로 스케줄 다시 가져옴
        updateScheduleData(null)
    }

    // 삭제 다이얼로그
    if(scheduleDeleteDialog.value){
        ScheduleDeleteDialog(openDialogCustom = scheduleDeleteDialog){ onDeleteClick ->
            when(onDeleteClick) {
                true -> {
                    state = false
                    onDelete(scheduleData)
                }
                false -> { }
            }
        }
    }

    // 해당 날짜에 스케줄이 있을 때
    if (state){
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
                .clip(RoundedCornerShape(8.dp))
                .background(Color(234, 232, 239))
                .fillMaxWidth()
                .height(70.dp)
                .pointerInput(Unit) {
                    /**
                     * 길게 누르면 삭제 모달 true
                     * 짧게 누르면 EditSheet 보이게
                     */
                    detectTapGestures(
                        onLongPress = {
                            scheduleDeleteDialog.value = true
                        },
                        onTap = {
                            showEditSheet = true
                        }
                    )
                }
        ){
            /**
             * 시간이 출력되는 부분
             * scheduleData.isWholeDay == true 면 하루종일 이라는 문자열 출력
             */
            Column(
                modifier = columnModifier.width(70.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(scheduleData.isWholeDay.value){
                    true -> Text(
                            text = "하루종일",
                            fontWeight = FontWeight.Bold
                        )
                    false -> {
                        val simpleDateFormat = SimpleDateFormat("HH:mm")
                        val startTime = simpleDateFormat
                            .format(scheduleData.startDateTime.longValue)
                        val endTime = simpleDateFormat
                            .format(scheduleData.endDateTime.longValue)
                        Text(
                            text = startTime,
                            style = textStyle
                        )
                        Text(text = "~" + endTime)
                    }
                }
            }
            Row(modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .width(4.dp)
                .fillMaxHeight(0.65f)
                .background(Color(41, 161, 255))
                .align(Alignment.CenterVertically)){}
            Column(
                modifier = columnModifier.padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row{
                    Text(
                        text = scheduleData.title.value,
                        style = textStyle
                    )
                }
                Row{
                    Text(text = scheduleData.scheduledLocation.name)
                }
            }
        }
        if (showEditSheet){
            /**
             * item 내용을 원본 객체에 대입
             */
            val closeBottomSheet : (ScheduleData, Boolean) -> Unit = { item, flag ->
                if (flag){
                    scheduleData.title.value = item.title.value
                    scheduleData.scheduledLocation.name = item.scheduledLocation.name
                    scheduleData.scheduledLocation.lat = item.scheduledLocation.lat
                    scheduleData.scheduledLocation.lon = item.scheduledLocation.lon
                    scheduleData.scheduledLocation.address = item.scheduledLocation.address
                    scheduleData.scheduledLocation.isGuide = item.scheduledLocation.isGuide
                    scheduleData.scheduledLocation.guideDatetime = item.scheduledLocation.guideDatetime
                    scheduleData.isWholeDay.value = item.isWholeDay.value
                    scheduleData.startDateTime.longValue = item.startDateTime.longValue
                    scheduleData.endDateTime.longValue = item.endDateTime.longValue
                    scheduleData.comment.value = item.comment.value
                    scheduleData.routeId = item.routeId
//                    scheduleData.repeat.value.repeatable = item.repeat.value.repeatable
//                    scheduleData.repeat.value.cycle = item.repeat.value.cycle
                    // TODO: 일정 수정 API 호출
                    modifySchedule(scheduleData)
                }
                showEditSheet = false
            }
            /**
             * 객체를 전달하면 취소를 눌러도 값이 수정되기에 Deep Copy하여 전달
             */
            ScheduleBottomSheet(
                closeBottomSheet = closeBottomSheet,
                scheduleData = cloneScheduleData(scheduleData = scheduleData),
                scheduleModalSheetOption = ScheduleModalSheetOption.EDIT
            )
        }
    }
}

/**
 * DeepCopy
 */
@Composable
fun cloneScheduleData(scheduleData: ScheduleData): ScheduleData {
    return ScheduleData(
        title = remember { mutableStateOf(scheduleData.title.value) },
        scheduledLocation = remember {
            ScheduleLocation(
                scheduleData.scheduledLocation.name,
                scheduleData.scheduledLocation.address,
                scheduleData.scheduledLocation.lat,
                scheduleData.scheduledLocation.lon,
                scheduleData.scheduledLocation.isGuide,
                scheduleData.scheduledLocation.guideDatetime
            )
        },
        isWholeDay = remember { mutableStateOf(scheduleData.isWholeDay.value) },
        isDateValidate = remember { mutableStateOf(true )},
        startDateTime = remember { mutableLongStateOf(scheduleData.startDateTime.longValue) },
        endDateTime = remember { mutableLongStateOf(scheduleData.endDateTime.longValue) },
        comment = remember { mutableStateOf(scheduleData.comment.value) },
        routeId = scheduleData.routeId,
        scheduleId = -1
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreView1() {
}