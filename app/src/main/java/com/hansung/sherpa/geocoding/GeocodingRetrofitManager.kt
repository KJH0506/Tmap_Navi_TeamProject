package com.hansung.sherpa.geocoding

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingRetrofitManager private constructor() {
    companion object {
        @Volatile private var instance : Retrofit? = null
        @Volatile private var geocodingService : GeocodingService? = null
        private fun getInstance() : Retrofit {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Retrofit.Builder()
                            .baseUrl("https://apis.openapi.sk.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().also { instance = it }
                    }
                }
            }
            return instance!!
        }
        fun getGeocodingServiceInstance() : GeocodingService{
            if(geocodingService == null){
                synchronized(this){
                    if(geocodingService == null){
                        geocodingService =  getInstance().create(GeocodingService::class.java)
                    }
                }
            }
            return geocodingService!!
        }
    }
}