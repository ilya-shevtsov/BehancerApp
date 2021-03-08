package com.elegion.test.behancer.ui.projects

import android.content.Context
import android.os.Bundle
import android.util.Log
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
    private var refreshOwner: RefreshOwner? = null
    private lateinit var error: View
    private var storage: Storage? = null
    private lateinit var projectsAdapter: ProjectsAdapter
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
        error = view.findViewById(R.id.errorView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            activity!!.setTitle(R.string.projects)
        }
        projectsAdapter = ProjectsAdapter { username ->
            onItemClick(username)
        }
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = projectsAdapter
        onRefreshData()
    }

    fun onItemClick(username: String?) {
        val arguments = Bundle()
        arguments.putString(ProfileFragment.PROFILE_KEY, username)

        val profileFragment = ProfileFragment.newInstance(arguments)
        val activity = activity as? SingleFragmentActivity
        if (activity != null) {
            activity.changeFragment(profileFragment)
        }else{
            Log.e("ProjectsFragment", "Parent activity is not SingleFragmentActivity")
        }

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
        getProjects()
    }

    private fun getProjects() {
        disposable = getApiService().getProjectList(BuildConfig.API_QUERY)
            .doOnSuccess { response: ProjectResponse ->
                storage?.insertProjects(
                    response
                )
            }
            .onErrorReturn { throwable: Throwable ->
                if (NETWORK_EXCEPTIONS.contains(throwable::class)
                ) storage?.projects else null
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                refreshOwner?.setRefreshState(
                    true
                )
            }
            .doFinally { refreshOwner?.setRefreshState(false) }
            .subscribe(
                { response: ProjectResponse ->
                    error.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    projectsAdapter.addData(response.projects, true)
                }
            ) {
                error.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
    }
}
