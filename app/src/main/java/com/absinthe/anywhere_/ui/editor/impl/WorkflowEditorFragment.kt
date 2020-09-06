package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.absinthe.anywhere_.adapter.workflow.FlowStepAdapter
import com.absinthe.anywhere_.databinding.EditorWorkflowBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.model.viewholder.FlowStepBean
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.editor.IEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WorkflowEditorFragment  : BaseEditorFragment(), IEditor {

    private lateinit var binding: EditorWorkflowBinding
    private val adapter = FlowStepAdapter()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorWorkflowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        binding.list.apply {
            adapter = this@WorkflowEditorFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())

            lifecycleScope.launchWhenResumed {
                repeat(5) {
                    withContext(Dispatchers.IO) {
                        delay(1000)

                        withContext(Dispatchers.Main) {
                            this@WorkflowEditorFragment.adapter.addData(FlowStepBean(AnywhereEntity.Builder(), 0))
                        }
                    }
                }
            }
        }
    }

    override fun tryRunning() {
    }

    override fun doneEdit(): Boolean {
        return true
    }
}