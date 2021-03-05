package com.elegion.test.behancer.ui.profile

import android.content.Context
import android.os.Bundle
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

    private lateinit var mRefreshOwner: RefreshOwner
    private lateinit var mErrorView: View
    private lateinit var mProfileView: View
    private lateinit var mUsername: String
    private lateinit var mStorage: Storage
    private lateinit var mDisposable: Disposable

    private lateinit var mProfileImage: ImageView
    private lateinit var mProfileName: TextView
    private lateinit var mProfileCreatedOn: TextView
    private lateinit var mProfileLocation: TextView

    companion object {
        const val PROFILE_KEY = "PROFILE_KEY"
        fun newInstance(args: Bundle?): ProfileFragment? {
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is StorageOwner) {
            mStorage = (context as StorageOwner).obtainStorage()!!
        }
        if (context is RefreshOwner) {
            mRefreshOwner = context
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
        mErrorView = view.findViewById(R.id.errorView)
        mProfileView = view.findViewById(R.id.view_profile)
        mProfileImage = view.findViewById(R.id.iv_profile)
        mProfileName = view.findViewById(R.id.tv_display_name_details)
        mProfileCreatedOn = view.findViewById(R.id.tv_created_on_details)
        mProfileLocation = view.findViewById(R.id.tv_location_details)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            mUsername = arguments!!.getString(PROFILE_KEY)!!
        }
        if (activity != null) {
            activity!!.title = mUsername
        }
        mProfileView.visibility = View.VISIBLE
        onRefreshData()
    }


    override fun onRefreshData() {
        getProfile()
    }

    private fun getProfile() {
        mDisposable = ApiUtils.getApiService().getUserInfo(mUsername)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { response: UserResponse? ->
                if (response != null) {
                    mStorage.insertUser(
                        response
                    )
                }
            }
            .onErrorReturn { throwable: Throwable ->
                if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable::class))
                mStorage.getUser(mUsername) else null
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { disposable: Disposable? ->
                mRefreshOwner.setRefreshState(
                    true
                )
            }
            .doFinally { mRefreshOwner.setRefreshState(false) }
            .subscribe(
                { response: UserResponse ->
                    mErrorView.visibility = View.GONE
                    mProfileView.visibility = View.VISIBLE
                    bind(response.user)
                }
            ) {
                mErrorView.visibility = View.VISIBLE
                mProfileView.visibility = View.GONE
            }
    }

    private fun bind(user: User) {
        Picasso.with(context)
            .load(user.image.photoUrl)
            .fit()
            .into(mProfileImage)
        mProfileName.text = user.displayName
        mProfileCreatedOn.text = DateUtils.format(user.createdOn)
        mProfileLocation.text = user.location
    }

}