package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.constants.OnceTag
import com.absinthe.anywhere_.databinding.EditorUrlSchemeBinding
import com.absinthe.anywhere_.model.database.setExecWithRoot
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.handler.URLSchemeHandler
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.absinthe.anywhere_.utils.manager.URLManager
import com.absinthe.libraries.utils.extensions.dp
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jonathanfinerty.once.Once

class SchemeEditorFragment : BaseEditorFragment() {

  private lateinit var binding: EditorUrlSchemeBinding

  override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
    binding = EditorUrlSchemeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun initView() {
    binding.btnUrlSchemeCommunity.setOnClickListener {
      if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.SHORTCUT_COMM_TIPS)) {
        DialogManager.showShortcutCommunityTipsDialog(requireActivity()) {
          openShortcutCommunity()
        }
        Once.markDone(OnceTag.SHORTCUT_COMM_TIPS)
      } else {
        openShortcutCommunity()
      }
    }
    item.let {
      binding.tietAppName.setText(it.appName)
      binding.tietUrlScheme.setText(it.param1)
      binding.tietDescription.setText(it.description)

      if (!it.param3.isNullOrBlank()) {
        binding.tietDynamicParams.setText(it.param3)
      }
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

  }

  override fun tryRunning() {
    val urlScheme = binding.tietUrlScheme.text.toString()
    if (urlScheme.isBlank()) {
      binding.tilUrlScheme.error = getString(R.string.bsd_error_should_not_empty)
      return
    }

    val doneItem = item.copy().apply {
      param1 = binding.tietUrlScheme.text.toString()
      param3 = binding.tietDynamicParams.text.toString()
      setExecWithRoot(execWithRoot)
    }
    context?.let {
      Opener.with(it).load(doneItem).open()
    }
  }

  override fun doneEdit(): Boolean {
    if (binding.tietAppName.text.isNullOrBlank()) {
      binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }
    if (binding.tietUrlScheme.text.isNullOrBlank()) {
      binding.tilUrlScheme.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }
    if (context == null) {
      return false
    }

    doneItem = item.copy().apply {
      appName = binding.tietAppName.text.toString()
      param1 = binding.tietUrlScheme.text.toString()
      param2 =
        AppUtils.getPackageNameByScheme(requireContext(), binding.tietUrlScheme.text.toString())
      param3 = binding.tietDynamicParams.text.toString()
      description = binding.tietDescription.text.toString()
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

  private fun openShortcutCommunity() {
    try {
      URLSchemeHandler.parse(requireContext(), URLManager.SHORTCUT_COMMUNITY_PAGE)
    } catch (e: Exception) {
      e.printStackTrace()
      if (e is ActivityNotFoundException) {
        ToastUtil.makeText(R.string.toast_no_react_url)
      } else if (e is RuntimeException) {
        ToastUtil.makeText(R.string.toast_runtime_error)
      }
    }
  }
}
