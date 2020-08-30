package com.absinthe.anywhere_.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.page.PageNode
import com.absinthe.anywhere_.adapter.page.PageTitleNode
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.database.AnywhereRepository
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.ActivityStackManager
import com.absinthe.anywhere_.utils.manager.ShellManager
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.checkShizukuOnWorking
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.isGrantShizukuPermission
import com.absinthe.anywhere_.utils.manager.ShizukuHelper.requestShizukuPermission
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PermissionUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.gson.Gson
import timber.log.Timber
import java.util.*

class AnywhereViewModel(application: Application) : AndroidViewModel(application) {

    val allAnywhereEntities: LiveData<List<AnywhereEntity>>
    var shouldShowFab: MutableLiveData<Boolean> = MutableLiveData()
    var background: MutableLiveData<String> = MutableLiveData()
        private set

    private val mRepository: AnywhereRepository = AnywhereApplication.sRepository

    init {
        allAnywhereEntities = mRepository.allAnywhereEntities
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

    fun setUpUrlScheme(url: String = "") {
        val context = ActivityStackManager.topActivity!! as Context
        val ae = AnywhereEntity.Builder().apply {
            appName = getApplication<Application>().getString(R.string.bsd_new_url_scheme_name)
            param1 = url
            type = AnywhereType.Card.URL_SCHEME
        }
        context.startActivity(Intent(context, EditorActivity::class.java).apply {
            putExtra(EXTRA_ENTITY, ae)
            putExtra(EXTRA_EDIT_MODE, false)
        })
    }

    fun startCollector(listener: OnStartCollectorListener) {
        val activity = ActivityStackManager.topActivity!!

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
                        ShellManager.acquireRoot()
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
                                ShellManager.acquireRoot()
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
                    title = AnywhereType.Category.DEFAULT_CATEGORY
                    priority = 1
                }
                type = AnywhereType.Page.CARD_PAGE
            }
            mRepository.insertPage(pe)
        }
    }

    fun addWebPage(uri: Uri, intent: Intent) {
        mRepository.allPageEntities.value?.let {
            val pe = PageEntity.Builder().apply {
                title = "Web Page " + (it.size + 1)
                priority = it.size + 1
                type = AnywhereType.Page.WEB_PAGE
                extra = uri.toString()
            }
            mRepository.insertPage(pe)
            AppUtils.takePersistableUriPermission(getApplication(), uri, intent)
        }
    }

    fun convertAnywhereTypeV2() {
        allAnywhereEntities.value?.let {
            val shortcutsList = mutableListOf<String>()
            for (entity in it) {
                //Remove shortcut and exported type from entity's type
                if (entity.type % 100 / 10 == 1) {
                    shortcutsList.add(entity.id)
                }
                entity.type = entity.type % 10

                //Transform extras to new structure
                if (entity.type == AnywhereType.Card.ACTIVITY) {
                    if (entity.param3.isNotEmpty()) {
                        val extraList = mutableListOf<ExtraBean.ExtraItem>()
                        for (eachLine in entity.param3.split("\n")) {
                            val splits = eachLine.split(" ")
                            if (splits.size == 3) {
                                extraList.add(ExtraBean.ExtraItem(splits[0], splits[1], splits[2]))
                            }
                        }
                        entity.param3 = Gson().toJson(ExtraBean("", "", "", extraList), ExtraBean::class.java)
                    }
                }

                AnywhereApplication.sRepository.update(entity)
                if (AppUtils.atLeastNMR1()) {
                    ShortcutsUtils.updateShortcut(entity)
                }
            }
            GlobalValues.shortcutsList = shortcutsList
        }
    }

    interface OnStartCollectorListener {
        fun onStart()
    }
}