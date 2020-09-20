package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.workflow.FlowStepAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorWorkflowBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.manager.DialogManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class WorkflowEditorFragment  : BaseEditorFragment() {

    val adapter = FlowStepAdapter()
    var currentIndex = 0

    private lateinit var binding: EditorWorkflowBinding
    private val nodeEditMenu = listOf("Edit", "Delete")

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
            adapter.setOnItemClickListener { _, _, position ->
                currentIndex = position
                if (adapter.data[position].entity == null) {
                    DialogManager.showAdvancedCardSelectDialog(requireActivity() as BaseActivity, true)
                } else {
                    AlertDialog.Builder(requireContext())
                            .setItems(nodeEditMenu.toTypedArray()) { _, which ->
                                when(which) {
                                    0 -> {}
                                    1 -> adapter.removeAt(position)
                                }
                            }
                            .show()
                }
            }
        }
    }

    override fun tryRunning() {
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
            AnywhereApplication.sRepository.insert(doneItem)
        }

        return true
    }
}