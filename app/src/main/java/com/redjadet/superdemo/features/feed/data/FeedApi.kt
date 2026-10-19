package com.redjadet.superdemo.features.feed.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface FeedApi {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}

@Serializable
data class PostDto(
    @SerialName("userId") val userId: Int,
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String,
)
