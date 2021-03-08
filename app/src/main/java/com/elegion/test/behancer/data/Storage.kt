package com.elegion.test.behancer.data

import androidx.core.util.Pair
import com.elegion.test.behancer.data.database.BehanceDao
import com.elegion.test.behancer.data.model.project.Cover
import com.elegion.test.behancer.data.model.project.Owner
import com.elegion.test.behancer.data.model.project.Project
import com.elegion.test.behancer.data.model.project.ProjectResponse
import com.elegion.test.behancer.data.model.user.UserResponse
import java.util.*

class Storage(private val behanceDao: BehanceDao) {

    fun insertProjects(response: ProjectResponse) {
        val projects = response.projectList
        behanceDao.insertProjectList(projects)
        val assembled = assemble(projects)
        behanceDao.clearCoverTable()
        assembled.first?.let { behanceDao.insertCoverList(it) }
        behanceDao.clearOwnerTable()
        assembled.second?.let { behanceDao.insertOwnerList(it) }
    }

    private fun assemble(projectList: List<Project>): Pair<List<Cover>, List<Owner>> {
        val coverList: MutableList<Cover> = ArrayList()
        val ownerList: MutableList<Owner> = ArrayList()
        for (project in projectList.indices) {
            val cover = projectList[project].cover
            if (cover != null) {
                cover.id = project
                cover.projectId = projectList[project].id
                coverList.add(cover)
                val owner = projectList[project].ownerList?.get(0)
                if (owner != null) {
                    owner.id = project
                    owner.projectId = projectList[project].id
                    ownerList.add(owner)
                }
            }
        }
        return Pair(coverList, ownerList)
    }

    val projectListResponse: ProjectResponse
        get() {
            val projectList = behanceDao.projectList()
            for (project in projectList) {
                project.cover = behanceDao.getCoverFromProject(project.id)
                project.ownerList = behanceDao.getOwnerListFromProject(project.id)
            }
            return ProjectResponse(projectList)
        }

    fun insertUser(response: UserResponse) {
        val user = response.user
        behanceDao.insertUser(user)
        val image = user.image
        if (image != null) {
            image.id = user.id
            image.userId = user.id
            behanceDao.insertImage(image)
        }
    }

    fun getUser(username: String): UserResponse {
        val user = behanceDao.getUserByName(username)
        val image = behanceDao.getImageFromUser(user.id)
        user.image = image
        return UserResponse(user)
    }

    interface StorageOwner {
        fun obtainStorage(): Storage?
    }
}
