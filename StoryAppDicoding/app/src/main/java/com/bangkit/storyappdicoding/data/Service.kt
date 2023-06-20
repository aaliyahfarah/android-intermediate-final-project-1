package com.bangkit.storyappdicoding.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DicodingApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): NetworkRegister

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): NetworkLogin

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") authHeader: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): NetworkAddNewStory

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") authHeader: String
    ): NetworkStoryList
}

object DicodingApiNetwork {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val dicodingApiService: DicodingApiService =
        retrofit.create(DicodingApiService::class.java)
}