package com.elegion.test.behancer.ui.projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elegion.test.behancer.R
import com.elegion.test.behancer.data.model.project.Project

class ProjectsAdapter(
    private val onItemClicked: (username: String) -> Unit
) : RecyclerView.Adapter<ProjectsHolder>() {

    private val projectList: MutableList<Project> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.li_projects, parent, false)
        return ProjectsHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectsHolder, position: Int) {
        val project: Project = projectList[position]
        holder.bind(project, onItemClicked)
    }

    override fun getItemCount(): Int {
        return projectList.size

    }

    fun addData(data: List<Project>, refresh: Boolean) {
        if (refresh) {
            projectList.clear()
        }
        projectList.addAll(data)
        notifyDataSetChanged()
    }
}