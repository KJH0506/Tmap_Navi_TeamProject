package com.hansung.sherpa.navigation

import android.util.Log
import androidx.core.content.ContextCompat
import com.hansung.sherpa.BuildConfig
import com.hansung.sherpa.MainActivity
import com.hansung.sherpa.R
import com.hansung.sherpa.convert.Convert
import com.hansung.sherpa.convert.LegRoute
import com.hansung.sherpa.convert.PathType
import com.hansung.sherpa.deviation.RouteControl
import com.hansung.sherpa.itemsetting.RouteDetailMapper
import com.hansung.sherpa.itemsetting.RouteFilterMapper
import com.hansung.sherpa.itemsetting.TransportRoute
import com.hansung.sherpa.transit.ODsayTransitRouteRequest
import com.hansung.sherpa.transit.PedestrianRouteRequest
import com.hansung.sherpa.transit.TmapTransitRouteRequest
import com.hansung.sherpa.transit.TransitManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay

class Navigation {
    private var startLatLng: LatLng = LatLng(0.0, 0.0)
    var endLatLng: LatLng = LatLng(0.0, 0.0)
    private var routeRequest: TmapTransitRouteRequest = setRouteRequest(startLatLng, endLatLng)
    private var pathOverlayList:MutableList<PathOverlay> = mutableListOf()
    lateinit var naverMap: NaverMap
    lateinit var mainActivity: MainActivity
    lateinit var routeControl: RouteControl

    // 반드시 지울 것!! 좌표 찾기 대신 넣는 임시 값
    // [개발]: 시작, 도착 좌표
    var tempStartLatLng = LatLng(37.639853,126.832716)
    var tempEndLatLng = LatLng(37.587777, 127.030373)

    // 재호 경로
    //private val tempStartLatLng = LatLng(37.6417, 126.8364)
    //val tempEndLatLng = LatLng(37.6274, 126.829613)
    // 반드시 지울 것!!

    // 경로 탐색(경로만 탐색)
    fun getTransitRoutes(start: String, end: String): MutableList<MutableList<LegRoute>> {
        // 검색어 기반 좌표 검색
        /**
         * 미완성이라 주석처리
         * val SL = SearchLocation()
         * startLatLng = SL.searchLatLng(start)
         * endLatLng = SL.searchLatLng(end)
         **/

        // 좌표 기반 경로 검색
        routeRequest = setRouteRequest(tempStartLatLng, tempEndLatLng)
        val transitRouteResponse = TransitManager(mainActivity).getTmapTransitRoutes(routeRequest)
        TransitManager(mainActivity).getODsayTransitRoute(Convert().convertTmapToODsayRequest(routeRequest))
        val transitRoutes = Convert().convertToRouteMutableLists(transitRouteResponse)

        return transitRoutes
    }

    // 이전 경로 탐색 코드
    fun getTransitRoutesBefore(start: String, end: String){
        // 검색어 기반 좌표 검색
        /**
         * 미완성이라 주석처리
         * val SL = SearchLocation()
         * startLatLng = SL.searchLatLng(start)
         * endLatLng = SL.searchLatLng(end)
        **/
        
        // 좌표 기반 경로 검색
        routeRequest = setRouteRequest(tempStartLatLng, tempEndLatLng)
        val transitRouteResponse = TransitManager(mainActivity).getTmapTransitRoutes(routeRequest)
        val transitRoutes = Convert().convertToRouteMutableLists(transitRouteResponse)
        val transitRoute = transitRoutes[0]
        
        // 경로 그리기
        drawRoute(transitRoute)

        // 기타
        routeControl.route = Convert().convertLegRouteToLatLng(transitRoute)
        routeControl.initializeRoute()
    }

