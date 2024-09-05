package com.absinthe.anywhere_.ui.editor

import android.net.Uri
import android.os.Bundle
import android.os.FileUriExposedException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.absinthe.anywhere_.BaseActivity.OpenDocument
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.dialog.EXTRA_FROM_WORKFLOW
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ToastUtil

abstract class BaseEditorFragment : Fragment(), IEditor {

  override var execWithRoot: Boolean = false
  protected val item by lazy {
    arguments?.getParcelable(EXTRA_ENTITY) as? AnywhereEntity ?: AnywhereEntity()
  }
  protected val isEditMode by lazy { requireArguments().getBoolean(EXTRA_EDIT_MODE) }
  protected val isFromWorkflow by lazy { requireArguments().getBoolean(EXTRA_FROM_WORKFLOW) }
  protected var doneItem: AnywhereEntity = AnywhereEntity()

  private var onDocumentResultAction: ((uri: Uri) -> Unit)? = null
  private lateinit var openDocumentResultLauncher: ActivityResultLauncher<Array<String>>

  protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View
  protected abstract fun initView()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val root = setBinding(inflater, container)
    initView()
    openDocumentResultLauncher = registerForActivityResult(OpenDocument()) {
      try {
        it?.let {
          it.data?.let { uri ->
            onDocumentResultAction?.invoke(uri)
            if (uri.toString().contains("file://")) {
              ToastUtil.makeText(R.string.toast_file_uri_exposed)
            } else {
              try {
                AppUtils.takePersistableUriPermission(requireActivity(), uri, it)
              } catch (e: RuntimeException) {
                ToastUtil.makeText(R.string.toast_runtime_error)
              }
            }
          }
        }
      } catch (e: Exception) {
        if (AppUtils.atLeastN()) {
          if (e is FileUriExposedException) {
            ToastUtil.makeText(R.string.toast_file_uri_exposed)
          }
        }
      }
    }
    return root
  }

  override fun doneEdit(): Boolean {
    if (isFromWorkflow) {
      EditorActivity.workflowResultItem.value = doneItem
    }
    return isFromWorkflow
  }

  fun setDocumentResult(mimeType: String, action: ((uri: Uri) -> Unit)?) {
    onDocumentResultAction = action
    openDocumentResultLauncher.launch(arrayOf(mimeType))
  }

}
