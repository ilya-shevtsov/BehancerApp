package com.elegion.test.behancer.ui.projects

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elegion.test.behancer.BuildConfig
import com.elegion.test.behancer.R
import com.elegion.test.behancer.common.RefreshOwner
import com.elegion.test.behancer.common.Refreshable
import com.elegion.test.behancer.common.SingleFragmentActivity
import com.elegion.test.behancer.data.Storage
import com.elegion.test.behancer.data.Storage.StorageOwner
import com.elegion.test.behancer.data.model.project.ProjectResponse
import com.elegion.test.behancer.ui.profile.ProfileFragment
import com.elegion.test.behancer.utils.ApiUtils.Companion.NETWORK_EXCEPTIONS
import com.elegion.test.behancer.utils.ApiUtils.Companion.getApiService

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProjectsFragment : Fragment(), Refreshable {

    companion object {
        fun newInstance(): ProjectsFragment {
            return ProjectsFragment()
        }
    }

    private lateinit var recycler: RecyclerView
    private lateinit var errorView: View
    private val projectsAdapter: ProjectsAdapter =
        ProjectsAdapter(onItemClicked = { username ->
            onItemClick(username)
        })
    private var refreshOwner: RefreshOwner? = null
    private var storage: Storage? = null
    private var disposable: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is StorageOwner) {
            storage = (context as StorageOwner).obtainStorage()!!
        }
        if (context is RefreshOwner) {
            refreshOwner = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fr_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = projectsAdapter

        errorView = view.findViewById(R.id.errorView)
        activity!!.setTitle(R.string.projects)
        onRefreshData()
    }

    fun onItemClick(username: String) {
        val profileFragment = ProfileFragment.newInstance(username)
        val activity = activity as? SingleFragmentActivity
        activity?.changeFragment(profileFragment)

    }

    override fun onDetach() {
        storage = null
        refreshOwner = null
        if (disposable != null) {
            disposable?.dispose()
        }
        super.onDetach()
    }

    override fun onRefreshData() {
        getProjectList()
    }

    private fun getProjectList() {
        disposable = getApiService()
            .getProjectList(BuildConfig.API_QUERY)
            .doOnSuccess { response ->
                storage?.insertProjects(
                    response
                )
            }
            .onErrorReturn { throwable: Throwable ->
                if (NETWORK_EXCEPTIONS.contains(throwable::class)) {
                    storage?.projectListResponse
                } else {
                    null
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                refreshOwner?.setRefreshState(true) }
            .doFinally { refreshOwner?.setRefreshState(false) }
            .subscribe(
                { response: ProjectResponse ->
                    errorView.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    projectsAdapter.addData(response.projectList, true)
                }
            ) {
                errorView.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
    }
}
