package com.elegion.test.behancer.data.api

import com.elegion.test.behancer.data.model.project.ProjectResponse
import com.elegion.test.behancer.data.model.user.UserResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BehanceApi {

    @GET("v2/projects")
    fun getProjectList(@Query("q") query: String): Single<ProjectResponse>

    @GET("v2/users/{username}")
    fun getUserInfo(@Path("username") username: String?): Single<UserResponse>
}