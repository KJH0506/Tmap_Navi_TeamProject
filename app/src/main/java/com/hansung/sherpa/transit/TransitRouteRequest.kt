package com.hansung.sherpa.transit

import com.google.gson.annotations.SerializedName

/**
 * 교통수단 API를 사용하기 위한 요청 param 클래스
 */
data class TransitRouteRequest(
    @SerializedName("startX") val startX: String,
    @SerializedName("startY") val startY: String,
    @SerializedName("endX") val endX: String,
    @SerializedName("endY") val endY: String,
    @SerializedName("lang") val lang: Int,
    @SerializedName("format") val format: String,
    @SerializedName("count") val count: Int
)


