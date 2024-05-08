package com.hansung.sherpa.transit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * retrofit을 사용한 Transit API 쿼리 클래스
 *
 * @since 2024-05-09
 * @author HS-JNYLee
 */
interface TransitRouteService {

    /**
     * 대중교통을 이용한 도보 TMap API
     *
     * @param appKey TMAP APP KEY
     * @param body ROUTE REQUEST
     */
    @Headers(
        "accept: application/json",
        "content-type: application/json",
    )
    @POST("transit/routes")
    fun postTransitRoutes(@Header("appKey") appKey: String, @Body body: TransitRouteRequest): Call<ResponseBody>
}
