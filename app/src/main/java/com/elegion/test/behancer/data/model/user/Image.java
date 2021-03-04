package com.elegion.test.behancer.data.model.user;



import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vladislav Falzan.
 */
@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "user_id"
))
public class Image {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "photo_url")
    @SerializedName("230")
    private String mPhotoUrl;

    @ColumnInfo(name = "user_id")
    private int mUserId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }
}
