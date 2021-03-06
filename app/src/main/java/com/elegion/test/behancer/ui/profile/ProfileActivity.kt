package com.elegion.test.behancer.ui.profile

import androidx.fragment.app.Fragment
import com.elegion.test.behancer.common.SingleFragmentActivity


class ProfileActivity : SingleFragmentActivity() {

    companion object {
        const val USERNAME_KEY = "USERNAME_KEY"
    }

    override fun getFragment(): Fragment {
        if (intent != null) {
            return ProfileFragment.newInstance(intent.getBundleExtra(USERNAME_KEY))!!
        }
        throw IllegalStateException("getIntent cannot be null")
    }
}