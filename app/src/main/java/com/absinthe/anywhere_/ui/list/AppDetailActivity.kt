package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.databinding.ActivityAppDetailBinding
import com.absinthe.anywhere_.model.AppListBean
import com.absinthe.anywhere_.utils.AppUtils.getActivitiesClass
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.UiUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AppDetailActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var mBinding: ActivityAppDetailBinding
    private var mAdapter: AppListAdapter = AppListAdapter(this, AppListAdapter.MODE_APP_DETAIL)

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        mBinding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar

        if (intent == null) {
            finish()
        } else {
            mToolbar?.title = intent.getStringExtra(Const.INTENT_EXTRA_APP_NAME)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null) {
            finish()
        } else {
            initRecyclerView()
            intent.getStringExtra(Const.INTENT_EXTRA_PKG_NAME)?.let {
                initData(it)
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.rvAppList.apply {
            layoutManager = WrapContentLinearLayoutManager(this@AppDetailActivity)
            adapter = mAdapter
            setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + StatusBarUtil.getNavBarHeight())
        }
    }

    private fun initData(pkgName: String) {
        mBinding.srlAppDetail.apply {
            isEnabled = true
            isRefreshing = true
        }

        GlobalScope.launch(Dispatchers.IO) {
            val list: MutableList<AppListBean> = ArrayList()
            val clazz = getActivitiesClass(this@AppDetailActivity, pkgName)

            for (s in clazz) {
                var appName = UiUtils.getActivityLabel(this@AppDetailActivity, ComponentName(pkgName, s))
                if (UiUtils.isActivityExported(this@AppDetailActivity, ComponentName(pkgName, s))) {
                    appName = "$appName (Exported)"
                }
                list.add(AppListBean(appName, pkgName, s, -1))
            }

            withContext(Dispatchers.Main) {
                if (list.isEmpty()) {
                    mBinding.vfContainer.displayedChild = 1
                } else {
                    list.sortByDescending { UiUtils.isActivityExported(Utils.getApp(), ComponentName(it.packageName, it.className)) }
                    mAdapter.setList(list)
                    mBinding.vfContainer.displayedChild = 0
                }

                mBinding.srlAppDetail.apply {
                    isEnabled = false
                    isRefreshing = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_detail_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchBar = searchView.findViewById<LinearLayout>(R.id.search_bar)

        searchView.apply {
            isSubmitButtonEnabled = true // Display "Start search" button
            isQueryRefinementEnabled = true
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
            setOnQueryTextListener(this@AppDetailActivity)
        }

        searchBar.layoutTransition = LayoutTransition()
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mAdapter.filter.filter(newText)
        return false
    }
}