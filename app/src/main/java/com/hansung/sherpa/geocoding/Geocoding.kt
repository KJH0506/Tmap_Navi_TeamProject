package com.hansung.sherpa.geocoding

import com.google.gson.annotations.SerializedName


data class Geocoding (

  @SerializedName("coordinateInfo" ) var coordinateInfo : CoordinateInfo? = CoordinateInfo()

)