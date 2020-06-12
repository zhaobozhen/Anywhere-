package com.absinthe.anywhere_.viewmodel

import android.app.Activity
import android.app.ActivityOptions
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.page.PageNode
import com.absinthe.anywhere_.adapter.page.PageTitleNode
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.database.AnywhereRepository
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_COLOR
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.checkShizukuOnWorking
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.isGrantShizukuPermission
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.requestShizukuPermission
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PermissionUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import timber.log.Timber
import java.util.*

class AnywhereViewModel(application: Application) : AndroidViewModel(application) {

    val allAnywhereEntities: LiveData<List<AnywhereEntity>>
    var shouldShowFab: MutableLiveData<Boolean> = MutableLiveData()

    private val mRepository: AnywhereRepository = AnywhereApplication.sRepository
    private var mBackground: MutableLiveData<String> = MutableLiveData()
    private var mFragment: MutableLiveData<Fragment> = MutableLiveData()

    init {
        allAnywhereEntities = mRepository.allAnywhereEntities
    }

    val background: MutableLiveData<String>
        get() {
            return mBackground
        }

    val fragment: MutableLiveData<Fragment>
        get() {
            return mFragment
        }

    fun insert(ae: AnywhereEntity) {
        mRepository.insert(ae)
    }

    fun update(ae: AnywhereEntity) {
        mRepository.update(ae)
    }

    fun delete(ae: AnywhereEntity) {
        mRepository.delete(ae)
    }

    fun getEntity(title: String): PageTitleNode {
        val pageNodeList: MutableList<BaseNode> = ArrayList()

        pageNodeList.add(PageNode().apply {
            this.title = title
        })

        return PageTitleNode(pageNodeList, title).apply {
            isExpanded = (title == GlobalValues.category)
        }
    }

    fun setUpUrlScheme(context: Context, view: View, url: String = "") {
        val ae = AnywhereEntity.Builder().apply {
            appName = getApplication<Application>().getString(R.string.bsd_new_url_scheme_name)
            param1 = url
            type = AnywhereType.URL_SCHEME
        }
        val options = ActivityOptions.makeSceneTransitionAnimation(
                context as BaseActivity,
                view,
                context.getString(R.string.trans_item_container)
        )
        context.startActivity(Intent(context, EditorActivity::class.java).apply {
            putExtra(EXTRA_ENTITY, ae)
            putExtra(EXTRA_EDIT_MODE, false)
            putExtra(EXTRA_COLOR, ContextCompat.getColor(context, R.color.colorPrimary))
        }, options.toBundle())
    }

    fun openImageEditor(context: Context, view: View) {
        val ae = AnywhereEntity.Builder().apply {
            appName = "New Image"
            type = AnywhereType.IMAGE
        }

        val options = ActivityOptions.makeSceneTransitionAnimation(
                context as BaseActivity,
                view,
                context.getString(R.string.trans_item_container)
        )
        context.startActivity(Intent(context, EditorActivity::class.java).apply {
            putExtra(EXTRA_ENTITY, ae)
            putExtra(EXTRA_EDIT_MODE, false)
            putExtra(EXTRA_COLOR, ContextCompat.getColor(context, R.color.colorPrimary))
        }, options.toBundle())
    }

    fun openShellEditor(context: Context, view: View) {
        val ae = AnywhereEntity.Builder().apply {
            appName = "New Shell"
            type = AnywhereType.SHELL
        }

        val options = ActivityOptions.makeSceneTransitionAnimation(
                context as BaseActivity,
                view,
                context.getString(R.string.trans_item_container)
        )
        context.startActivity(Intent(context, EditorActivity::class.java).apply {
            putExtra(EXTRA_ENTITY, ae)
            putExtra(EXTRA_EDIT_MODE, false)
            putExtra(EXTRA_COLOR, ContextCompat.getColor(context, R.color.colorPrimary))
        }, options.toBundle())
    }

    fun openSwitchShellEditor(context: Context, view: View) {
        val ae = AnywhereEntity.Builder().apply {
            appName = "New Switch Shell"
            type = AnywhereType.SWITCH_SHELL
        }
        val options = ActivityOptions.makeSceneTransitionAnimation(
                context as BaseActivity,
                view,
                context.getString(R.string.trans_item_container)
        )
        context.startActivity(Intent(context, EditorActivity::class.java).apply {
            putExtra(EXTRA_ENTITY, ae)
            putExtra(EXTRA_EDIT_MODE, false)
            putExtra(EXTRA_COLOR, ContextCompat.getColor(context, R.color.colorPrimary))
        }, options.toBundle())
    }

    fun startCollector(activity: Activity, listener: OnStartCollectorListener) {
        when (GlobalValues.workingMode) {
            Const.WORKING_MODE_URL_SCHEME -> {
                ToastUtil.makeText(R.string.toast_works_on_root_or_shizuku)
            }
            Const.WORKING_MODE_SHIZUKU -> {
                if (PermissionUtils.isGrantedDrawOverlays()) {
                    if (checkShizukuOnWorking(activity) && isGrantShizukuPermission) {
                        listener.onStart()
                    } else {
                        requestShizukuPermission()
                    }
                } else {
                    if (AppUtils.atLeastR()) {
                        ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
                    }
                    PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            if (checkShizukuOnWorking(activity) && isGrantShizukuPermission) {
                                listener.onStart()
                            } else {
                                requestShizukuPermission()
                            }
                        }

                        override fun onDenied() {}
                    })
                }
            }
            Const.WORKING_MODE_ROOT -> {
                if (PermissionUtils.isGrantedDrawOverlays()) {
                    if (DeviceUtils.isDeviceRooted()) {
                        listener.onStart()
                    } else {
                        Timber.d("ROOT permission denied.")
                        ToastUtil.makeText(R.string.toast_root_permission_denied)
                        AppUtils.acquireRootPerm(activity)
                    }
                } else {
                    if (AppUtils.atLeastR()) {
                        ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
                    }
                    PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            if (DeviceUtils.isDeviceRooted()) {
                                listener.onStart()
                            } else {
                                Timber.d("ROOT permission denied.")
                                ToastUtil.makeText(R.string.toast_root_permission_denied)
                                AppUtils.acquireRootPerm(activity)
                            }
                        }

                        override fun onDenied() {}
                    })
                }
            }
        }
    }

    fun addPage() {
        mRepository.allPageEntities.value?.let {
            val pe = PageEntity.Builder().apply {
                if (it.isNotEmpty()) {
                    title = "Page " + (it.size + 1)
                    priority = it.size + 1
                } else {
                    title = AnywhereType.DEFAULT_CATEGORY
                    priority = 1
                }
                type = AnywhereType.CARD_PAGE
            }
            mRepository.insertPage(pe)
        }
    }

    fun addWebPage(uri: Uri, intent: Intent) {
        mRepository.allPageEntities.value?.let {
            val pe = PageEntity.Builder().apply {
                title = "Web Page " + (it.size + 1)
                priority = it.size + 1
                type = AnywhereType.WEB_PAGE
                extra = uri.toString()
            }
            mRepository.insertPage(pe)
            AppUtils.takePersistableUriPermission(getApplication(), uri, intent)
        }
    }

    interface OnStartCollectorListener {
        fun onStart()
    }
}