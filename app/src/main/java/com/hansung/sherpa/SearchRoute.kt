package com.hansung.sherpa

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hansung.sherpa.Geocoding.GeocodingAPI

// 텍스트뷰에 값이 그래도 유지되고 있게하기 위한 뷰모델이다.
// searchRouteViewModel의 이름과 위치는 바꿀 필요가 있을듯
class searchRouteViewModel:ViewModel() {
    val departureText = MutableLiveData<String>()
}
fun SearchRoute(departure:String) {
    val geocodingAPI = GeocodingAPI(departure)
}