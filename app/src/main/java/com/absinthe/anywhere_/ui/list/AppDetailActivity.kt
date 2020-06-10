package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.applist.AppListDiffCallback
import com.absinthe.anywhere_.adapter.applist.MODE_APP_DETAIL
import com.absinthe.anywhere_.adapter.manager.WrapContentLinearLayoutManager
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.databinding.ActivityAppDetailBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.view.editor.AnywhereEditor
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AppDetailActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var mBinding: ActivityAppDetailBinding
    private var mAdapter: AppListAdapter = AppListAdapter(MODE_APP_DETAIL)
    private val mItems = mutableListOf<AppListBean>()

    init {
        isPaddingToolbar = true
    }

    override fun setViewBinding() {
        mBinding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun setToolbar() {
        mToolbar = mBinding.toolbar.toolbar

        intent?.let {
            mToolbar?.title = it.getStringExtra(Const.INTENT_EXTRA_APP_NAME)
        } ?: finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            initRecyclerView()
            it.getStringExtra(Const.INTENT_EXTRA_PKG_NAME)?.let { packageName ->
                initData(packageName)
            }
        } ?: finish()
    }

    private fun initRecyclerView() {
        mAdapter.setDiffCallback(AppListDiffCallback())
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)
            var exported = 0

            if (AppUtils.isActivityExported(this, ComponentName(item.packageName,
                            item.className))) {
                exported = 100
            }
            val ae = AnywhereEntity.Builder().apply {
                appName = item.appName
                param1 = item.packageName
                param2 = item.className.removePrefix(item.packageName)
                type = AnywhereType.ACTIVITY + exported
            }

            val editor = AnywhereEditor(this)
                    .item(ae)
                    .isEditorMode(false)
                    .isShortcut(false)
                    .build()
            editor.show()
        }
        mBinding.srlAppDetail.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorPrimary)
            setColorSchemeColors(Color.WHITE)
        }
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

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                //Get all activity classes in the AndroidManifest.xml
                val packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES)

                packageInfo.activities?.let { acivities ->
                    Timber.d("Found %d activity in the AndroidManifest.xml", acivities.size)

                    for (ai in acivities) {
                        val bean = AppListBean().apply {
                            appName = if (ai.exported) {
                                "${ai.loadLabel(packageManager)} (Exported)"
                            } else {
                                ai.loadLabel(packageManager).toString()
                            }
                            packageName = pkgName
                            className = ai.name
                            type = -1
                        }
                        mItems.add(bean)
                        Timber.d(ai.name, "...OK")
                    }

                    mItems.sortByDescending { AppUtils.isActivityExported(Utils.getApp(), ComponentName(it.packageName, it.className)) }
                }
            } catch (exception: PackageManager.NameNotFoundException) {
                exception.printStackTrace()
            } catch (exception: RuntimeException) {
                exception.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                if (mItems.isEmpty()) {
                    mBinding.vfContainer.displayedChild = 1
                } else {
                    mAdapter.setDiffNewData(mItems)
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
        searchView.findViewById<LinearLayout>(R.id.search_bar)?.layoutTransition = LayoutTransition()

        searchView.apply {
            isSubmitButtonEnabled = true // Display "Start search" button
            isQueryRefinementEnabled = true
            setIconifiedByDefault(false)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(this@AppDetailActivity)
        }

        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filter = mItems.filter {
            it.appName.contains(newText) || it.packageName.contains(newText)
        }
        mAdapter.setDiffNewData(filter.toMutableList())
        return false
    }
}