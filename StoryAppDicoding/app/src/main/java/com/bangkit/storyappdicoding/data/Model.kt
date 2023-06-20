package com.bangkit.storyappdicoding.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
) : Parcelable

data class User(
    val userId: String,
    val email: String,
    val password: String,
    val name: String,
    val token: String,
    val isLogin: Boolean
)

@JsonClass(generateAdapter = true)
data class Status(
    val error: Boolean,
    val message: String?
)
