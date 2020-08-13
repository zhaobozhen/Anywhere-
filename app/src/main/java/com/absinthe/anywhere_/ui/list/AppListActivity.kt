package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.applist.AppListDiffCallback
import com.absinthe.anywhere_.adapter.applist.MODE_APP_LIST
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.databinding.ActivityAppListBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils.getAppList
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.blankj.utilcode.util.ConvertUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityAppListBinding
    private var mItems = mutableListOf<AppListBean>()
    private var isShowSystemApp = false
    private var initDataJob: Job? = null
    private val mAdapter: AppListAdapter = AppListAdapter(MODE_APP_LIST)

    override fun setViewBinding() {
        isPaddingToolbar = true
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
        searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_bar)?.layoutTransition = LayoutTransition()

        searchView.apply {
            isQueryRefinementEnabled = true
            setIconifiedByDefault(false)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(this@AppListActivity)
        }

        // Bug of DayNight lib
        showSystemApp?.apply {
            if (StatusBarUtil.isDarkMode(this@AppListActivity)) {
                title = SpannableStringBuilder(title).apply {
                    setSpan(ForegroundColorSpan(Color.WHITE), 0, title.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
        }
        menu.findItem(R.id.search).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.show_system_app) {
            isShowSystemApp = if (item.title.toString() == getString(R.string.menu_show_system_app)) {
                item.setTitle(R.string.menu_hide_system_app)
                true
            } else {
                item.setTitle(R.string.menu_show_system_app)
                false
            }

            if (StatusBarUtil.isDarkMode(this)) {
                item.title = SpannableStringBuilder(item.title).apply {
                    setSpan(ForegroundColorSpan(Color.WHITE), 0, item.title.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            initData(isShowSystemApp)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        super.initView()
        binding.srlAppList.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorPrimary)
            setColorSchemeColors(Color.WHITE)
            setOnRefreshListener { initData(isShowSystemApp) }
        }
        binding.extendedFab.apply {
            (layoutParams as CoordinatorLayout.LayoutParams)
                    .setMargins(
                            0,
                            0,
                            ConvertUtils.dp2px(16f),
                            ConvertUtils.dp2px(16f) + StatusBarUtil.getNavBarHeight()
                    )

            setOnClickListener {
                val ae = AnywhereEntity.Builder().apply {
                    appName = AnywhereType.Card.NEW_TITLE_MAP[AnywhereType.Card.ACTIVITY]!!
                    type = AnywhereType.Card.ACTIVITY
                }
                startActivity(Intent(this@AppListActivity, EditorActivity::class.java).apply {
                    putExtra(EXTRA_ENTITY, ae)
                    putExtra(EXTRA_EDIT_MODE, false)
                })
            }
        }
    }

    private fun initRecyclerView() {
        mAdapter.setDiffCallback(AppListDiffCallback())
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)
            val intent = Intent(this, AppDetailActivity::class.java).apply {
                putExtra(Const.INTENT_EXTRA_APP_NAME, item.appName)
                putExtra(Const.INTENT_EXTRA_PKG_NAME, item.packageName)
            }
            startActivity(intent)
        }

        binding.rvAppList.apply {
            layoutManager = WrapContentLinearLayoutManager(this@AppListActivity)
            adapter = mAdapter
            setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + StatusBarUtil.getNavBarHeight())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val topRowVerticalPosition = if (recyclerView.childCount == 0) {
                        0
                    } else {
                        recyclerView.getChildAt(0).top
                    }
                    binding.srlAppList.isEnabled = topRowVerticalPosition >= 0

                    if (!recyclerView.canScrollVertically(-1) && binding.extendedFab.isExtended) {
                        binding.extendedFab.shrink()
                    } else if (dy < 0 && !binding.extendedFab.isExtended) {
                        binding.extendedFab.extend()
                    }
                }
            })
        }
    }

    private fun initData(showSystem: Boolean = false) {
        initDataJob?.cancel()
        binding.srlAppList.isRefreshing = true

        initDataJob = lifecycleScope.launch(Dispatchers.IO) {
            mItems = getAppList(packageManager, showSystem).toMutableList()

            withContext(Dispatchers.Main) {
                mAdapter.setDiffNewData(mItems)
                binding.srlAppList.isRefreshing = false
                
                var menu: Menu? = binding.toolbar.toolbar.menu
                while (menu == null) {
                    menu = binding.toolbar.toolbar.menu
                }
                menu.findItem(R.id.search).isVisible = true
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filter = mItems.filter {
            it.appName.contains(newText, ignoreCase = true) || it.packageName.contains(newText, ignoreCase = true)
        }
        mAdapter.setDiffNewData(filter.toMutableList())
        return false
    }
}