package com.hansung.sherpa.ui.preference.calendar

//import org.threeten.bp.DayOfWeek
//import org.threeten.bp.LocalDate
//import org.threeten.bp.YearMonth
//import org.threeten.bp.format.DateTimeFormatter
//import org.threeten.bp.format.TextStyle
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hansung.sherpa.schedule.RouteManager
import com.hansung.sherpa.schedule.ScheduleManager
import com.hansung.sherpa.schedule.Schedules
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale


/**
 * @author 6-keem
 *
 * 사용자 일정 관리를 위한 캘린더 액티비티
 */


/**
 * @param finish : 뒤로가기 클릭 시 callback되는 람다 함수
 * 캘린더 화면을 구성하는 Composable
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController
){
    var scheduleDataList = remember { mutableStateListOf<ScheduleData>() }
    var showBottomSheet by remember { mutableStateOf(false) }

    val scheduleManager = ScheduleManager()
    val routeManager = RouteManager()

    val currentSelectedDate = remember { mutableStateOf<LocalDateTime>(LocalDateTime.of(1970,12,12,0,0,0)) }

    /**
     * 일정 추가,수정,삭제 시 호출하여 스케줄 현재 scheduleDataList를 관리하는 람다 함수
     *
     * 1. 선택된 날짜에 해당하는 스케줄 목록(scheduleResponse)가 매개변수로 전달됨
     * 2. 리스트의 모든 값을 삭제하고 각 스케줄에 대해 예약 경로가 있는지 확인함
     * 3. 스케줄과 예약 경로를 합쳐 scheduleList에 추가함
     */
    val updateScheduleList : (List<Schedules>?) -> Unit = { scheduleResponse ->
        scheduleDataList.clear()
        scheduleResponse?.forEach {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val startTime = sdf.parse(it.dateBegin)
            val endTime = sdf.parse(it.dateEnd)
            val route = it.routeId?.let { it1 -> routeManager.findRoute(it1) }

            scheduleDataList.add(
                ScheduleData(
                    title =  mutableStateOf(it.title) ,
                    comment = mutableStateOf(it.description) ,
                    startDateTime = mutableLongStateOf(startTime.time) ,
                    endDateTime =  mutableLongStateOf(endTime.time) ,
                    isDateValidate = mutableStateOf(true) ,
                    isWholeDay = mutableStateOf(it.isWholeday),
                    scheduledLocation = if(route != null){
                        ScheduleLocation(
                            name = route.location.name,
                            lat = route.location.latitude,
                            lon = route.location.longitude,
                            address = it.address,
                            isGuide = !it.guideDatetime.isNullOrEmpty(),
                            guideDatetime = if(!it.guideDatetime.isNullOrEmpty()) it.guideDatetime.toLong() else 0L
                        )
                    } else {
                        ScheduleLocation(
                            name = "",
                            lat = 0.0,
                            lon = 0.0,
                            address = "",
                            isGuide = false,
                            guideDatetime = 0
                        )
                    },
                    routeId = route?.routeId,
                    scheduleId = it.scheduleId!!
                )
            )
        }
    }

    /**
     * 날짜가 변경되면 호출되는 람다 함수
     *
     * 1. changeDate로 null or 변경된 날짜가 전달 됨
     * 2. 변경된 날짜에 대해 서버에 저장된 스케줄을 가져옴
     *      if changeDate == null -> 현재 날짜
     * 3. updateScheduleList 함수를 호출하여 scheduleList를 최신화함
     */
    val updateScheduleData : (LocalDate?) -> Unit = { changedDate ->
        val changeDate = changedDate ?: currentSelectedDate.value.toLocalDate()
        val localDatetime = changeDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val scheduleResponse = scheduleManager.findSchedules(SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(localDatetime)))?.data
        currentSelectedDate.value = changeDate.atStartOfDay()
        updateScheduleList(scheduleResponse)
    }

    // TODO: 시트 닫았을 때 추가 버튼을 통한 닫힘이면 서버에 일정 추가
    /**
     * 시트가 닫힐 때 callback 되는 람다 함수
     *
     * @param item : ScheduleData
     * @param isAdded : Boolean -> 추가 버튼을 눌렀는지
     *
     * if scheduleData 내 위치 설정이 되어 있으면
     *      Server DB Route table에 추가
     * 스케줄을 서버에 추가
     *
     */
    val closeBottomSheet : (ScheduleData, Boolean) -> Unit = { item, isAdded ->
        if(isAdded){
            var routeId : Int? = null
            if(item.scheduledLocation.name.isNotEmpty()) {
                routeId = routeManager.insertRoute(scheduleData = item)?.routeId
            }
            scheduleManager.insertSchedules(scheduleData = item, routeId = routeId)

            currentSelectedDate.value = LocalDateTime.of(currentSelectedDate.value.year,
                currentSelectedDate.value.monthValue, currentSelectedDate.value.dayOfMonth,
                currentSelectedDate.value.hour, currentSelectedDate.value.minute)
        }
        showBottomSheet = false
    }

    Scaffold (
        topBar = {
            // 중앙 정렬 Topbar
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    titleContentColor = Color.DarkGray
                ),

                title = {
                    Text(
                        text = "캘린더",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)
                    )
                },
                // 뒤로가기 버튼 클릭 시 finish 람다 함수 호출
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기")
                    }
                },
            )
        },
        /**
         *  하단에 고정되는 일정 추가 버튼
         *  추가 버튼 클릭 시 showBottomSheet = true
         */
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(shape = RoundedCornerShape(40.dp))
                    .width(80.dp)
                    .height(40.dp),
                containerColor = Color.Black,
                contentColor = Color.White,
            ) {
                Text(text = "추가하기")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerpadding ->
            Surface(modifier = Modifier.padding(innerpadding)) {
                Calendar(scheduleDataList = scheduleDataList, updateScheduleData = updateScheduleData)
            }
        }
    )

    /**
     *  일정 추가 버튼을 누르면 껍데기 scheduleDate 객체 생성
     *  생성 후 ModalBottomSheet 객체에 넣어준다.
     */
    if (showBottomSheet){
        val mills = currentSelectedDate.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startDateTime = android.icu.util.Calendar.getInstance().apply {
            timeInMillis = mills
            set(android.icu.util.Calendar.MINUTE, 0)
        }
        val endDateTime = android.icu.util.Calendar.getInstance().apply {
            timeInMillis = mills
            set(android.icu.util.Calendar.MINUTE, 0)
        }
        val scheduleData = ScheduleData(
            title = remember { mutableStateOf("") },
            scheduledLocation = remember {
                ScheduleLocation(
                    "",
                    "",
                    0.0,
                    0.0,
                    false,
                    guideDatetime = startDateTime.timeInMillis
                )
            },
            isWholeDay = remember { mutableStateOf(false) },
            isDateValidate = remember { mutableStateOf(true )},
            startDateTime = remember { mutableLongStateOf(startDateTime.timeInMillis) },
            endDateTime = remember { mutableLongStateOf(endDateTime.timeInMillis) },
            comment = remember { mutableStateOf("") },
            routeId = null,
            scheduleId = -1
        )
        ScheduleBottomSheet(
            closeBottomSheet = closeBottomSheet,
            scheduleData = scheduleData,
            scheduleModalSheetOption = ScheduleModalSheetOption.ADD
        )
    }
}

