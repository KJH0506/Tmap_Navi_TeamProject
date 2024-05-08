package com.hansung.sherpa.convert

import com.hansung.sherpa.transit.Leg
import com.hansung.sherpa.transit.TransitRouteResponse

/**
 * API로 받아온 데이터 객체를 사용할 객체로 변환해주는 클래스
 *
 * @since 2024-05-09
 * @author HS-JNYLee
 *
 * @property routeMutableLists 여러 경로의 데이터를 가지고 있는 프로퍼티
 * @property legRouteMutableList 1개의 경로의 데이터를 저장하는 프로퍼티
 */
class Convert {
    private val routeMutableLists = mutableListOf<MutableList<LegRoute>>()
    private val legRouteMutableList = mutableListOf<LegRoute>()

    /**
     * API reponse 값을 경로 리스트로 바꿔주는 함수
     * @param response API로 받아온 데이터 객체
     *
     * @return  MutableList<MutableList<Route>>
     */
    fun convertToRouteMutableLists(response: TransitRouteResponse): MutableList<MutableList<LegRoute>> {
        response.metaData?.plan?.itineraries?.forEach {
            legRouteMutableList.removeAll(legRouteMutableList) // 초기화
            it.legs?.forEach { leg -> navigateRouteType(leg) } // 구간별 값 추가
            routeMutableLists.add(legRouteMutableList) // 완성된 경로 저장
        }
        return routeMutableLists
    }

    /**
     * 각 구간별 이동수단 타입에 따라 구분해주는 함수
     * @param leg 구간의 정보
     */
    private fun navigateRouteType(leg: Leg) {
        when (leg.mode) {
            "WALK" -> legRouteMutableList.add(getWalkRoute(leg))
            "SUBWAY" -> legRouteMutableList.add(getSubwayRoute(leg))
            "BUS" -> legRouteMutableList.add(getBusRoute(leg))
            "EXPRESSBUS" -> legRouteMutableList.add(getExpressBusRoute(leg))
            "TRAIN" -> legRouteMutableList.add(getTrainRoute(leg))
        }
    }

    /**
     * 문자열 좌표 값을 좌표 객체로 바꾸는 함수
     * @param lineString 해당 구간의 경로 좌표 값
     *
     * @return MutableList
     */
    private fun convertToCoordsList(lineString: String): MutableList<Coordinate> {
        val coordinateList = mutableListOf<Coordinate>()
        lineString.split(" ")               // "111,222 333,444" -> ["111,222", "333,444"]
            .map { it.split(",") }          // ["111,222", "333,444"] -> ["111", "222"], ["333", "444"]
            .associate { (lon, lat) -> lat to lon }   // ["111", "222"], ["333", "444"] -> [("111", "222"), ("333", "444")]
            .forEach {
                coordinateList.add(
                    Coordinate(
                        it.key.toDouble(), // latitude
                        it.value.toDouble() // longitude
                    )
                )
            }
        return coordinateList
    }

    /**
     * 도보 경로를 저장하는 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getWalkRoute(leg: Leg): LegRoute {
        val legRoute = LegRoute()
        legRoute.coordinates.add(Coordinate(leg.start.lat, leg.start.lon))
        if (leg.steps != null) {
            legRoute.pathType = PathType.WALK
            leg.steps.forEach { step ->
                legRoute.coordinates.addAll(convertToCoordsList(step.linestring))
            }
        }
        legRoute.coordinates.add(Coordinate(leg.end.lat, leg.end.lon))
        return legRoute
    }


    /**
     * 지하철, 버스, 고속버스, 기차의 경로 그리는 공통 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getTransportationRoute(leg: Leg): LegRoute {
        val legRoute = LegRoute()
        legRoute.coordinates.add(Coordinate(leg.start.lat, leg.start.lon))
        if (leg.passShape != null) {
            legRoute.coordinates.addAll(convertToCoordsList(leg.passShape.linestring!!))
        }
        legRoute.coordinates.add(Coordinate(leg.end.lat, leg.end.lon))
        return legRoute
    }

    /**
     * 버스 경로를 저장하는 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getBusRoute(leg: Leg): LegRoute {
        return getTransportationRoute(leg).apply { pathType = PathType.BUS }
    }

    /**
     * 고속버스 경로를 저장하는 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getExpressBusRoute(leg: Leg): LegRoute {
        return getTransportationRoute(leg).apply { pathType = PathType.EXPRESSBUS }
    }

    /**
     * 지하철 경로를 저장하는 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getSubwayRoute(leg: Leg): LegRoute {
        return getTransportationRoute(leg).apply { pathType = PathType.SUBWAY }
    }

    /**
     * 기차 경로를 저장하는 함수
     * @param leg 구간의 정보
     *
     * @return Route
     */
    private fun getTrainRoute(leg: Leg): LegRoute {
        return getTransportationRoute(leg).apply { pathType = PathType.TRAIN }
    }
}