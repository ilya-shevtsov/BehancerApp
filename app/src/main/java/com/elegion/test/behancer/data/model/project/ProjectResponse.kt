package com.elegion.test.behancer.data.model.project

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ProjectResponse(
    @SerializedName("projects")
    val projects: List<Project>
) : Serializable
