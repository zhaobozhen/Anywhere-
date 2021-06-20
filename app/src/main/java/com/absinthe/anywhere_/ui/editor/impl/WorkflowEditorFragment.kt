package com.absinthe.anywhere_.ui.editor.impl

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.applist.AppListAdapter
import com.absinthe.anywhere_.adapter.workflow.FlowStepAdapter
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorWorkflowBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.AppListBean
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.absinthe.anywhere_.ui.dialog.EXTRA_FROM_WORKFLOW
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WorkflowEditorFragment  : BaseEditorFragment() {

    val adapter = FlowStepAdapter()
    var currentIndex = -1

    private lateinit var binding: EditorWorkflowBinding
    override var execWithRoot: Boolean = false

    private val nodeEditMenu by lazy {
        listOf(
                requireContext().getString(R.string.bsd_workflow_menu_edit),
                requireContext().getString(R.string.menu_delete)
        )
    }
    private val nodeCreateMenu by lazy {
        listOf(
                requireContext().getString(R.string.bsd_workflow_menu_create),
                requireContext().getString(R.string.bsd_workflow_menu_choose_exist),
                requireContext().getString(R.string.menu_delete)
        )
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorWorkflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        item.let {
            binding.apply {
                tietAppName.setText(it.appName)
                tietDescription.setText(it.description)
            }

            val extra: List<FlowStepBean>? = try {
                Gson().fromJson(it.param1, object : TypeToken<List<FlowStepBean>>() {}.type)
            } catch (e: JsonSyntaxException) {
                null
            }

            if (extra != null) {
                adapter.setList(extra)
            }
        }

        binding.apply {
            list.apply {
                adapter = this@WorkflowEditorFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
            btnAddNode.setOnClickListener {
                adapter.addData(FlowStepBean())
            }
            adapter.draggableModule.isDragEnabled = true
            adapter.setOnItemClickListener { _, _, position ->
                currentIndex = position
                if (adapter.data[position].entity == null) {
                    AlertDialog.Builder(requireContext())
                            .setItems(nodeCreateMenu.toTypedArray()) { _, which ->
                                when(which) {
                                    0 -> DialogManager.showAdvancedCardSelectDialog(requireActivity() as BaseActivity, true)
                                    1 -> {
                                        DialogManager.showCardListDialog(requireActivity() as BaseActivity).apply {
                                            setOnItemClickListener(object : AppListAdapter.OnAppItemClickListener {
                                                override fun onClick(bean: AppListBean, which: Int) {
                                                    lifecycleScope.launch(Dispatchers.IO) {
                                                        AnywhereApplication.sRepository.getEntityById(bean.id)?.let {
                                                            withContext(Dispatchers.Main) {
                                                                adapter.setData(position, FlowStepBean(it, adapter.data[position].delay))
                                                            }
                                                        }
                                                    }
                                                    dismiss()
                                                }
                                            })
                                        }
                                    }
                                    2 -> adapter.removeAt(position)
                                }
                            }
                            .show()
                } else {
                    AlertDialog.Builder(requireContext())
                            .setItems(nodeEditMenu.toTypedArray()) { _, which ->
                                when(which) {
                                    0 -> {
                                        startActivityForResult(Intent(requireActivity(), EditorActivity::class.java).apply {
                                            putExtra(EXTRA_ENTITY, adapter.data[position].entity)
                                            putExtra(EXTRA_EDIT_MODE, false)
                                            putExtra(EXTRA_FROM_WORKFLOW, isFromWorkflow)
                                        }, Const.REQUEST_CODE_OPEN_EDITOR)
                                    }
                                    1 -> adapter.removeAt(position)
                                }
                            }
                            .show()
                }
            }
        }
    }

    override fun tryRunning() {
        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            description = binding.tietDescription.text.toString()

            val extras = adapter.data
            param1 = Gson().toJson(extras)
        }
        Opener.with(requireContext()).load(ae).open()
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        doneItem = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            description = binding.tietDescription.text.toString()

            val extras = adapter.data
            param1 = Gson().toJson(extras)
        }

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