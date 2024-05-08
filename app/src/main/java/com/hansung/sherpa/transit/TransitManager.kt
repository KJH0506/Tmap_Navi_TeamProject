package com.hansung.sherpa.transit

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hansung.sherpa.BuildConfig
import com.hansung.sherpa.R
import com.hansung.sherpa.convert.Convert
import com.hansung.sherpa.convert.LegRoute
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 교통수단 API를 관리하는 클래스
 *
 * @since 2024-05-09
 * @author HS-JNYLee
 *
 * @property context Activity의 context
 * @sample TransitManager.sampleGetTransitRoutes
 */
class TransitManager(context: Context) {

    val context: Context = context

    /**
     * 교통수단 API를 사용해 경로 데이터를 가져와 역직렬화하는 함수
     *
     * @param routeRequest 요청할 정보 객체
     * @return LiveData<TransitRouteResponse>
     */
    fun getTransitRoutes(routeRequest: TransitRouteRequest): LiveData<TransitRouteResponse> {
        val resultLiveData = MutableLiveData<TransitRouteResponse>()
        val appKey = BuildConfig.TMAP_APP_KEY // 앱 키
        Retrofit.Builder()
            .baseUrl(context.getString(R.string.route_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TransitRouteService::class.java).postTransitRoutes(appKey, routeRequest) // API 호출
            .enqueue(object : Callback<ResponseBody> {

                // 성공시 콜백
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // Deserialized 역직렬화
                        val transitRouteResponse = Gson().fromJson(responseBody.string(), TransitRouteResponse::class.java)
                        // post to livedata (Change Notification) 변경된 값을 알림
                        resultLiveData.postValue(transitRouteResponse)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })
        return resultLiveData
    }

    /**
     * getTransitRoutes 함수 사용 예시
     *
     * @param context Activity Context
     * @param owner Activity
     */
    private fun sampleGetTransitRoutes(context: Context, owner: LifecycleOwner) {
        // 요청 param setting
        val routeRequest = TransitRouteRequest(
            startX = "126.926493082645",
            startY = "37.6134436427887",
            endX = "127.126936754911",
            endY = "37.5004198786564",
            lang = 0,
            format = "json",
            count = 10
        )
        var transitRoutes: MutableList<MutableList<LegRoute>>
        // 관찰 변수 변경 시 콜백
        TransitManager(context).getTransitRoutes(routeRequest).observe(owner) { transitRouteResponse ->
            transitRoutes = Convert().convertToRouteMutableLists(transitRouteResponse)
        }
    }
}