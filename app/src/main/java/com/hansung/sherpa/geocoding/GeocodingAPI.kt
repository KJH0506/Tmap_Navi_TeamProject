/*

         ----------------  호출 예시  ----------------

        (findViewById<Button>(R.id.Button)).setOnClickListener { it ->
            var address = findViewById<EditText>(R.id.editTextText).text.toString();
            if (address != "")
                GeocodingAPI(address).request()
        }

        ----------------  호출 예시  ----------------

 */

package com.hansung.sherpa.geocoding

import android.net.Uri
import android.util.Log
import com.hansung.sherpa.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GeocodingAPI(private val address: String) {
    fun request(){
        val encodedAddress : String = Uri.encode(address)
        GeocodingRetrofitManager.getGeocodingServiceInstance()
            .geocoding(BuildConfig.TMAP_APP_KEY,"1", encodedAddress)
            .enqueue(object : Callback<Geocoding> {
                override fun onResponse(call: Call<Geocoding>, response: Response<Geocoding>) {
                    if(response.isSuccessful) {
                        try {
                            val coordinate = response.body()?.coordinateInfo?.coordinate?.get(0)
                            val lat: String = if (coordinate?.lat != null) coordinate.lat!!
                                else throw IllegalArgumentException("null")

                            val lon: String = if (coordinate.lon != null) coordinate.lon!!
                                else throw IllegalArgumentException("null")

                            Log.d("status", "GeocodingAPI : (lat : $lat, lon : $lon)")
                        } catch (_: Exception) {
                            Log.d("status", "GeocodingAPI : Exception")
                        }
                    }
                }
                override fun onFailure(call: Call<Geocoding>, t: Throwable) {
                    Log.d("status", "GeocodingAPI : failure")
                }
            })
    }
}