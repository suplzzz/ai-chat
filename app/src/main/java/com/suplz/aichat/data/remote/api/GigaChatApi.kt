package com.suplz.aichat.data.remote.api

import com.suplz.aichat.data.remote.dto.BalanceResponse
import com.suplz.aichat.data.remote.dto.ChatRequest
import com.suplz.aichat.data.remote.dto.ChatResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GigaChatApi {
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): ChatResponse

    @GET("files/{file_id}/content")
    suspend fun getFileContent(@Path("file_id") fileId: String): ResponseBody

    @GET("balance")
    suspend fun getBalance(): BalanceResponse
}