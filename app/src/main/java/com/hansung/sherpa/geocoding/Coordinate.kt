package com.hansung.sherpa.geocoding

import com.google.gson.annotations.SerializedName


data class Coordinate (

  @SerializedName("matchFlag"           ) var matchFlag           : String? = null,
  @SerializedName("lat"                 ) var lat                 : String? = null,
  @SerializedName("lon"                 ) var lon                 : String? = null,
  @SerializedName("latEntr"             ) var latEntr             : String? = null,
  @SerializedName("lonEntr"             ) var lonEntr             : String? = null,
  @SerializedName("city_do"             ) var cityDo              : String? = null,
  @SerializedName("gu_gun"              ) var guGun               : String? = null,
  @SerializedName("eup_myun"            ) var eupMyun             : String? = null,
  @SerializedName("legalDong"           ) var legalDong           : String? = null,
  @SerializedName("legalDongCode"       ) var legalDongCode       : String? = null,
  @SerializedName("adminDong"           ) var adminDong           : String? = null,
  @SerializedName("adminDongCode"       ) var adminDongCode       : String? = null,
  @SerializedName("ri"                  ) var ri                  : String? = null,
  @SerializedName("bunji"               ) var bunji               : String? = null,
  @SerializedName("buildingName"        ) var buildingName        : String? = null,
  @SerializedName("buildingDong"        ) var buildingDong        : String? = null,
  @SerializedName("newMatchFlag"        ) var newMatchFlag        : String? = null,
  @SerializedName("newLat"              ) var newLat              : String? = null,
  @SerializedName("newLon"              ) var newLon              : String? = null,
  @SerializedName("newLatEntr"          ) var newLatEntr          : String? = null,
  @SerializedName("newLonEntr"          ) var newLonEntr          : String? = null,
  @SerializedName("newRoadName"         ) var newRoadName         : String? = null,
  @SerializedName("newBuildingIndex"    ) var newBuildingIndex    : String? = null,
  @SerializedName("newBuildingName"     ) var newBuildingName     : String? = null,
  @SerializedName("newBuildingCateName" ) var newBuildingCateName : String? = null,
  @SerializedName("newBuildingDong"     ) var newBuildingDong     : String? = null,
  @SerializedName("zipcode"             ) var zipcode             : String? = null,
  @SerializedName("remainder"           ) var remainder           : String? = null

)