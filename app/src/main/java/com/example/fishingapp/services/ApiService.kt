package com.example.fishingapp.services

import com.example.fishingapp.models.PostModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/solunar/{allUrl}")
    fun getPosts(@Path("allUrl") allUrl: String): Call<PostModel>
}