package com.elegion.test.behancer.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.elegion.test.behancer.AppDelegate
import com.elegion.test.behancer.R
import com.elegion.test.behancer.data.Storage
import com.elegion.test.behancer.data.Storage.StorageOwner

abstract class SingleFragmentActivity : AppCompatActivity(), StorageOwner, OnRefreshListener, RefreshOwner {

    private lateinit var refresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_swipe_container)

        refresher = findViewById(R.id.refresher)
        refresher.setOnRefreshListener(this)

        if (savedInstanceState == null){
//            openFragment(fragment = ProjectsFragment.newInstance())
            changeFragment(fragment = getFragment())
        }
    }

    protected abstract fun getFragment(): Fragment

    open fun changeFragment(fragment: Fragment) {
        val addToBackStack = supportFragmentManager.findFragmentById(R.id.fragmentContainer) != null
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }


    override fun onRefresh() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment is Refreshable) {
            (fragment as Refreshable).onRefreshData()
        } else {
            setRefreshState(false)
        }
    }

    override fun setRefreshState(refreshing: Boolean) {
        refresher.post { refresher.isRefreshing = refreshing }
    }

    override fun obtainStorage(): Storage {
        return (applicationContext as AppDelegate).storage
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}