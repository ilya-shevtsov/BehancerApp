package com.elegion.test.behancer.data.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Int = 0,

    @ColumnInfo(name = "username")
    @SerializedName("username")
    var username: String? = null,

    @ColumnInfo(name = "location")
    @SerializedName("location")
    var location: String? = null,

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    var createdOn: Long = 0,

    @SerializedName("images")
    @Ignore
    var image: Image? = null,

    @ColumnInfo(name = "display_name")
    @SerializedName("display_name")
    var displayName: String? = null
)
