package com.absinthe.anywhere_.ui.editor.impl

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.card.ExtrasAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorAnywhereBinding
import com.absinthe.anywhere_.databinding.LayoutHeaderExtrasBinding
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.TYPE_STRING
import com.absinthe.anywhere_.model.database.setExecWithRoot
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class AnywhereEditorFragment : BaseEditorFragment() {

  private lateinit var binding: EditorAnywhereBinding
  private val adapter = ExtrasAdapter()

  override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
    binding = EditorAnywhereBinding.inflate(inflater, container, false)
    return binding.root
  }

  @SuppressLint("SetTextI18n")
  override fun initView() {
    item.let {
      val extraBean: ExtraBean? = try {
        Gson().fromJson(it.param3, ExtraBean::class.java)
      } catch (e: JsonSyntaxException) {
        null
      }

      adapter.apply {
        animationEnable = true
        val headerBinding = LayoutHeaderExtrasBinding.inflate(layoutInflater)
        addHeaderView(headerBinding.root)

        headerBinding.ibAdd.setOnClickListener {
          val item = ExtraBean.ExtraItem(TYPE_STRING, "", "")
          addData(0, item)
        }
        setOnItemChildClickListener { _, view, position ->
          if (view.id == R.id.ib_delete) {
            if (data.size > 0 && position < data.size) {
              removeAt(position)
            }
          }
        }
      }

      binding.apply {
        tietAppName.setText(it.appName)
        tietPackageName.setText(it.param1)
        tietClassName.setText(it.param2)
        tietDescription.setText(it.description)
        rvExtras.apply {
          adapter = this@AnywhereEditorFragment.adapter
        }
        extraBean?.apply {
          tietIntentAction.setText(action)
          tietIntentData.setText(data)
          adapter.setList(extras)
        }
        if (it.param2?.startsWith(it.param1) == true || it.param2?.startsWith(".") == true) {
          tilClassName.setEndIconOnClickListener {
            if (!tietClassName.text.isNullOrBlank() && !tietPackageName.text.isNullOrBlank()) {
              if (tietClassName.text.toString().startsWith(tietPackageName.text.toString())) {
                tietClassName.setText(
                  tietClassName.text.toString().removePrefix(tietPackageName.text.toString())
                )
              } else if (tietClassName.text.toString().startsWith(".")) {
                tietClassName.setText("${tietPackageName.text.toString()}${tietClassName.text.toString()}")
              }
            }
          }
        } else {
          tilClassName.isEndIconVisible = false
        }
      }
    }
  }

  override fun tryRunning() {
    if (binding.tietPackageName.text.isNullOrBlank()) {
      if (binding.tietIntentAction.text.isNullOrBlank()) {
        binding.tilIntentAction.error = getString(R.string.bsd_error_should_not_empty)
        return
      }
    }
    if (binding.tietIntentAction.text.isNullOrBlank()) {
      if (binding.tietPackageName.text.isNullOrBlank()) {
        binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
        return
      }
    }

    val doneItem = item.copy().apply {
      param1 = binding.tietPackageName.text.toString()
      param2 = if (binding.tietClassName.text.isNullOrBlank()) {
        ActivityUtils.getLauncherActivity(param1)
      } else {
        binding.tietClassName.text.toString()
      }
      val extras = adapter.data.filter { it.key.isNotBlank() && it.value.isNotBlank() }
      val extraBean = ExtraBean(
        action = binding.tietIntentAction.text.toString(),
        data = binding.tietIntentData.text.toString(),
        extras = extras
      )
      param3 = Gson().toJson(extraBean)
    }
    Opener.with(requireContext()).load(doneItem).open()
  }

  override fun doneEdit(): Boolean {
    if (binding.tietAppName.text.isNullOrBlank()) {
      binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }
    if (binding.tietPackageName.text.isNullOrBlank()) {
      if (binding.tietIntentAction.text.isNullOrBlank()) {
        binding.tilIntentAction.error = getString(R.string.bsd_error_should_not_empty)
        return false
      }
    }
    if (binding.tietIntentAction.text.isNullOrBlank()) {
      if (binding.tietPackageName.text.isNullOrBlank()) {
        binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
        return false
      }
    }

    doneItem = item.copy().apply {
      appName = binding.tietAppName.text.toString()
      param1 = binding.tietPackageName.text.toString()
      param2 = binding.tietClassName.text.toString()
      description = binding.tietDescription.text.toString()

      val extras = adapter.data.filter { it.key.isNotBlank() && it.value.isNotBlank() }
      val extraBean = ExtraBean(
        action = binding.tietIntentAction.text.toString(),
        data = binding.tietIntentData.text.toString(),
        extras = extras
      )
      param3 = Gson().toJson(extraBean)
      setExecWithRoot(execWithRoot)
    }

    if (super.doneEdit()) return true
    if (isEditMode && doneItem == item) return true

    if (isEditMode) {
      if (doneItem.appName != item.appName) {
        if (GlobalValues.shortcutsList.contains(doneItem.id)) {
          if (AppUtils.atLeastNMR1()) {
            ShortcutsUtils.updateShortcut(doneItem)
          }
        }
      }
      AnywhereApplication.sRepository.update(doneItem)
    } else {
      doneItem.id = System.currentTimeMillis().toString()
      AnywhereApplication.sRepository.insert(doneItem)
    }

    return true
  }
}
