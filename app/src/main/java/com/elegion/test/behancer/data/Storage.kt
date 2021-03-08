package com.elegion.test.behancer.data

import androidx.core.util.Pair
import com.elegion.test.behancer.data.database.BehanceDao
import com.elegion.test.behancer.data.model.project.Cover
import com.elegion.test.behancer.data.model.project.Owner
import com.elegion.test.behancer.data.model.project.Project
import com.elegion.test.behancer.data.model.project.ProjectResponse
import com.elegion.test.behancer.data.model.user.UserResponse
import java.util.*

class Storage(private val mBehanceDao: BehanceDao) {
    fun insertProjects(response: ProjectResponse) {
        val projects = response.projectList
        mBehanceDao.insertProjectList(projects)
        val assembled = assemble(projects)
        mBehanceDao.clearCoverTable()
        assembled.first?.let { mBehanceDao.insertCoverList(it) }
        mBehanceDao.clearOwnerTable()
        assembled.second?.let { mBehanceDao.insertOwnerList(it) }
    }

    private fun assemble(projects: List<Project>): Pair<List<Cover>, List<Owner>> {
        val covers: MutableList<Cover> = ArrayList()
        val owners: MutableList<Owner> = ArrayList()
        for (i in projects.indices) {
            val cover = projects[i].cover
            if (cover != null) {
                cover.id = i
                cover.projectId = projects[i].id
                covers.add(cover)
                val owner = projects[i].ownerList?.get(0)
                if (owner != null) {
                    owner.id = i
                    owner.projectId = projects[i].id
                    owners.add(owner)
                }

            }

        }
        return Pair(covers, owners)
    }

    val projects: ProjectResponse
        get() {
            val projects = mBehanceDao.projectList()
            for (project in projects) {
                project.cover = mBehanceDao.getCoverFromProject(project.id)
                project.ownerList = mBehanceDao.getOwnerListFromProject(project.id)
            }
            return ProjectResponse(projects)
        }

    fun insertUser(response: UserResponse) {
        val user = response.user
        mBehanceDao.insertUser(user)
        val image = user.image
        if (image != null){
            image.id = user.id
            image.userId = user.id
            mBehanceDao.insertImage(image)
        }



    }

    fun getUser(username: String): UserResponse {
        val user = mBehanceDao.getUserByName(username)
        val image = mBehanceDao.getImageFromUser(user.id)
        user.image = image
        return UserResponse(user)
    }

    interface StorageOwner {
        fun obtainStorage(): Storage?
    }
}
