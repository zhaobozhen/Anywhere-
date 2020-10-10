package com.absinthe.anywhere_.api

import com.absinthe.anywhere_.model.cloud.GitHubApiContentBean
import retrofit2.Call
import retrofit2.http.GET

interface GitHubApi {

    @GET("contents")
    fun requestAllContents(): Call<List<GitHubApiContentBean>>
}