package com.hansung.sherpa.transit

data class TransitRouteResponse(
    val metaData: MetaData? = null
)

data class MetaData(
    val requestParameters: RequestParameters? = null,
    val plan: Plan? = null
)

data class RequestParameters(
    val busCount: Int? = null,
    val expressbusCount: Int? = null,
    val subwayCount: Int? = null,
    val airplaneCount: Int? = null,
    val locale: String? = null,
    val endY: String? = null,
    val endX: String? = null,
    val wideareaRouteCount: Int? = null,
    val subwayBusCount: Int? = null,
    val startY: String? = null,
    val startX: String? = null,
    val ferryCount: Int? = null,
    val trainCount: Int? = null,
    val reqDttm: String? = null
)

data class Plan(
    val itineraries: List<Itinerary>
)

data class Itinerary(
    val fare: Fare? = null,
    val totalTime: Int? = null,
    val legs: List<Leg>? = null,
    val totalWalkTime: Int? =null,
    val transferCount: Int? = null,
    val pathType: Int? = null // 교통수단 종류
)

data class Fare(
    val regular: Regular? = null
)

data class Regular(
    val totalFare: Int? = null,
    val currency: Currency? = null
)

data class Currency(
    val symbol: String? = null,
    val currency: String? = null,
    val currencyCode: String? = null
)

data class Leg(
    val mode: String? = null,
    val sectionTime: Int? = null,
    val distance: Int? = null,
    val start: Location,
    val end: Location,
    val steps: List<Step>? = null,
    val routeColor: String? = null,
    val route: String? = null,
    val routeId: String? = null,
    val service: Int? = null,
    val passStopList: PassStopList? = null,
    val type: Int? = null,
    val passShape: PassShape? = null
)

data class Location(
    val name: String,
    val lon: Double,
    val lat: Double
)

data class Step(
    val streetName: String? = null,
    val distance: Int? = null,
    val description: String? = null,
    val linestring: String
)

data class PassStopList(
    val stationList: List<Station>? = null
)

data class Station(
    val index: Int? = null,
    val stationName: String? = null,
    val lon: String,
    val lat: String,
    val stationID: String? = null
)

data class PassShape(
    val linestring: String? = null
)
