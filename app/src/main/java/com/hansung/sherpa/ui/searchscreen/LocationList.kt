package com.hansung.sherpa.ui.searchscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hansung.sherpa.itemsetting.LatLng
import com.hansung.sherpa.searchlocation.SearchLoactionCallBack
import com.hansung.sherpa.searchlocation.SearchLocation
import com.hansung.sherpa.searchlocation.SearchLocationResponse

/**
 * 검색 키워드를 통해 장소를 선택하기 위한 영역
 * 최종적으로 장소(출발지 or 목적지)를 선택하는 영역이다.
 *
 * @param locationValue 장소 리스트를 띄울 검색어
 * @param update 장소 선택이 완료되었다면, 선택한 장소 정보를 타입에 맞게 출발지 목적지로 지정하기 위한 함수. State Hoisting을 이용한다.
 */
@Composable
fun LocationList(locationValue:String, update: (String, LatLng) -> Unit) {
    var searchLocationResponse by remember { mutableStateOf(SearchLocationResponse()) }

    /**
     * locationValue가 변경된다면 발생하는 함수
     * SearchArea.kt의 Departure TextField, Destination TextField 참고
     * 
     * locationValue 값에 따라서 장소 리스트를 생성한다.
     */
    LaunchedEffect(locationValue) {
        SearchLocation().search(locationValue, object : SearchLoactionCallBack {
            override fun onSuccess(response: SearchLocationResponse?) {
                searchLocationResponse = response!!
            }

            override fun onFailure(message: String) {
                Log.e("SearchLocation error", "지역검색 API 오류: $message")
            }
        })
    }

    /**
     * Location List LazyColumn
     * locationValue 키워드에 맞는 장소 리스트
     *
     * 선택 시 해당 경로의 이름과 주소를 반환 받는다.
     */
    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(searchLocationResponse.items){
            Row(modifier = Modifier.background(Color.White).padding(vertical = 8.dp).clickable {
                val x = it.mapx?.toDouble()?.div(10000000)?:-1.0 // 경도 (lon)
                val y = it.mapy?.toDouble()?.div(10000000)?:-1.0 // 위도 (lat)

                update(it.title?:"Null",LatLng(y,x))

                searchLocationResponse = SearchLocationResponse()
            }){
                /**
                 * Location Name Text
                 *
                 * 장소의 이름이 나오는 영역
                 */
                Text(text = it.title!! , maxLines = 1)

                Spacer(modifier = Modifier.weight(1f))

                /**
                 * Address Text
                 *
                 * 장소의 주소가 나오는 영역
                 */
                Text(text = it.address!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}