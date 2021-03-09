package com.example.twitterspammer.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("1.1/statuses/update.json")
    fun postTweet(
        @Part("status") text:RequestBody
    ): Call<ResponseBody>

}