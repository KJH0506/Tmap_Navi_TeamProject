package com.hansung.sherpa.convert

/**
 *  Enum Class
 *  이동수단 타입 지정
 *
 *  @author HS-JNYLee
 *  @since 2024-05-09
 *
 *  @property WALK 도보
 *  @property BUS 버스
 *  @property SUBWAY 지하철
 *  @property EXPRESSBUS 고속버스
 *  @property TRAIN 지하철
 *  
 */
enum class PathType {
    WALK, BUS, SUBWAY, EXPRESSBUS, TRAIN
}

/**
 * 구간별 이동 경로 좌표와 타입을 저장하는 클래스
 *
 *  @author HS-JNYLee
 *  @since 2024-05-09
 *
 *  @param coordinates 이동 경로 좌표
 *  @param pathType 이동수단 타입 : PathType
 */
class LegRoute(coordinates: MutableList<Coordinate> = mutableListOf(), pathType: PathType = PathType.WALK) {
    var coordinates: MutableList<Coordinate> = coordinates
    var pathType: PathType = pathType
}