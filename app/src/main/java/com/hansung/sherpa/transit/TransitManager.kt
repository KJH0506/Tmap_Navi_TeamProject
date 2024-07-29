package com.hansung.sherpa.transit

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hansung.sherpa.BuildConfig
import com.hansung.sherpa.R
import com.hansung.sherpa.convert.Convert
import com.hansung.sherpa.convert.LegRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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
    fun getTransitRoutes(routeRequest: TmapTransitRouteRequest): LiveData<TmapTransitRouteResponse> {
        val resultLiveData = MutableLiveData<TmapTransitRouteResponse>()
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
                        val tmapTransitRouteResponse = Gson().fromJson(responseBody.string(), TmapTransitRouteResponse::class.java)
                        // post to livedata (Change Notification) 변경된 값을 알림
                        resultLiveData.postValue(tmapTransitRouteResponse)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        return resultLiveData
    }

    /**
     * 교통수단 API를 사용해 경로 데이터를 가져와 역직렬화하는 함수
     *
     * @param routeRequest 요청할 정보 객체
     * @return TransitRouteResponse
     */
    fun getTmapTransitRoutes(routeRequest: TmapTransitRouteRequest): TmapTransitRouteResponse {
        val appKey = BuildConfig.TMAP_APP_KEY // 앱 키
        lateinit var rr: TmapTransitRouteResponse
        runBlocking<Job> {
            launch(Dispatchers.IO) {
                try {
                    val response = Retrofit.Builder()
                        .baseUrl(context.getString(R.string.route_base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create<TransitRouteService?>(TransitRouteService::class.java)
                        .postTransitRoutes(appKey, routeRequest).execute() // API 호출
                    rr = Gson().fromJson(response.body()!!.string(), TmapTransitRouteResponse::class.java)
                    // Error Log
                    /*if (rr.metaData == null) {
                        val errorCode = Gson().fromJson(response.body()!!.string(), TmapTransitErrorCode::class.java)
                        Log.e("Error", "Error Code: ${errorCode.result?.status}, ${errorCode.result?.message}")
                        // getOdsayTransitRoute(Convert().convertTmapToOdsayRequest(routeRequest))
                    }*/
                } catch (e: IOException) {
                    Log.e("Error", "Transit API Exception")
                    rr = TmapTransitRouteResponse()
                }
            }
        }
        return rr
    }

    fun getOdsayTransitRoute(routeRequest: OdsayTransitRouteRequest): OdsayTransitRouteResponse? {
        var rr: OdsayTransitRouteResponse? = null
        runBlocking<Job> {
            launch(Dispatchers.IO) {
                try {
                    val response = Retrofit.Builder()
                        .baseUrl(context.getString(R.string.odsay_route_base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create<TransitRouteService?>(TransitRouteService::class.java)
                        .getOdsayTransitRoutes(odsayRequestToMap(routeRequest)).execute()
                    rr = Gson().fromJson(response.body()!!.string(), OdsayTransitRouteResponse::class.java)
                    // Error Log
                    /*if (rr!!.result == null) {
                        val errorCode = Gson().fromJson(response.body()!!.string(), OdsayTransitRouteErrorCode::class.java)
                        Log.e("Error", "Error Code: ${errorCode.error.code}, ${errorCode.error.msg}")
                    }*/
                } catch (e: IOException) {
                    Log.e("Error", "Transit API Exception ${rr}")
                }
            }
        }
        return rr
    }

    /**
     * 보행자 API를 사용해 경로 데이터를 가져와 역직렬화하는 함수
     *
     * @param routeRequest:PedestrianRouteRequest 요청할 정보 객체
     * @return PedestrianResponse
     */
    fun getPedestrianRoute(routeRequest: PedestrianRouteRequest): PedestrianResponse {
        val appKey = BuildConfig.TMAP_APP_KEY // 앱 키
        lateinit var rr: PedestrianResponse
        runBlocking<Job> {
            launch(Dispatchers.IO) {
                try {
                    Log.d("reqlocation","" + routeRequest.startY +", "+ routeRequest.startX+"    "+routeRequest.endY+", "+routeRequest.endX)
                    val response = Retrofit.Builder()
                        .baseUrl(context.getString(R.string.route_base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create<PedestrianRouteService?>(PedestrianRouteService::class.java)
                        .postPedestrianRoutes(appKey, routeRequest).execute() // API 호출
                    rr = Gson().fromJson(
                        response.body()!!.string(),
                        PedestrianResponse::class.java
                    )
                } catch (e: IOException) {
                    Log.i("Error", "Transit API Exception")
                    rr = PedestrianResponse()
                }
            }
        }
        return rr
    }

    /**
     * getTransitRoutes 함수 사용 예시
     *
     * @param context Activity Context
     * @param owner Activity
     */
    fun sampleGetTransitRoutes(context: Context, owner: LifecycleOwner) {
        // 요청 param setting
        val routeRequest = TmapTransitRouteRequest(
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

    fun odsayRequestToMap(request: OdsayTransitRouteRequest): Map<String, String> {
        return mapOf(
            "apiKey" to request.apiKey,
            "SX" to request.SX,
            "SY" to request.SY,
            "EX" to request.EX,
            "EY" to request.EY,
            "OPT" to request.OPT,
            "SearchType" to request.SearchType,
            "SearchPathType" to request.SearchPathType
        )
    }
}