    // 경로 요청 값 만들기
    private fun setRouteRequest(startLatLng: LatLng, endLatLng: LatLng):TmapTransitRouteRequest {
        return TmapTransitRouteRequest(
            startX = startLatLng.longitude.toString(),
            startY = startLatLng.latitude.toString(),
            endX = endLatLng.longitude.toString(),
            endY = endLatLng.latitude.toString(),
            lang = 0,
            format = "json",
            count = 10
        )
    }

    private fun setODsayRouteRequest(startLatLng: LatLng, endLatLng: LatLng): ODsayTransitRouteRequest {
        return ODsayTransitRouteRequest(
            apiKey = BuildConfig.ODSAY_APP_KEY,
            SX = startLatLng.longitude.toString(),
            SY = startLatLng.latitude.toString(),
            EX = endLatLng.longitude.toString(),
            EY = endLatLng.latitude.toString()
        )
    }

    // 경로를 지우는 함수
    private fun clearRoute() {
        pathOverlayList.forEach { it.map = null }
        pathOverlayList = mutableListOf()
    }

    private fun clearRoute(to:LatLng){
        var pathOverlayIndex = 0
        var coordsIndex = 0

        outer@ for(i in pathOverlayList){
            coordsIndex=0
            for(j in i.coords){
                if(j.equals(to)){
                    Log.d("index", ""+pathOverlayIndex + ", " + coordsIndex)
                    Log.d("index", ""+to.toString() + "    " + j.toString())
                    break@outer
                }
                coordsIndex+=1
            }
            pathOverlayIndex+=1
        }

        if(pathOverlayIndex==0){
            Log.d("apple","구역1")
            pathOverlayList[pathOverlayIndex].map=null
            for(i in 0..coordsIndex){
                pathOverlayList[pathOverlayIndex].coords.removeAt(0)
            }
            pathOverlayList[pathOverlayIndex].map = naverMap
        }
        else{
            Log.d("apple","구역2")
            for(i in 0 until  pathOverlayIndex){
                pathOverlayList[i].map=null
                pathOverlayList.removeAt(0)
            }

            pathOverlayList[0].map = naverMap

            for(i in 0..coordsIndex){
                pathOverlayList[pathOverlayIndex].coords.removeAt(0)
            }
            pathOverlayList[0].map = naverMap
        }
    }

    // 재탐색 후 경로를 그리는 함수
    fun redrawRoute(location:LatLng, endLatLng: LatLng) {
        // 기존 경로 지도에서 지우기
        clearRoute(endLatLng)

        // 요청 좌표 기반 경로 검색
        val transitRouteResponse = TransitManager(mainActivity).getTmapTransitRoutes(routeRequest)

        val transitRoutes = Convert().convertToRouteMutableLists(transitRouteResponse)

        val transitRoute = transitRoutes[0]
        val pedestrianRouteRequest = PedestrianRouteRequest(
            startX = location.longitude.toFloat(),
            startY = location.latitude.toFloat(),
            endX = endLatLng.longitude.toFloat(),
            endY = endLatLng.latitude.toFloat(),
            passList = ""+location.longitude+","+location.latitude+"_"+endLatLng.longitude + ","+ endLatLng.latitude
        )
        val pedestrianRouteResponse = TransitManager(mainActivity).getPedestrianRoute(pedestrianRouteRequest)
        val pedestrianRoute = Convert().convertPedestrianRouteToLatLng(pedestrianRouteResponse,location,endLatLng)

        // 경로 그리기
        drawPedestrianRoute(pedestrianRoute)

        routeControl.addPedestrianRoute(pedestrianRoute)
    }

    private fun drawPedestrianRoute(pedestrianRoute:MutableList<LatLng>){
        val pathOverlay = PathOverlay()
        pathOverlay.coords = pedestrianRoute.toList()
        pathOverlay.color = convertIntToStr(R.color.WALK)
        pathOverlay.width = 10
        pathOverlay.map = naverMap

        pathOverlayList.add(0, pathOverlay)
    }

