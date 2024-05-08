package com.hansung.sherpa.convert


/**
 * 위도, 경도 타입 지정
 */
typealias coordinate = Double

/**
 * 좌표 값을 저장하는 클래스
 *
 * @since 2024-05-09
 * @author HS-JNYLee
 *
 * @param lat 위도
 * @param lon 경도
 * @sample com.hansung.sherpa.convert.coordinateSample
 */
class Coordinate(lat: coordinate = 0.0, lon: coordinate = 0.0) {
    val latitude: coordinate = lat
    val longitude: coordinate = lon
}

private fun coordinateSample() {
    val coordinate = Coordinate(37.607075, 127.00584)
}

