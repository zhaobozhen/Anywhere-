package com.absinthe.anywhere_.ui.editor.impl

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
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class AnywhereEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorAnywhereBinding
    private val adapter = ExtrasAdapter()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorAnywhereBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        item.let {
            val extraBean: ExtraBean? = try {
                Gson().fromJson<ExtraBean>(it.param3, ExtraBean::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }

            binding.apply {
                tietAppName.setText(it.appName)
                tietPackageName.setText(it.param1)
                tietClassName.setText(it.param2)
                tietDescription.setText(it.description)
                extraBean?.apply {
                    tietIntentAction.setText(action)
                    tietIntentData.setText(data)
                }
                rvExtras.apply {
                    adapter = this@AnywhereEditorFragment.adapter
                }
            }

            adapter.apply {
                setHasStableIds(true)
                val headerBinding = LayoutHeaderExtrasBinding.inflate(layoutInflater)
                addHeaderView(headerBinding.root)

                headerBinding.ibAdd.setOnClickListener {
                    addData(0, ExtraBean.ExtraItem(TYPE_STRING, "", ""))
                }
                setOnItemChildClickListener { _, view, position ->
                    if (view.id == R.id.ib_delete) {
                        removeAt(position)
                    }
                }
            }
        }
    }

    override fun tryingRun() {
        if (binding.tietPackageName.text.isNullOrBlank()) {
            binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
            return
        }
        if (binding.tietClassName.text.isNullOrBlank()) {
            binding.tilClassName.error = getString(R.string.bsd_error_should_not_empty)
            return
        }

        val ae = AnywhereEntity(item).apply {
            param1 = binding.tietPackageName.text.toString()
            param2 = binding.tietClassName.text.toString()
            val extraBean = ExtraBean(
                    action = binding.tietIntentAction.text.toString(),
                    data = binding.tietIntentData.text.toString(),
                    extras = listOf()
            )
            param3 = Gson().toJson(extraBean)
        }
        AppUtils.openAnywhereEntity(requireContext(), ae)
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietPackageName.text.isNullOrBlank()) {
            binding.tilPackageName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietClassName.text.isNullOrBlank()) {
            binding.tilClassName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            param1 = binding.tietPackageName.text.toString()
            param2 = binding.tietClassName.text.toString()
            description = binding.tietDescription.text.toString()

            val extraBean = ExtraBean(
                    action = binding.tietIntentAction.text.toString(),
                    data = binding.tietIntentData.text.toString(),
                    extras = listOf()
            )
            param3 = Gson().toJson(extraBean)
        }

        if (isEditMode && ae == item) return true

        if (isEditMode) {
            if (ae.appName != item.appName) {
                if (GlobalValues.shortcutsList.contains(ae.id)) {
                    if (AppUtils.atLeastNMR1()) {
                        ShortcutsUtils.updateShortcut(ae)
                    }
                }
            }
            AnywhereApplication.sRepository.update(ae)
        } else {
            AnywhereApplication.sRepository.insert(ae)
        }

        return true
    }
}