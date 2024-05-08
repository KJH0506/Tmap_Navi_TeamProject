package com.hansung.sherpa.geocoding

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


interface GeocodingService {
    @Headers("Accept: application/json")
    @GET("tmap/geo/fullAddrGeo")
    fun geocoding(@Header("appKey") appKey: String, @Query("version") version : String, @Query("fullAddr") fullAddr : String): Call<Geocoding>
}
