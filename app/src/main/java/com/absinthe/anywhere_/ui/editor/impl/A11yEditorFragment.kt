package com.absinthe.anywhere_.ui.editor.impl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.absinthe.anywhere_.a11y.A11yEntity
import com.absinthe.anywhere_.adapter.a11y.A11yAdapter
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorA11yBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class A11yEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorA11yBinding
    private val adapter = A11yAdapter()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorA11yBinding.inflate(inflater)
        return binding.root
    }

    override fun initView() {
        item.let {
            binding.apply {
                tietAppName.setText(it.appName)
                tietDescription.setText(it.description)
            }

            val extra: A11yEntity? = try {
                Gson().fromJson(it.param1, A11yEntity::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }

            if (extra != null) {
                adapter.setList(extra.actions)
            }
        }

        binding.apply {
            list.apply {
                adapter = this@A11yEditorFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
            btnAddNode.setOnClickListener {
                adapter.addData(A11yActionBean())
            }
            adapter.draggableModule.isDragEnabled = true
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
            AnywhereApplication.sRepository.insert(doneItem)
        }

        return true
    }
}