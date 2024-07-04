package com.hansung.sherpa.routelist

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.hansung.sherpa.R


val itemList = arrayListOf(
    RouteItem("12분", "09시 55분 도착"),
    RouteItem("15분","09시 56분 도착"),
    RouteItem("21분","10시 02분 도착"),
    RouteItem("34분","10시 15분 도착"),
    RouteItem("1시간","10시 41분 도착")
)

class RouteListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_list);

        val routeList = findViewById<RecyclerView>(R.id.recycler_View)

        itemList.add(RouteItem("12분","09시 55분 도착"))
        itemList.add(RouteItem("15분","09시 56분 도착"))
        itemList.add(RouteItem("21분","10시 02분 도착"))
        itemList.add(RouteItem("34분","10시 15분 도착"))
        itemList.add(RouteItem("1시간","10시 41분 도착"))

        val routeListAdapter = RouteListAdapter(itemList)
        routeListAdapter.notifyDataSetChanged()

        routeList.adapter = routeListAdapter
        routeList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        routeListAdapter.setItemClickListener(object: RouteListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                Log.d("explain", "클릭")
            }
        })
    }
}