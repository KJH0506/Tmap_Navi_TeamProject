package com.cookandroid.mapsdkexercise.Geocoding

import com.google.gson.annotations.SerializedName


data class CoordinateInfo (

  @SerializedName("coordType"   ) var coordType   : String?               = null,
  @SerializedName("addressFlag" ) var addressFlag : String?               = null,
  @SerializedName("page"        ) var page        : String?               = null,
  @SerializedName("count"       ) var count       : String?               = null,
  @SerializedName("totalCount"  ) var totalCount  : String?               = null,
  @SerializedName("coordinate"  ) var coordinate  : ArrayList<Coordinate> = arrayListOf()

)