/**
 * CalendarScreen의 Calendar 출력하는 부분
 * @param scheduleDataList : 선택된 날짜의 스케줄이 저장되어 있는 리스트
 * @param config : 캘린더 기본 설정 정보 객체
 * @param currentDate : 선택된 현재 날짜
 * @param updateScheduleData : 날짜가 바뀌면 호출되는 callback
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    scheduleDataList : SnapshotStateList<ScheduleData>,
    config: CalendarConfig = CalendarConfig(),
    currentDate: LocalDate = LocalDate.now(),
    updateScheduleData: (LocalDate?) -> Unit
) {
    val initialPage = (currentDate.year - config.yearRange.first) * 12 + currentDate.monthValue - 1
    var currentSelectedDate by remember { mutableStateOf(currentDate) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var currentPage by remember { mutableIntStateOf(initialPage) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { (config.yearRange.last - config.yearRange.first + 1) * 12 }
    )
    val coroutineScope = rememberCoroutineScope()
    updateScheduleData(currentSelectedDate)

    /**
     * 월이 바뀌면 호출되는 함수
     * @param offset : 월을 얼마나 변경할 지
     */
    val onChangeMonth : @Composable (Int) -> Unit = { offset ->
        with(pagerState) {
            LaunchedEffect(key1 = currentPage) {
                coroutineScope.launch {
                    animateScrollToPage(
                        page = (currentPage + offset).mod(pageCount)
                    )
                }
                // 각 변수 초기화
                currentMonth = currentMonth.plusMonths(offset.toLong())
                currentPage = pagerState.currentPage
                val yearMonth = YearMonth.now()
                currentSelectedDate = when ((currentMonth.month == yearMonth.month && currentMonth.year == yearMonth.year)){
                    true -> LocalDate.now()
                    false -> LocalDate.of(currentMonth.year, currentMonth.month, 1)
                }
            }
        }
    }

    /**
     * 실제 달력이 출력되는 부분
     */
    LazyColumn(modifier = Modifier) {
        val headerText = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
        // < yyyy.MM > 출력되는 캘린더 header 부분
        item{
            CalendarHeader(
                onChangeMonth = onChangeMonth,
                text = headerText,
            )
        }
        // 캘린더 몸체 부분
        item {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp)
                    .wrapContentSize()
                    .padding(bottom = 18.dp)
            ) { page ->
                val date = LocalDate.of(
                    config.yearRange.first + page / 12,
                    (page % 12) + 1,
                    1
                )
                CalendarMonthItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    currentDate = date,
                    selectedDate = currentSelectedDate,
                    onSelectedDate = { date ->
                        currentSelectedDate = date
                    }
                )
            }
        }
        item{
            /**
             *  현재 선택된 날짜 문자열
             *  선택된 날짜의 스케줄 출력 부분
             */
            CurrentDateColumn(currentSelectedDate)
            ScheduleColumns(
                scheduleDataList = scheduleDataList,
                updateScheduleData = updateScheduleData
            )
        }
    }
}

