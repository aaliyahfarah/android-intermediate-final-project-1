package com.bangkit.storyappdicoding.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkRegister(
    val error: Boolean,
    val message: String
)

@JsonClass(generateAdapter = true)
data class NetworkLogin(
    val error: Boolean,
    val message: String,
    val loginResult: NetworkUser
)

@JsonClass(generateAdapter = true)
data class NetworkUser(
    val userId: String,
    val name: String,
    val token: String
)

@JsonClass(generateAdapter = true)
data class NetworkAddNewStory(
    val error: Boolean,
    val message: String
)

@JsonClass(generateAdapter = true)
data class NetworkStoryList(
    val error: Boolean,
    val message: String,
    val listStory: List<NetworkStory>
)

@JsonClass(generateAdapter = true)
data class NetworkStory(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

fun NetworkStoryList.asDomainModel(): List<Story> {
    return listStory.map {
        Story(
            it.id,
            it.name,
            it.description,
            it.photoUrl,
            it.createdAt,
            it.lat,
            it.lon
        )
    }
}