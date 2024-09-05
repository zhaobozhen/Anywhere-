package com.absinthe.anywhere_.ui.editor.impl

import android.content.ActivityNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.card.ExtrasAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorBroadcastBinding
import com.absinthe.anywhere_.databinding.LayoutHeaderExtrasBinding
import com.absinthe.anywhere_.model.ExtraBean
import com.absinthe.anywhere_.model.TYPE_STRING
import com.absinthe.anywhere_.model.database.setExecWithRoot
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.utils.UxUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.libraries.utils.extensions.dp
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class BroadcastEditorFragment : BaseEditorFragment() {

  private lateinit var binding: EditorBroadcastBinding
  private val adapter = ExtrasAdapter()

  override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
    binding = EditorBroadcastBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun initView() {
    item.let {
      val extraBean: ExtraBean? = try {
        Gson().fromJson(it.param1, ExtraBean::class.java)
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
        tietDescription.setText(it.description)
        tietIntentPackage.setText(it.param2)
        tietIntentClass.setText(it.param3)
        rvExtras.apply {
          adapter = this@BroadcastEditorFragment.adapter
        }
        extraBean?.apply {
          tietIntentAction.setText(action)
          tietIntentData.setText(data)
          adapter.setList(extras)
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
  }

  override fun tryRunning() {
    val doneItem = item.copy().apply {
      val extras = adapter.data.filter { it.key.isNotBlank() && it.value.isNotBlank() }
      val extraBean = ExtraBean(
        action = binding.tietIntentAction.text.toString(),
        data = binding.tietIntentData.text.toString(),
        extras = extras
      )
      param1 = Gson().toJson(extraBean)
      param2 = binding.tietIntentPackage.text.toString()
      param3 = binding.tietIntentClass.text.toString()
      setExecWithRoot(execWithRoot)
    }
    Opener.with(requireContext()).load(doneItem).open()
  }

  override fun doneEdit(): Boolean {
    if (binding.tietAppName.text.isNullOrBlank()) {
      binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
      return false
    }

    doneItem = item.copy().apply {
      appName = binding.tietAppName.text.toString()
      description = binding.tietDescription.text.toString()

      val extras = adapter.data.filter { it.key.isNotBlank() && it.value.isNotBlank() }
      val extraBean = ExtraBean(
        action = binding.tietIntentAction.text.toString(),
        data = binding.tietIntentData.text.toString(),
        extras = extras
      )
      param1 = Gson().toJson(extraBean)
      param2 = binding.tietIntentPackage.text.toString()
      param3 = binding.tietIntentClass.text.toString()
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
