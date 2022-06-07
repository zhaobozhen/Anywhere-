package com.absinthe.anywhere_.ui.shortcuts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.BuildConfig
import com.absinthe.anywhere_.adapter.shortcut.ThirdAppsShortcutAdapter
import com.absinthe.anywhere_.databinding.ActivityThirdAppsShortcutBinding
import com.absinthe.anywhere_.utils.ToastUtil
import rikka.widget.borderview.BorderView
import timber.log.Timber


class ThirdAppsShortcutActivity : AppBarActivity<ActivityThirdAppsShortcutBinding>() {

  override fun setViewBinding() = ActivityThirdAppsShortcutBinding.inflate(layoutInflater)

  override fun getToolBar() = binding.toolbar.toolBar

  override fun getAppBarLayout() = binding.toolbar.appBar

  private val mAdapter = ThirdAppsShortcutAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding.list.apply {
      adapter = mAdapter
      layoutManager = LinearLayoutManager(this@ThirdAppsShortcutActivity)
      borderVisibilityChangedListener =
        BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
          getAppBarLayout().isLifted = !top
        }
    }

    mAdapter.setList(getResolveInfoList())

    val resultLauncher = registerForActivityResult(OpenCreateShortcut()) {
      it?.let {
        runCatching {
          startActivity(it)
        }.onFailure { t ->
          ToastUtil.Toasty.show(this, t.message ?: "Can not open this activity")
        }
      } ?: run {
        Timber.d("resultLauncher: intent is null")
      }
    }

    mAdapter.setOnItemClickListener { _, _, position ->
      val info = mAdapter.data[position]
      resultLauncher.launch(info)
    }
  }

  private fun getResolveInfoList(): List<ResolveInfo> {
    return packageManager.queryIntentActivities(
      Intent(Intent.ACTION_CREATE_SHORTCUT), PackageManager.MATCH_DEFAULT_ONLY
    ).filter { it.activityInfo.applicationInfo.packageName != BuildConfig.APPLICATION_ID }
  }

  private class OpenCreateShortcut : ActivityResultContract<ResolveInfo, Intent?>() {
    @CallSuper
    override fun createIntent(context: Context, input: ResolveInfo): Intent {
      return Intent(Intent.ACTION_CREATE_SHORTCUT).apply {
        flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION
          or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
          or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
          or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION).inv()
        setClassName(
          input.activityInfo.applicationInfo.packageName,
          input.activityInfo.name
        )
      }
    }

    override fun getSynchronousResult(
      context: Context,
      input: ResolveInfo
    ): SynchronousResult<Intent?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
      return intent.takeIf { resultCode == Activity.RESULT_OK }
        ?.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT) as? Intent
    }
  }
}
