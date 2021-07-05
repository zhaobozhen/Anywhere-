package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.applist.AppListDiffCallback
import com.absinthe.anywhere_.adapter.applist.MODE_APP_LIST
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityAppListBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils.getAppList
import com.absinthe.libraries.utils.extensions.dp
import com.absinthe.libraries.utils.manager.SystemBarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import rikka.widget.borderview.BorderView

const val EXTRA_APP_LIST_ENTRY_MODE = "EXTRA_APP_LIST_ENTRY_MODE"
const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
const val MODE_NORMAL = 0
const val MODE_SELECT = 1

class AppListActivity : AppBarActivity<ActivityAppListBinding>(), SearchView.OnQueryTextListener {

    private var mItems = mutableListOf<AppListBean>()
    private var initDataJob: Job? = null
    private var isDataInit = false
    private val mAdapter: AppListAdapter = AppListAdapter(MODE_APP_LIST)
    private val entryMode by lazy { intent.getIntExtra(EXTRA_APP_LIST_ENTRY_MODE, MODE_NORMAL) }

    override fun setViewBinding() = ActivityAppListBinding.inflate(layoutInflater)

    override fun getToolBar() = binding.toolbar.toolBar

    override fun getAppBarLayout() = binding.toolbar.appBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            setTitle(
                if (GlobalValues.showSystemApps) {
                    R.string.menu_hide_system_app
                } else {
                    R.string.menu_show_system_app
                }
            )
        }

        if (!isDataInit) {
            menu.findItem(R.id.search).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.show_system_app) {
            GlobalValues.showSystemApps = if (item.title.toString() == getString(R.string.menu_show_system_app)) {
                item.setTitle(R.string.menu_hide_system_app)
                true
            } else {
                item.setTitle(R.string.menu_show_system_app)
                false
            }

            initData(GlobalValues.showSystemApps)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        super.initView()
        binding.extendedFab.apply {
            post {
                (layoutParams as CoordinatorLayout.LayoutParams).setMargins(0, 0, 16.dp, 16.dp + SystemBarManager.navigationBarSize)
            }

            setOnClickListener {
                val ae = AnywhereEntity().apply {
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

            if (entryMode == MODE_NORMAL) {
                val intent = Intent(this, AppDetailActivity::class.java).apply {
                    putExtra(Const.INTENT_EXTRA_APP_NAME, item.appName)
                    putExtra(Const.INTENT_EXTRA_PKG_NAME, item.packageName)
                }
                startActivity(intent)
            } else {
                val intent = Intent().apply {
                    putExtra(EXTRA_PACKAGE_NAME, item.packageName)
                }
                setResult(Const.REQUEST_CODE_APP_LIST_SELECT, intent)
                finish()
            }
        }

        binding.list.apply {
            layoutManager = WrapContentLinearLayoutManager(this@AppListActivity)
            adapter = mAdapter
            borderVisibilityChangedListener =
                BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                    appBar?.setRaised(!top)
                }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (!recyclerView.canScrollVertically(-1) && !binding.extendedFab.isExtended) {
                            binding.extendedFab.extend()
                        } else {
                            binding.extendedFab.shrink()
                        }
                    }
                }
            })
            FastScrollerBuilder(this).useMd2Style().build()
        }
    }

    private fun initData(showSystem: Boolean = GlobalValues.showSystemApps) {
        initDataJob?.cancel()
        binding.progressHorizontal.show()

        initDataJob = lifecycleScope.launch(Dispatchers.IO) {
            mItems = getAppList(packageManager, showSystem).toMutableList()
            isDataInit = true

            withContext(Dispatchers.Main) {
                mAdapter.setDiffNewData(mItems)
                binding.progressHorizontal.hide()
                getToolBar().menu?.findItem(R.id.search)?.isVisible = true
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