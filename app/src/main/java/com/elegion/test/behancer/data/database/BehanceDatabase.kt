package com.elegion.test.behancer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elegion.test.behancer.data.model.project.Cover
import com.elegion.test.behancer.data.model.project.Owner
import com.elegion.test.behancer.data.model.project.Project
import com.elegion.test.behancer.data.model.user.Image
import com.elegion.test.behancer.data.model.user.User

@Database(
    entities = [Project::class, Cover::class, Owner::class, User::class, Image::class],
    version = 1
)
abstract class BehanceDatabase : RoomDatabase() {
    abstract val behanceDao: BehanceDao
}

