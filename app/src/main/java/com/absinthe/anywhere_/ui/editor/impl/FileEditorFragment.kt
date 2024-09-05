package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorFileBinding
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.libraries.utils.extensions.dp
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class FileEditorFragment : BaseEditorFragment() {

  private lateinit var binding: EditorFileBinding

  override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
    binding = EditorFileBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun initView() {
    binding.tilUrl.isEnabled = false

    binding.tietAppName.setText(item.appName)
    binding.tietDescription.setText(item.description)

    binding.btnSelectFile.setOnClickListener {
      try {
        (requireContext() as EditorActivity).setDocumentResult("*/*") {
          binding.tietUrl.setText(it.toString())
        }
      } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        ToastUtil.makeText(R.string.toast_no_document_app)
      }
    }

    if (isEditMode) {
      binding.tietUrl.setText(item.param1)
    }

    binding.imgLogo.apply {
      if (item.iconUri.isNullOrEmpty()) {
        Glide.with(requireActivity().applicationContext)
          .load(UxUtils.getAppIcon(requireActivity(), item, 45.dp))
          .diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.imgLogo)
      } else {
        Glide.with(requireActivity().applicationContext).load(item.iconUri)
          .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imgLogo)
      }
      setOnClickListener {
        try {
          setDocumentResult("image/*") { uri ->
            // save image to local
            val iconPath =
              Utils.getApp().filesDir.toString() + "/pics/pic_${System.currentTimeMillis()}.png"
            FileIOUtils.writeFileFromIS(iconPath, context.contentResolver.openInputStream(uri))
            Glide.with(requireActivity()).load(iconPath)
              .transition(DrawableTransitionOptions.withCrossFade()).into(binding.imgLogo)
            item.iconUri = iconPath
            AnywhereApplication.sRepository.update(item)
          }
        } catch (e: ActivityNotFoundException) {
          e.printStackTrace()
          ToastUtil.makeText(R.string.toast_no_document_app)
        }
      }
    }

    requireActivity().invalidateOptionsMenu()
  }

  override fun tryRunning() {}

  override fun doneEdit(): Boolean {
    if (binding.tietAppName.text.isNullOrBlank()) {
      binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }
    if (binding.tietUrl.text.isNullOrBlank()) {
      binding.tilUrl.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }

    doneItem = item.copy().apply {
      appName = binding.tietAppName.text.toString()
      param1 = binding.tietUrl.text.toString()
      description = binding.tietDescription.text.toString()
    }

    if (super.doneEdit()) return true
    if (isEditMode && doneItem == item) return true

    if (isEditMode) {
      if (doneItem.appName != item.appName || doneItem.param1 != item.param1) {
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

  override fun onPrepareOptionsMenu(menu: Menu) {
    menu.findItem(R.id.trying_run).isVisible = false
    super.onPrepareOptionsMenu(menu)
  }
}
