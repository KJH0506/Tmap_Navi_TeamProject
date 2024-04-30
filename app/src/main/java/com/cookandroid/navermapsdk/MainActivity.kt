package com.cookandroid.navermapsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naver.maps.map.NaverMapSdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(com.cookandroid.navermapsdk.BuildConfig.api_key) // 본인 api key
    }
}