/**
 * 캘린더 내 일 출력 부분
 * @param modifier : Optional<
 * @param currentDate 현재 날짜
 * @param selectedDate 선택된 날짜
 * @param onSelectedDate 날짜 변경 시 callback 함수
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarMonthItem(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onSelectedDate: (LocalDate) -> Unit
) {
    val lastDay = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.withDayOfMonth(1).dayOfWeek.value % 7
    val days = IntRange(1, lastDay).toList()

    Column(modifier = modifier) {
        DayOfWeek()
        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ) {
            for (i in 0 until firstDayOfWeek) {
                item {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(top = 10.dp)
                    )
                }
            }
            items(days) { day ->
                val date = currentDate.withDayOfMonth(day)
                val isSelected = remember(selectedDate) {
                    selectedDate.compareTo(date) == 0
                }

                // 각 날짜 1일, 2일, 3일...
                CalendarDay(
                    modifier = Modifier.padding(top = 10.dp),
                    date = date,
                    isToday = date == LocalDate.now(),
                    isSelected = isSelected,
                    onSelectedDate = onSelectedDate
                )
            }
        }
    }
}

/**
 * 각 날짜 출력되는 Composable
 * @param modifier : Optional
 * @param date 날짜
 * @param isToday 현재 날짜인지
 * @param isSelected 선택된 날짜인지
 * @param hasEvent 등록된 이벤트가 있을 경우
 * @param onSelectedDate 날짜가 선택될 경우 callback 함수
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvent: Boolean = false,
    onSelectedDate: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .size(30.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(
                color = when {
                    isSelected -> Color.Black
                    isToday -> Color.Gray
                    else -> Color(234, 232, 239)
                }
            )
            .indication(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(4.dp)
            .clickable { onSelectedDate(date) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val textColor = if (isSelected || isToday) Color.White else
            when(date.dayOfWeek){
                DayOfWeek.SUNDAY -> Color.Red
                DayOfWeek.SATURDAY -> Color.Blue
                else -> Color.Black
            }

        Text(
            modifier = Modifier,
            textAlign = TextAlign.Center,
            text = date.dayOfMonth.toString(),
            color = textColor
        )
        if (hasEvent) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .let {
                        if (isSelected || isToday)
                            it.background(Color.White)
                        else
                            it.background(Color.Black)
                    }
            )
        }
    }
}


/**
 * 캘린더 위에 나타나는 일 월 화 수 목 금 토 출력하는 Composable
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayOfWeek(
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        val daysOfWeek = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = when(dayOfWeek){
                    DayOfWeek.SUNDAY -> Color.Red
                    DayOfWeek.SATURDAY -> Color.Blue
                    else -> Color.Black
                },
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 캘린더 상단 < yyyy.MM > 출력되는 Comopsable
 *
 * @param onChangeMonth 버튼 클릭 시 callback 함수
 * @param text 상단에 표시 할 text
 */
@Composable
fun CalendarHeader(
    onChangeMonth : @Composable (Int) -> Unit,
    text: String,
) {
    var isLeftArrowClick by remember { mutableStateOf(false) }
    var isRightArrowClick by remember { mutableStateOf(false) }

    if(isLeftArrowClick){
        isLeftArrowClick = false
        onChangeMonth(-1)
    }
    if(isRightArrowClick){
        isRightArrowClick = false
        onChangeMonth(1)
    }

    Row(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        IconButton(
            onClick = { isLeftArrowClick = true },
            modifier = Modifier.align(Alignment.CenterVertically)) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "이전달")
        }
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = { isRightArrowClick = true },
            modifier = Modifier.align(Alignment.CenterVertically)) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "이전달")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCalendarPreference() {
    CalendarScreen(navController = NavController(LocalContext.current))
}