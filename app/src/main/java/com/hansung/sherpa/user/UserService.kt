package com.hansung.sherpa.user

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    /**
     * 계정을 생성하는 API
     */
    @POST("user/createUser")
    fun postCreateService(@Body body: CreateUserRequest): Call<ResponseBody>

    /**
     * 계정 로그인(사용자 인증) 하는 API
     */
    @POST("user/login")
    fun postLoginService(@Body body: LoginRequest): Call<ResponseBody>

    /**
     * 사용자 정보를 얻는 API
     */
    @GET("user/getUser/{userId}")
    fun getUser(@Path("userId") userId:Int): Call<ResponseBody>

    /**
     * 보호자 인증을 요청하는 API
     */
    @GET("user/getLinkPermission/{caretakerId}/{caregiverEmail}")
    fun getLinkPermissionService(@Path("caretakerId") userId: Int, @Path("caregiverEmail") body: String): Call<ResponseBody>

    /**
     * 사용자와 보호자의 관계를 얻는 API
     */
    @GET("userRelation/getUserRelation/{userId}")
    fun getRelationService(@Path("userId") userId:Int): Call<ResponseBody>

    /**
     * 사용자와 보호자의 관계 갱신해주는 API
     */
    @PATCH("userRelation/updateUserRelation")
    fun updateRelationService(@Body body: UpdateUserRelationRequest): Call<ResponseBody>

    @GET("user/signupEmail/{email}")
    fun verificatonEmail(@Path("email") email:String): Call<ResponseBody>

    @GET("user/signupTelNum/{telNum}")
    fun verificatonTelNum(@Path("telNum") telNum:String): Call<ResponseBody>

    @GET("user/caregiver/list")
    fun getCaregiverList(): Call<ResponseBody>
}
