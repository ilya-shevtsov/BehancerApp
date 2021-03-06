package com.elegion.test.behancer.data.model.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserResponse (
    @SerializedName("user")
    val user: User
): Serializable
