package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AppBarActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.ActivityLabBinding
import com.absinthe.anywhere_.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rikka.recyclerview.fixEdgeEffect
import rikka.widget.borderview.BorderRecyclerView
import rikka.widget.borderview.BorderView

class LabActivity : AppBarActivity<ActivityLabBinding>() {

  override fun setViewBinding() = ActivityLabBinding.inflate(layoutInflater)

  override fun getToolBar() = binding.toolbar.toolBar

  override fun getAppBarLayout() = binding.toolbar.appBar

  class LabFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.settings_lab, rootKey)

      findPreference<SwitchPreference>(Const.PREF_TRANS_ICON)?.apply {
        setOnPreferenceChangeListener { _, newValue ->
          GlobalScope.launch(Dispatchers.IO) {
            delay(500)
            AppUtils.setTransparentLauncherIcon(newValue as Boolean)
          }
          true
        }
      }
      findPreference<SwitchPreference>(Const.PREF_EDITOR_ENTRY_ANIM)?.apply {
        setOnPreferenceChangeListener { _, newValue ->
          GlobalValues.editorEntryAnim = newValue as Boolean
          true
        }
      }
      findPreference<SwitchPreference>(Const.PREF_DEPRECATED_SC_CREATING_METHOD)?.apply {
        setOnPreferenceChangeListener { _, newValue ->
          GlobalValues.deprecatedScCreatingMethod = newValue as Boolean
          true
        }
      }
      findPreference<SwitchPreference>(Const.PREF_SHOW_DEFREEZING_TOAST)?.apply {
        setOnPreferenceChangeListener { _, newValue ->
          GlobalValues.showDefreezingToast = newValue as Boolean
          true
        }
      }
    }

    override fun onCreateRecyclerView(
      inflater: LayoutInflater,
      parent: ViewGroup,
      savedInstanceState: Bundle?
    ): RecyclerView {
      val recyclerView =
        super.onCreateRecyclerView(inflater, parent, savedInstanceState) as BorderRecyclerView
      recyclerView.fixEdgeEffect()
      recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
      recyclerView.isVerticalScrollBarEnabled = false

      val lp = recyclerView.layoutParams
      if (lp is FrameLayout.LayoutParams) {
        lp.rightMargin =
          recyclerView.context.resources.getDimension(rikka.material.R.dimen.rd_activity_horizontal_margin)
            .toInt()
        lp.leftMargin = lp.rightMargin
      }

      recyclerView.borderViewDelegate.borderVisibilityChangedListener =
        BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
          (activity as LabActivity?)?.getAppBarLayout()?.isLifted = !top
        }

      return recyclerView
    }
  }

}
