package com.elegion.test.behancer.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.elegion.test.behancer.R
import com.elegion.test.behancer.common.RefreshOwner
import com.elegion.test.behancer.common.Refreshable
import com.elegion.test.behancer.data.Storage
import com.elegion.test.behancer.data.Storage.StorageOwner
import com.elegion.test.behancer.data.model.user.User
import com.elegion.test.behancer.data.model.user.UserResponse
import com.elegion.test.behancer.utils.ApiUtils
import com.elegion.test.behancer.utils.DateUtils
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProfileFragment : Fragment(), Refreshable {

    private lateinit var refreshOwner: RefreshOwner
    private lateinit var error: View
    private lateinit var profile: View
    private lateinit var username: String
    private lateinit var storage: Storage
    private lateinit var disposable: Disposable

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileCreatedOn: TextView
    private lateinit var profileLocation: TextView

    companion object {
        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            fragment.username = username
            return fragment
        }
    }

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
        return inflater.inflate(R.layout.fr_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        error = view.findViewById(R.id.errorView)
        profile = view.findViewById(R.id.view_profile)
        profileImage = view.findViewById(R.id.iv_profile)
        profileName = view.findViewById(R.id.tv_display_name_details)
        profileCreatedOn = view.findViewById(R.id.tv_created_on_details)
        profileLocation = view.findViewById(R.id.tv_location_details)

        activity?.title = username
        profile.visibility = View.VISIBLE
        onRefreshData()
    }

    override fun onRefreshData() {
        getProfile()
    }

    private fun getProfile() {
        disposable = ApiUtils.getApiService().getUserInfo(username)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { response: UserResponse? ->
                if (response != null) {
                    storage.insertUser(
                        response
                    )
                }
            }
            .onErrorReturn { throwable: Throwable ->
                if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable::class))
                    storage.getUser(username) else null
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                refreshOwner.setRefreshState(
                    true
                )
            }
            .doFinally { refreshOwner.setRefreshState(false) }
            .subscribe(
                { response: UserResponse ->
                    error.visibility = View.GONE
                    profile.visibility = View.VISIBLE
                    bind(response.user)
                }
            ) {
                Log.e("ProfileFragment", "This is what went wrong $it")
                error.visibility = View.VISIBLE
                profile.visibility = View.GONE
            }
    }

    private fun bind(user: User) {
        val url = user.image?.photoUrl
        if (url != null) {
            Picasso.with(context)
                .load(url)
                .fit()
                .into(profileImage)
        }
        profileName.text = user.displayName
        profileCreatedOn.text = DateUtils.format(user.createdOn)
        profileLocation.text = user.location
    }
}