    // 경로를 그리는 함수
    private fun drawRoute(transitRoute: MutableList<LegRoute>) {
        for (i in transitRoute){
            val pathOverlay = PathOverlay().also {
                it.coords = Convert().convertCoordinateToLatLng(i.coordinates)
                it.width = 10
                when(i.pathType){
                    PathType.WALK -> it.color = convertIntToStr(R.color.WALK)
                    PathType.BUS -> it.color = convertIntToStr(R.color.BUS)
                    PathType.EXPRESSBUS -> it.color = convertIntToStr(R.color.EXPRESSBUS)
                    PathType.SUBWAY -> it.color = convertIntToStr(R.color.SUBWAY)
                    PathType.TRAIN -> it.color = convertIntToStr(R.color.TRAIN)
                }
                it.map = naverMap
            }
            pathOverlayList.add(pathOverlay)
        }
    }
    
    // 색상 값 변환 HEXACODE(#ffffff) -> INT(@ColorInt)
    fun convertIntToStr(color:Int) : Int {
        return ContextCompat.getColor(mainActivity, color)
    }


    /**
     * API 사용법 참고용
     * API 요청과 매핑 방법에 대해서 전체 흐름을 보여준다.
     *
     * LogCat Tag
     * - API
     * - MAPPER
     */
    fun getDetailTransitRoutes(start: String, end: String) {
        val isMapStruct: Boolean = false // MapStruct 라이브러리 사용여부
        val TM = TransitManager(mainActivity)

        // [API] 대중교통+도보 길찾기
        val routeRequest =  setODsayRouteRequest(tempStartLatLng, tempEndLatLng)
        val ODsayTransitRouteResponse = TM.getODsayTransitRoute(routeRequest)
        Log.i("API", ODsayTransitRouteResponse.toString())

        // [API] 노선 그래픽 : 대중교통 경로 좌표 찾기
        val routeGraphicList = TM.requestCoordinateForMapObject(ODsayTransitRouteResponse!!)
        Log.i("API", routeGraphicList.toString())

        // [MAPPING] MapStruct 라이브러리 TODO 사용금지!
        if (isMapStruct) {
            try {
                val oDsayPath = ODsayTransitRouteResponse.result!!.path[0]
                val graphPosList = routeGraphicList[0].result!!.lane!![0]!!.section?.get(0)!!.graphPos!!
                val transportRoute = RouteDetailMapper.INSTANCE.convertToTransit(oDsayPath, graphPosList)
                Log.i("MAPPER", transportRoute.toString())
            } catch (e: Exception) {
                Log.e("MAPPER", e.toString())
            }
        }

        // [MAPPING] 하드코딩 매핑 TransportRouteList
        var transportRouteList: List<TransportRoute>? = emptyList()
        if (!isMapStruct) {
            try {
                transportRouteList = RouteFilterMapper().mappingODsayResponseToTransportRouteList(ODsayTransitRouteResponse, routeGraphicList)
                Log.i("MAPPER", transportRouteList.toString())
            } catch (e: Exception) {
                Log.e("MAPPER", e.toString())
            }
        }

        // [API] 고른 경로에 대한 보행자 경로 리턴
        val selectedIndex = 0 // 사용자가 1개의 경로를 고름
        val pedestrianRouteList = TM.requestCoordinateForRoute(
            tempStartLatLng, tempEndLatLng, ODsayTransitRouteResponse.result?.path?.get(selectedIndex)
        )
        Log.i("API", pedestrianRouteList.toString())

        // [MAPPING] 선택한 경로에 대한 데이터를 사용할 클래스 객체에 넣어준다.
        if (!isMapStruct) {
            try {
                val transportRoute = RouteFilterMapper().mappingPedestrianRouteToTransportRoute(transportRouteList!![selectedIndex], pedestrianRouteList)
                Log.i("MAPPER", transportRoute.toString())
            } catch (e : Exception) {
                Log.e("MAPPER", e.toString())
            }
        }
    }

}

