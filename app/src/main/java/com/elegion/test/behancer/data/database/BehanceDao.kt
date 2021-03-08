package com.elegion.test.behancer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elegion.test.behancer.data.model.project.Cover
import com.elegion.test.behancer.data.model.project.Owner
import com.elegion.test.behancer.data.model.project.Project
import com.elegion.test.behancer.data.model.user.Image
import com.elegion.test.behancer.data.model.user.User

@Dao
interface BehanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProjectList(projects: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoverList(covers: List<Cover>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOwnerList(owners: List<Owner>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(image: Image)

    @Query("SELECT * FROM project")
    fun projectList(): List<Project>

    @Query("SELECT * FROM cover WHERE project_id = :projectId")
    fun getCoverFromProject(projectId: Int): Cover

    @Query("SELECT * FROM owner WHERE project_id = :projectId")
    fun getOwnerListFromProject(projectId: Int): List<Owner>

    @Query("SELECT * FROM user WHERE username = :userName")
    fun getUserByName(userName: String): User

    @Query("SELECT * FROM image WHERE user_id = :userId")
    fun getImageFromUser(userId: Int): Image

    @Query("DELETE FROM owner")
    fun clearOwnerTable()

    @Query("DELETE FROM cover")
    fun clearCoverTable()

    @Query("DELETE FROM image")
    fun clearImageTable()

    @get:Query("SELECT * FROM user")
    val userList: List<User>

    @get:Query("SELECT * FROM image")
    val imageList: List<Image>
}