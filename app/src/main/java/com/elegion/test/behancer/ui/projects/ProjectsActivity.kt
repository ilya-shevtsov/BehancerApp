package com.elegion.test.behancer.ui.projects

import androidx.fragment.app.Fragment
import com.elegion.test.behancer.common.SingleFragmentActivity

class ProjectsActivity : SingleFragmentActivity() {
    override fun getFragment(): Fragment {
        return ProjectsFragment.newInstance()
    }
}