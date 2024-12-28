package com.hansung.sherpa

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import com.hansung.sherpa.fcm.MessageViewModel
import com.hansung.sherpa.fcm.PermissionDialog
import com.hansung.sherpa.fcm.RationaleDialog
import com.hansung.sherpa.fcm.ScheduleViewModel
import com.hansung.sherpa.itemsetting.SubPath
import com.hansung.sherpa.itemsetting.SubPathAdapter
import com.hansung.sherpa.itemsetting.TransportRoute
import com.hansung.sherpa.navigation.Navigation
import com.hansung.sherpa.sendInfo.CaregiverViewModel
import com.hansung.sherpa.sendInfo.CaretakerViewModel
import com.hansung.sherpa.sendInfo.PartnerViewModel
import com.hansung.sherpa.sendInfo.receive.isCareGiver
import com.hansung.sherpa.sendInfo.receive.onChangedRTDBListener
import com.hansung.sherpa.ui.account.login.LoginScreen
import com.hansung.sherpa.ui.account.signup.SignupScreen
import com.hansung.sherpa.ui.common.MessageAlam
import com.hansung.sherpa.ui.common.PermissionAlam
import com.hansung.sherpa.ui.common.ScheduleAlam
import com.hansung.sherpa.ui.preference.PreferenceComposable
import com.hansung.sherpa.ui.preference.calendar.CalendarScreen
import com.hansung.sherpa.ui.searchscreen.SearchScreen
import com.hansung.sherpa.ui.specificroute.SpecificRouteScreen
import com.hansung.sherpa.ui.start.StartScreen
import com.hansung.sherpa.ui.theme.SherpaTheme
import com.hansung.sherpa.user.UserManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : ComponentActivity() {

    private lateinit var naverMap: NaverMap

    private lateinit var locationSource: FusedLocationSource

    lateinit var navController: NavHostController

    // TODO: 여기 있는게 "알림" topic으로 FCM 전달 받는 뷰모델 ※ FCM pakage 참고
    private val messageViewModel: MessageViewModel by viewModels()
    private val message2ViewModel: MessageViewModel by viewModels()
    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private val partnerViewModel: PartnerViewModel by viewModels()
    private val caretakerViewModel: CaretakerViewModel by viewModels()
    private val caregiverViewModel: CaregiverViewModel by viewModels()


    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val head = intent?.getStringExtra("title") ?: ""
            val body = intent?.getStringExtra("body") ?: ""

            val parts = head.split("/")
            val topic = parts[0]
            val title = parts[1]

            when (topic) {
                "알림" -> messageViewModel.updateValue(title, body)
                "일정" -> scheduleViewModel.updateSchedule(body)
                "예약경로" -> {
                    // TODO: 출 -> 목 경로 탐색
                    /*
                        1. 첫 번째 인덱스 경로 가져옴
                        2. StaticValue 경로 값에 저장
                        3. 경로 안내 팝업 띄우기
                        4. Navigation specific route로 옮기기
                    */
                    val tokens = body.split(",")
                    val latLng = LatLng(tokens[0].toDouble(), tokens[1].toDouble())
                    if(StaticValue.myPos != null){
                        val transportRoutes = Navigation().getDetailTransitRoutes(
                            LatLng(StaticValue.myPos!!.latitude, StaticValue.myPos!!.longitude),
                            latLng,
                            "", "")
                        transportRoutes[0]

                        Toast.makeText(context,"경로 안내를 시작합니다.", Toast.LENGTH_LONG)
                        navController.navigate(SherpaScreen.SpecificRoute.name)
                    }
                }
                "재탐색" -> {
                    Log.d("FCM LOG", "재탐색")
                    caregiverViewModel.devateRoute()
                }
                "시작" -> {
                    Log.d("FCM LOG", "시작 전송")
                    val transportRoute = caregiverViewModel.startNavigation()
                    if(transportRoute != null) navController.navigate("${SherpaScreen.SpecificRoute.name}/$transportRoute")
                }
                "요청" -> message2ViewModel.updateValue(title, body)
                else -> Log.e("FCM Log: Error", "FCM: message 형식 오류")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FCM SDK 초기화
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.w("FCMLog", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCMLog", "Current token: $token")

                StaticValue.FcmToken = token
            }

        // BroadcastReceiver 등록
        registerReceiver(messageReceiver, IntentFilter("FCM_MESSAGE"), RECEIVER_NOT_EXPORTED)

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.CLIENT_ID)

        val database = Firebase
            .database(BuildConfig.FIREBASE_RTDB_URL)
            .reference
        StaticValue.ref = database

        StaticValue.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                RequestNotificationPermissionDialog()
            }
            SherpaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // TODO: 임시로 설정해둠

                    // 화면 간 이동에 대한 함수
                    // https://developer.android.com/codelabs/basic-android-kotlin-compose-navigation?hl=ko#0
                    navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = SherpaScreen.Start.name
                    ){
                        composable(route = SherpaScreen.Start.name){
                            StartScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = SherpaScreen.Login.name){
                            LoginScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = SherpaScreen.SignUp.name) {
                            SignupScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = SherpaScreen.Home.name){
                            val userInfo = StaticValue.userInfo
                            val relation = UserManager().getRelation(userInfo.userId!!).data
                            if(isCareGiver()) {
                                val caregiverId = relation?.caretakerId.toString()
                                onChangedRTDBListener(caregiverId, partnerViewModel, caregiverViewModel)
                            }
                            else {
                                val caretakerId = relation?.caregiverId.toString()
                                onChangedRTDBListener(caretakerId, partnerViewModel, caregiverViewModel)
                            }
                            MessageAlam(messageViewModel)
                            ScheduleAlam(scheduleViewModel)
                            PermissionAlam(message2ViewModel)
                            HomeScreen(navController, Modifier.padding(innerPadding), partnerViewModel)
                        }
                        composable(route = "${SherpaScreen.Search.name}/{destinationValue}",
                            arguments = listOf(navArgument("destinationValue"){type = NavType.StringType})
                        ){
                            val destinationValue = it.arguments?.getString("destinationValue")!!
                            SearchScreen(navController, destinationValue, Modifier.padding(innerPadding))
                        }
                        composable(route = "${SherpaScreen.SpecificRoute.name}/{transportRouteEncodingJson}",
                            arguments = listOf(navArgument("transportRouteEncodingJson"){type = NavType.StringType})
                        ){
                            val transportRouteEncodingJson = it.arguments?.getString("transportRouteEncodingJson")
                            val transportRoute = fromTransportRouteJson(transportRouteEncodingJson!!)
                            SpecificRouteScreen(
                                transportRoute,
                                partnerViewModel, caregiverViewModel,
                                { navController.navigate(SherpaScreen.Home.name) }
                            )
                        }
                        composable(route = SherpaScreen.Preference.name){
                            PreferenceComposable(navController = navController)
                        }
                        composable(route = SherpaScreen.CALENDAR.name){
                            CalendarScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestNotificationPermissionDialog() {
        val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        if (!permissionState.status.isGranted) {
            if (permissionState.status.shouldShowRationale) RationaleDialog()
            else PermissionDialog { permissionState.launchPermissionRequest() }
        }
    }

    // 권한 확인
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(messageReceiver)
    }

    fun fromTransportRouteJson(encodingJson:String): TransportRoute {
        // 디코딩 작업 (transportRoute는 디코딩되어 전달된다.)
        val json = encodingJson
            .substring(0, encodingJson.length)
            .chunked(2)
            .map { Integer.parseInt(it, 16).toByte() }
            .toByteArray()
            .toString(Charsets.UTF_8)

        // transportRoute는 interface로 제작되어 있어 추가적인 타입 변환 어댑터가 필요하다.
        val gson = GsonBuilder()
            .registerTypeAdapter(SubPath::class.java, SubPathAdapter())
            .create()

        // 정상화된 json 객체 파싱 진행
        Log.d("API Log", "반환: ${json}")
        return gson.fromJson(json, TransportRoute::class.java)
    }
}