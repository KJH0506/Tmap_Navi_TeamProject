package com.hansung.sherpa.user

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

/**
 * - UserRequest -
 * compose.user 패키지에서 다루는 request 클래스를 모아둔 파일
 *
 */

/**
 * user 정보를 생성하는 클래스
 *
 */
class CreateUserRequest (
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("telNum") val telNum: String,
    @SerializedName("address") val address: String,
    @SerializedName("detailAddress") val detailAddress: String = "지정안함",
    @SerializedName("fcmToken") val fcmToken: String,
    @SerializedName("caregiverId") val caregiverId: Int = 0,
    @SerializedName("caregiverRelation") val caregiverRelation: String = "지정안함",
    @SerializedName("createAt") val createdAt: Timestamp,
    @SerializedName("updateAt") val updatedAt: Timestamp,
)

/**
 * user로 접속하기 위한 클래스
 *
 */
class LoginRequest (
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * 보호자 피보호자 관계를 갱신하기 위한 클래스
 */
data class UpdateUserRelationRequest (
    @SerializedName("caretakerId") private val caretakerId: Int = 0,
    @SerializedName("caregiverId") private val caregiverId: Int = 0,
    @SerializedName("relation") private val relation: String? = null
)