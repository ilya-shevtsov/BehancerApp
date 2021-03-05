package com.elegion.test.behancer.data.model.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = [("id")],
        childColumns = [("project_id")]
    )]
)
class Owner : Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "username")
    @SerializedName("username")
    var username: String? = null
        private set

    @ColumnInfo(name = "project_id")
    var projectId = 0

    fun setUsername(username: String) {
        this.username = username
    }
}