package com.elegion.test.behancer.ui.projects

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elegion.test.behancer.R
import com.elegion.test.behancer.data.model.project.Project
import com.elegion.test.behancer.utils.DateUtils.format
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer

class ProjectsHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        const val FIRST_OWNER_INDEX = 0
    }

    private val image: ImageView = containerView.findViewById(R.id.image)
    private val name: TextView = containerView.findViewById(R.id.tv_name)
    private val userName: TextView = containerView.findViewById(R.id.tv_username)
    private val publishedDate: TextView = containerView.findViewById(R.id.tv_published)

    fun bind(
        project: Project,
        onItemClicked: (username: String) -> Unit

    ) {
        Picasso.with(image.context).load(project.cover.photoUrl)
            .fit()
            .into(image)
        name.text = project.name
        userName.text = project.owners[FIRST_OWNER_INDEX].username
        publishedDate.text = format(project.publishedOn)
        containerView.setOnClickListener {
            onItemClicked.invoke(project.owners.first().username.orEmpty())
        }

    }

}