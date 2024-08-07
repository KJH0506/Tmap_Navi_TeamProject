package com.hansung.sherpa

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hansung.sherpa.deviation.RouteControl
import com.hansung.sherpa.gps.GPSDatas
import com.hansung.sherpa.gps.GpsLocationSource
import com.hansung.sherpa.navigation.MyOnLocationChangeListener
import com.hansung.sherpa.navigation.Navigation
import com.hansung.sherpa.navigation.OnLocationChangeManager
import com.hansung.sherpa.ui.login.LoginScreen
import com.hansung.sherpa.ui.start.StartScreen
import com.hansung.sherpa.ui.search.SearchScreen
import com.hansung.sherpa.ui.specificroute.SpecificRouteScreen
import com.hansung.sherpa.ui.theme.SherpaTheme
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : ComponentActivity(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var locationSource: FusedLocationSource
    private lateinit var destinationTextView: EditText // 목적지 textview
    private lateinit var searchButton: ImageButton // 검색 버튼
    private val markerIcon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_location_overlay_icon)

    // 내비게이션 안내 값을 전송하기 위함
    lateinit var navigation:Navigation

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.CLIENT_ID)

        locationSource = GpsLocationSource.createInstance(this)

        setContent {
            SherpaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 화면 간 이동에 대한 함수
                    // https://developer.android.com/codelabs/basic-android-kotlin-compose-navigation?hl=ko#0
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = SherpaScreen.Start.name
                    ){
                        composable(route = "${SherpaScreen.Start.name}"){
                            StartScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = "${SherpaScreen.Login.name}"){
                            LoginScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = "${SherpaScreen.Home.name}"){
                            HomeScreen(navController, Modifier.padding(innerPadding))
                        }
                        composable(route = "${SherpaScreen.Search.name}/{destinationValue}",
                            arguments = listOf(navArgument("destinationValue"){type = NavType.StringType})
                        ){
                            val destinationValue = it.arguments?.getString("destinationValue")!!
                            SearchScreen(navController, destinationValue, Modifier.padding(innerPadding))
                        }
                        composable(route = "${SherpaScreen.SpecificRoute.name}"){
                            // KJH 세부 경로 화면
                            HomeScreen(navController, Modifier.padding(innerPadding))
                            SpecificRouteScreen(StaticValue.transportRoute)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onMapReady(p0: NaverMap) {
        this.naverMap = p0

        // LocationOverlay 설정
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = markerIcon
        locationOverlay.isVisible = true

        //좌측하단 Tracking Mode 변환 버튼
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationSource = locationSource

        // 움직이는 사용자 마커 따라 그리기
        onChangeUserMarker()

        // 검색 필요 클래스 초기화
        val routeControl = RouteControl() // 사용자 위치 확인
        val gpsData = GPSDatas(this) // gps 위치
        navigation = Navigation() // 경로 그리기 & 탐색
        navigation.naverMap = naverMap
        navigation.mainActivity = this
        navigation.routeControl = routeControl

        // ---------- 삭제할 것 ----------
        StaticValue.navigation = navigation
        // ---------- 여기까지 ----------

        // ----- 사용자 위치 변경시 경로 이탈 확인 로직 -----
        val i = object : MyOnLocationChangeListener {
            override fun callback(location: Location) {
                val nowLocation = LatLng(location.latitude, location.longitude)
                navigation.tempStartLatLng = nowLocation
                Log.d("nowsection",""+routeControl.nowSection)
                if (routeControl.route.isNotEmpty() && routeControl.detectOutRoute(nowLocation)) {// 경로이탈 탐지
                    var shortestRouteIndex = routeControl.findShortestIndex(nowLocation)
                    var toLatLng = routeControl.route[shortestRouteIndex]
                    routeControl.delRouteToIndex(shortestRouteIndex)
                    navigation.redrawRoute(nowLocation, toLatLng)
                }
            }
        }

        val OLCM = OnLocationChangeManager
        OLCM.naverMap = naverMap
        OLCM.addMyOnLocationChangeListener(i)
    }

    // 사용자 마커 표시
    private fun onChangeUserMarker() {
        val currMarker = Marker()
        naverMap.addOnLocationChangeListener { location ->
            currMarker.map = null
            setUserMarkerPosition(currMarker, LatLng(location.latitude, location.longitude))
        }
    }

    // 사용자 마커 위치 지정하여 재정의
    private fun setUserMarkerPosition(marker: Marker, latLng: LatLng) {
        marker.icon = markerIcon
        marker.position = latLng
        marker.anchor = PointF(0.5f, 0.5f)
        marker.map = naverMap
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

    // ---------- 수정예정 ----------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> { // RouteList Activity 문제점 좀 있음
                    val startKeyword = data?.getStringExtra("startKeyword")!!
                    val endKeyword = data.getStringExtra("endKeyword")!!
                    Log.d("explain", "$startKeyword is $endKeyword")
                    navigation.getTransitRoutes(startKeyword, endKeyword)
                }
            }
        }
    }
}

