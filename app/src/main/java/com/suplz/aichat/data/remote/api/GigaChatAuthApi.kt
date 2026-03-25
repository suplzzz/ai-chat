package com.suplz.aichat.data.remote.api

import com.suplz.aichat.data.remote.dto.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatAuthApi {
    @FormUrlEncoded
    @POST("api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authHeader: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS"
    ): TokenResponse
}