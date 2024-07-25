package com.hansung.sherpa.busarrivalinfo

import com.google.gson.annotations.SerializedName

/**
 * '정류소별특정노선버스 도착예정정보 목록조회' 요청 파라미터 클래스
 *  https://www.data.go.kr/iim/api/selectAPIAcountView.do
 *
 *  @param serviceKey 공공데이터포털에서 발급받은 인증키
 *  @param _type 데이터 타입(xml, json) - Default: xml
 *  @param cityCode 도시코드 - [상세기능3 도시코드 목록 조회]에서 조회 가능
 *  @param nodeId 정류소 ID - [국토교통부(TAGO)_버스정류소정보]에서 조회가능
 *  @param routeId 노선 ID - 버스고유 ID
 */
class BusArrivalInfoRequest(
    @SerializedName("serviceKey") val serviceKey:String = "VCzeRv5K5pqKSztweNXA4lJt8uyxti3zL4LJr7h+KEULei9hs7ZRTRU0b5jSlTZQ5i3lycuEN7NS6uACbg4ZwA==",
    @SerializedName("_type") val _type:String = "json",
    @SerializedName("cityCode") val cityCode:Int,
    @SerializedName("nodeId") val nodeId:String,
    @SerializedName("routeId") val routeId:String
) {
    /**
     * BusArrivalInfoService가 @QueryMap으로 전달됨
     */
    fun getMap():Map<String,String> = mapOf("serviceKey" to serviceKey,"_type" to _type,"cityCode" to cityCode.toString(),"nodeId" to nodeId,"routeId" to routeId)
}