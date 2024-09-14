package com.hansung.sherpa.user

import android.util.Log
import com.google.gson.Gson
import com.hansung.sherpa.StaticValue
import com.hansung.sherpa.Url
import com.hansung.sherpa.user.updateFcm.UpdateFcmRequest
import com.hansung.sherpa.user.updateFcm.UpdateFcmResponse
import com.hansung.sherpa.user.updateFcm.UpdateFcmService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserManager {
    /**
     * 계정을 생성하는 함수
     *
     * ### 상태 코드
     * 200: API 요청 성공
     * 
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun create(request: CreateUserRequest): UserResponse {
        var result: UserResponse? = null
        runBlocking {
            launch(Dispatchers.IO) {
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .postCreateService(request).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.create: 'response is null'")
                        result = UserResponse(404, "UserManager.create: 응답없음")
                    }
                    else {
                        Log.i("API Log: Success", "create 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserResponse::class.java
                        )
                    }
                } catch(e:IOException){
                    Log.e("API Log: IOException", "UserManager.create: ${e.message}(e.message)")
                    result = UserResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: UserResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 계정 로그인(사용자 인증)하는 함수
     *
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     *
     * 401: 로그인 실패
     */
    fun login(email:String, password:String): UserResponse {
        val loginRequest = LoginRequest(email,password)
        var result: UserResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .postLoginService(loginRequest).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.login: 'response is null'")
                        result = UserResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "login 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserResponse::class.java
                        )
                    }
                } catch (e:IOException){
                    Log.e("API Log: IOException", "UserManager.login: ${e.message}(e.message)")
                    result = UserResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: UserResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 사용자 정보를 얻는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun getUser(userId:Int): UserResponse {
        var result: UserResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .getUser(userId).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.getUser: 'response is null'")
                        result = UserResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "getUser 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserResponse::class.java
                        )
                    }
                }catch(e: java.io.IOException){
                    Log.e("API Log: IOException", "UserManager.getUser: ${e.message}(e.message)")
                    result = UserResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: UserResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 모든 보호자 리스트를 가져오는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun getCaregiverUsersList(): UserListResponse {
        var result: UserListResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .getCaregiverList().execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.getUser: 'response is null'")
                        result = UserListResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "getUser 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserListResponse::class.java
                        )
                    }
                }catch(e: java.io.IOException){
                    Log.e("API Log: IOException", "UserManager.getUser: ${e.message}(e.message)")
                    result = UserListResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: UserListResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 보호자 인증을 요청하는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun linkPermission(caregiverEmail: String):LinkPermissionResponse {
        var result: LinkPermissionResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .getLinkPermissionService(StaticValue.userInfo.userId!!, caregiverEmail).execute()
                    //TODO: 잘못된 Email로 요청할 때 에러처리 해야한다.
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.linkPermission: 'response is null'")
                        result = LinkPermissionResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "linkPermission 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            LinkPermissionResponse::class.java
                        )
                    }
                } catch(e:IOException){
                    Log.e("API Log: IOException", "UserManager.linkPermission: ${e.message}(e.message)")
                    result = LinkPermissionResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: LinkPermissionResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 사용자와 보호자의 관계를 얻는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun getRelation(userId:Int): RelationResponse {
        var result: RelationResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .getRelationService(userId).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.getRelation: 'response is null'")
                        result = RelationResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "getRelation 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            RelationResponse::class.java
                        )
                    }
                } catch(e: java.io.IOException){
                    Log.e("API Log: IOException", "UserManager.getRelation: ${e.message}(e.message)")
                    result = RelationResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?:RelationResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 사용자와 보호자의 관계를 얻는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun updateRelation(relationRequest: UpdateUserRelationRequest): RelationResponse {
        var result: RelationResponse? = null
        runBlocking {
            launch(Dispatchers.IO){
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .updateRelationService(relationRequest).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.getRelation: 'response is null'")
                        result = RelationResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "getRelation 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            RelationResponse::class.java
                        )
                    }
                } catch(e: java.io.IOException){
                    Log.e("API Log: IOException", "UserManager.getRelation: ${e.message}(e.message)")
                    result = RelationResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?:RelationResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 사용자 정보를 얻는 함수
     * ### 상태 코드
     * 200: API 요청 성공
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun updateFcm() {
        val updateFcmRequest = UpdateFcmRequest()
        var result: UpdateFcmResponse? = null
        runBlocking {
            launch(Dispatchers.IO) {
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UpdateFcmService::class.java)
                        .patchUpdateFcmService(updateFcmRequest).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    // 반환 실패에 대한 에러처리
                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.updateFcm: 'response is null'")
                        result = UpdateFcmResponse(404, "'reponse.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "updateFcm 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UpdateFcmResponse::class.java
                        )
                    }
                } catch (e:IOException){
                    Log.e("API Log: IOException", "UserManager.updateFcm: ${e.message}(e.message)")
                    result = UpdateFcmResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
    }

    /**
     * 이메일 중복 검사
     * ### 상태 코드
     * 200: API 요청 성공 (이메일 중복)
     *
     * 201: API 요청 성공 (이메일 중복 X)
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun verificatonEmail(email: String): UserResponse{
        var result: UserResponse? = null
        runBlocking {
            launch(Dispatchers.IO) {
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .verificatonEmail(email).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.updateFcm: 'response is null'")
                        result = UserResponse(404, "'response.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "verificatonEmail 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserResponse::class.java
                        )
                    }
                } catch (e:IOException){
                    Log.e("API Log: IOException", "UserManager.verificatonEmail: ${e.message}(e.message)")
                    result = UserResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?: UserResponse(500, "에러 원인을 찾을 수 없음")
    }

    /**
     * 전화번호 중복 확인
     * ### 상태 코드
     * 200: API 요청 성공 (전화번호 중복)
     *
     * 201: API 요청 성공 (전화번호 중복 X)
     *
     * 404: Null 값 반환
     *
     * 404: 네트워크 연결 실패
     *
     * 500: 에러 원인을 찾을 수 없음
     */
    fun verificatonTelNum(telNum: String): UserResponse{
        var result: UserResponse? = null
        runBlocking {
            launch(Dispatchers.IO) {
                try{
                    val response = Retrofit.Builder()
                        .baseUrl(Url.SHERPA)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(UserService::class.java)
                        .verificatonTelNum(telNum).execute()
                    val jsonString = response.body()?.string()?:"response is null"

                    if(jsonString == "response is null") {
                        Log.e("API Log:response(Null)", "UserManager.verificatonTelNum: 'response is null'")
                        result = UserResponse(404, "'response.body()' is null")
                    }
                    else {
                        Log.i("API Log: Success", "verificatonTelNum 함수 실행 성공 ${result?.message}")
                        result = Gson().fromJson(
                            jsonString,
                            UserResponse::class.java
                        )
                    }
                } catch (e:IOException){
                    Log.e("API Log: IOException", "UserManager.verificatonTelNum: ${e.message}(e.message)")
                    result = UserResponse(404, "IOException: 네트워크 연결 실패")
                }
            }
        }
        return result?:UserResponse(500, "에러 원인을 찾을 수 없음")
    }
}