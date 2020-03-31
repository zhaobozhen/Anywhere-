package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.databinding.ActivityAppListBinding
import com.absinthe.anywhere_.utils.AppUtils.getAppList
import com.absinthe.anywhere_.utils.UiUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityAppListBinding
    private var mAdapter: AppListAdapter = AppListAdapter(this, AppListAdapter.MODE_APP_LIST)
    private var isShowSystemApp = false

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        binding = ActivityAppListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setToolbar() {
        mToolbar = binding.toolbar.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isShowSystemApp = false
        initRecyclerView()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_list_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val showSystemApp = menu.findItem(R.id.show_system_app)
        val searchBar = searchView.findViewById<LinearLayout>(R.id.search_bar)

        searchView.apply {
            isQueryRefinementEnabled = true
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
            setOnQueryTextListener(this@AppListActivity)
        }
        searchBar.layoutTransition = LayoutTransition()

        // Bug of DayNight lib
        if (showSystemApp != null) {
            if (UiUtils.isDarkMode(this)) {
                showSystemApp.title = Html.fromHtml("<font color='#FFFFFF'>" + showSystemApp.title + "</font>")
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.show_system_app) {
            if (item.title.toString() == getString(R.string.menu_show_system_app)) {
                item.setTitle(R.string.menu_hide_system_app)
                if (UiUtils.isDarkMode(this)) {
                    item.title = Html.fromHtml("<font color='#FFFFFF'>" + item.title + "</font>")
                }
                isShowSystemApp = true
            } else {
                item.setTitle(R.string.menu_show_system_app)
                if (UiUtils.isDarkMode(this)) {
                    item.title = Html.fromHtml("<font color='#FFFFFF'>" + item.title + "</font>")
                }
                isShowSystemApp = false
            }
            initData(isShowSystemApp)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        super.initView()
        binding.srlAppList.setOnRefreshListener { initData(isShowSystemApp) }
    }

    private fun initRecyclerView() {
        binding.rvAppList.layoutManager = WrapContentLinearLayoutManager(this)
        mAdapter = AppListAdapter(this, AppListAdapter.MODE_APP_LIST)
        binding.rvAppList.adapter = mAdapter
        binding.rvAppList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition = if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
                binding.srlAppList.isEnabled = topRowVerticalPosition >= 0
            }
        })
    }

    private fun initData(showSystem: Boolean = false) {
        binding.srlAppList.isRefreshing = true

        GlobalScope.launch(Dispatchers.IO) {
            val list = getAppList(packageManager, showSystem).toMutableList()
            withContext(Dispatchers.Main) {
                mAdapter.setList(list)
                binding.srlAppList.isRefreshing = false
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mAdapter.filter.filter(newText)
        return false
    }
}