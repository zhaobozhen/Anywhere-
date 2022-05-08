package com.absinthe.anywhere_.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
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
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.database.PageEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.manager.ShellManager
import com.absinthe.anywhere_.utils.manager.ShizukuHelper
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PermissionUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class AnywhereViewModel(application: Application) : AndroidViewModel(application) {

  private val mRepository: AnywhereRepository = AnywhereApplication.sRepository
  val allAnywhereEntities: LiveData<List<AnywhereEntity>> = mRepository.allAnywhereEntities
  var shouldShowFab: MutableLiveData<Boolean> = MutableLiveData()
  var background: MutableLiveData<String> = MutableLiveData()
    private set

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

  fun setUpUrlScheme(context: Context, url: String = "") {
    val ae = AnywhereEntity().apply {
      appName = AnywhereType.Card.NEW_TITLE_MAP[AnywhereType.Card.URL_SCHEME]!!
      param1 = url
      type = AnywhereType.Card.URL_SCHEME
    }
    context.startActivity(Intent(context, EditorActivity::class.java).apply {
      putExtra(EXTRA_ENTITY, ae)
      putExtra(EXTRA_EDIT_MODE, false)
    })
  }

  fun startCollector(activity: Activity, listener: OnStartCollectorListener) {
    when (GlobalValues.workingMode) {
      Const.WORKING_MODE_URL_SCHEME -> {
        ToastUtil.makeText(R.string.toast_works_on_root_or_shizuku)
      }
      Const.WORKING_MODE_SHIZUKU -> {
        if (PermissionUtils.isGrantedDrawOverlays()) {
          if (ShizukuHelper.checkPermission(activity)) {
            listener.onStart()
          }
        } else {
          if (AppUtils.atLeastR()) {
            ToastUtil.makeText(R.string.toast_overlay_choose_anywhere)
          }
          PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
              if (ShizukuHelper.checkPermission(activity)) {
                listener.onStart()
              }
            }

            override fun onDenied() {}
          })
        }
      }
      Const.WORKING_MODE_ROOT -> {
        if (PermissionUtils.isGrantedDrawOverlays()) {
          if (Shell.isAppGrantedRoot() == true) {
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
    mRepository.allPageEntities.value?.let { pages ->
      val pe = PageEntity().apply {
        if (pages.isNotEmpty()) {
          var count = 1
          var t = "Page " + (pages.size + count++)
          while (pages.any { it.title == t }) {
            t = "Page " + (pages.size + count++)
          }
          title = t
          priority = pages.size + 1
        } else {
          title = AnywhereType.Category.DEFAULT_CATEGORY
          priority = 1
        }
        type = AnywhereType.Page.CARD_PAGE
      }
      mRepository.insertPage(pe)
    }
  }

  interface OnStartCollectorListener {
    fun onStart()
  }
}
