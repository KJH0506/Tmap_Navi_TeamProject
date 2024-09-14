package com.hansung.sherpa.user

import com.hansung.sherpa.user.table.Relation
import com.hansung.sherpa.user.table.User1

/**
 * - UserResponse -
 * compose.user 패키지에서 다루는 response 클래스를 모아둔 파일
 *
 */

/**
 * User1 객체를 반환 받을 때 사용하는 클래스
 *
 */
data class UserResponse(
    val code: Int? = null,
    val message: String? = null,
    val `data`: User1? = null
)

/**
 * User1 리스트 객체를 반환 받을 때 사용하는 클래스
 *
 */
data class UserListResponse(
    val code: Int? = null,
    val message: String? = null,
    val data: List<User1>? = null
)

/**
 * 사용자가 보호자 userId를 반환 받을 때 사용하는 클래스
 *
 */
class LinkPermissionResponse (
    val code: Int? = null,
    val message: String? = null,
    val data:Int? = null
)

/**
 * 사용자와 보호자의 관계를 반환하는 클래스
 *
 */
data class RelationResponse(
    val code: Int? = null,
    val message: String? = null,
    val `data`: Relation? = null
)