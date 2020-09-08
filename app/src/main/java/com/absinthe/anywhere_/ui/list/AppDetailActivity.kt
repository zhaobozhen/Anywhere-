package com.absinthe.anywhere_.ui.list

import android.animation.LayoutTransition
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.StatusBarUtil
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.libraries.utils.extensions.addPaddingBottom
import com.blankj.utilcode.util.ActivityUtils
import com.catchingnow.icebox.sdk_client.IceBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppDetailActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var mBinding: ActivityAppDetailBinding
    private var mAdapter: AppListAdapter = AppListAdapter(MODE_APP_DETAIL)
    private var isDataInit = false
    private val mItems = mutableListOf<AppListBean>()

    override fun setViewBinding() {
        isPaddingToolbar = true
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
            val ae = AnywhereEntity.Builder().apply {
                appName = item.appName
                param1 = item.packageName
                param2 = item.className.removePrefix(item.packageName)
                type = AnywhereType.Card.ACTIVITY
            }
            startActivity(Intent(this, EditorActivity::class.java).apply {
                putExtra(EXTRA_ENTITY, ae)
                putExtra(EXTRA_EDIT_MODE, false)
            })
        }
        mBinding.srlAppDetail.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorPrimary)
            setColorSchemeColors(Color.WHITE)
        }
        mBinding.rvAppList.apply {
            layoutManager = WrapContentLinearLayoutManager(this@AppDetailActivity)
            adapter = mAdapter
            addPaddingBottom(StatusBarUtil.getNavBarHeight())
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

                val isFrozen = try {
                    IceBox.getAppEnabledSetting(this@AppDetailActivity, pkgName) != 0 //0 means available
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }

                val appPackageInfo = if (!isFrozen) {
                    packageInfo
                } else {
                    val pmFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PackageManager.MATCH_DISABLED_COMPONENTS
                    } else {
                        PackageManager.GET_DISABLED_COMPONENTS
                    }
                    packageManager.getPackageArchiveInfo(packageInfo.applicationInfo.sourceDir, PackageManager.GET_ACTIVITIES or pmFlag)
                }

                appPackageInfo?.activities?.let { activities ->
                    for (ai in activities) {
                        val launchActivity = ActivityUtils.getLauncherActivity(ai.packageName)
                        val bean = AppListBean(
                                id = ai.name,
                                appName = when {
                                    ai.name == launchActivity -> {
                                        "${ai.loadLabel(packageManager)} (Launcher)"
                                    }
                                    ai.exported -> {
                                        "${ai.loadLabel(packageManager)} (Exported)"
                                    }
                                    else -> {
                                        ai.loadLabel(packageManager).toString()
                                    }
                                },
                                isExported = ai.exported,
                                packageName = pkgName,
                                className = ai.name,
                                icon = ai.loadIcon(packageManager),
                                type = AnywhereType.Card.ACTIVITY
                        )
                        mItems.add(bean)
                    }
                }
                mItems.sortByDescending { it.isExported }
                isDataInit = true
            } catch (exception: PackageManager.NameNotFoundException) {
                exception.printStackTrace()
            } catch (exception: RuntimeException) {
                exception.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                if (mItems.isEmpty()) {
                    mBinding.vfContainer.displayedChild = 1
                } else {
                    val launchActivity = ActivityUtils.getLauncherActivity(mItems[0].packageName)
                    mItems.find { it.className == launchActivity }?.let {
                        it.isLaunchActivity = true
                    }

                    mAdapter.setDiffNewData(mItems)
                    mBinding.vfContainer.displayedChild = 0
                }

                mBinding.srlAppDetail.apply {
                    isEnabled = false
                    isRefreshing = false
                    mBinding.toolbar.toolbar.menu?.findItem(R.id.search)?.isVisible = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_detail_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_bar)?.layoutTransition = LayoutTransition()

        searchView.apply {
            isSubmitButtonEnabled = true // Display "Start search" button
            isQueryRefinementEnabled = true
            setIconifiedByDefault(false)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(this@AppDetailActivity)
        }

        if (!isDataInit) {
            menu.findItem(R.id.search).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.open_detail) {
            try {
                startActivity(Intent().apply {
                    action = "android.intent.action.SHOW_APP_INFO"
                    putExtra("android.intent.extra.PACKAGE_NAME", intent.getStringExtra(Const.INTENT_EXTRA_PKG_NAME))
                })
            } catch (e: ActivityNotFoundException) {
                ToastUtil.makeText(R.string.toast_no_react_show_info)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filter = mItems.filter {
            it.appName.contains(newText, ignoreCase = true) || it.className.contains(newText, ignoreCase = true)
        }
        mAdapter.setDiffNewData(filter.toMutableList())
        return false
    }

}