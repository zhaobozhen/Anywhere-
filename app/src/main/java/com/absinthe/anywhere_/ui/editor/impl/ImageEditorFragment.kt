package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorImageBinding
import com.absinthe.anywhere_.model.database.isBrightWhenShowImage
import com.absinthe.anywhere_.model.database.setBrightWhenShowImage
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppTextUtils
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import com.google.android.material.shape.CornerFamily
import timber.log.Timber

class ImageEditorFragment : BaseEditorFragment(), OnButtonCheckedListener {

  private lateinit var binding: EditorImageBinding

  override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
    binding = EditorImageBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun initView() {
    if (item.param1.startsWith("http://") || item.param1.startsWith("https://")) {
      binding.toggleGroup.check(R.id.btn_web)
    } else {
      binding.tilUrl.isEnabled = false
    }

    binding.brightToggle.isChecked = item.isBrightWhenShowImage()
    binding.toggleGroup.addOnButtonCheckedListener(this)
    binding.tietAppName.setText(item.appName)
    binding.tietDescription.setText(item.description)

    binding.ivPreview.apply {
      shapeAppearanceModel = shapeAppearanceModel.toBuilder()
        .setAllCorners(
          CornerFamily.ROUNDED,
          context.resources.getDimension(R.dimen.toolbar_radius_corner)
        )
        .build()
      setOnClickListener {
        try {
          (requireContext() as EditorActivity).setDocumentResult("image/*") {
            loadImage(it.toString())
            binding.tietUrl.setText(it.toString())
          }
        } catch (e: ActivityNotFoundException) {
          e.printStackTrace()
          ToastUtil.makeText(R.string.toast_no_document_app)
        }
      }
    }

    binding.tietUrl.apply {
      if (isEditMode) {
        setText(item.param1)
        loadImage(item.param1)
      }
      addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
          if (AppTextUtils.isImageUrl(s.toString())) {
            loadImage(s.toString())
          }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
      })
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
      setBrightWhenShowImage(binding.brightToggle.isChecked)
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
      Timber.d("sasa")
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

  override fun onButtonChecked(
    group: MaterialButtonToggleGroup?,
    checkedId: Int,
    isChecked: Boolean
  ) {
    when (checkedId) {
      R.id.btn_local -> if (isChecked) {
        if (!isEditMode) {
          binding.tietUrl.setText("")
          binding.ivPreview.setImageDrawable(
            ContextCompat.getDrawable(
              requireContext(),
              R.drawable.ic_image_placeholder
            )
          )
        }
        binding.tilUrl.isEnabled = false
        binding.ivPreview.isClickable = true
      }
      R.id.btn_web -> if (isChecked) {
        binding.tilUrl.isEnabled = true
        binding.ivPreview.isClickable = false
      }
    }
  }

  private fun loadImage(url: String) {
    activity?.let {
      Glide.with(it.applicationContext)
        .load(url)
        .into(binding.ivPreview)
      binding.ivPreview.requestFocus()
    }
  }
}
