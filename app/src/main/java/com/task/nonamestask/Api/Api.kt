package com.task.nonamestask.Api

import com.task.nonamestask.Models.Data
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface Api {

    @GET("/users/{username}/repos")
    @Headers("accept:*/*")
    suspend fun getRepo(@Path("username") username: String?): Response<ArrayList<Data>>?
}