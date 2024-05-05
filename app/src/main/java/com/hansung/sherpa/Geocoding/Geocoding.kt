package com.cookandroid.mapsdkexercise.Geocoding

import com.google.gson.annotations.SerializedName


data class Geocoding (

  @SerializedName("coordinateInfo" ) var coordinateInfo : CoordinateInfo? = CoordinateInfo()

)