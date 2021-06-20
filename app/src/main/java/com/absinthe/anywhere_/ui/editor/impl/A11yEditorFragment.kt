package com.absinthe.anywhere_.ui.editor.impl

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.a11y.A11yActionBean
import com.absinthe.anywhere_.a11y.A11yEntity
import com.absinthe.anywhere_.a11y.A11yType
import com.absinthe.anywhere_.adapter.a11y.A11yAdapter
import com.absinthe.anywhere_.adapter.a11y.bean.A11yBaseBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yCoordBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yTextBean
import com.absinthe.anywhere_.adapter.a11y.bean.A11yViewIdBean
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.constants.GlobalValues
import com.absinthe.anywhere_.databinding.EditorA11yBinding
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.BaseEditorFragment
import com.absinthe.anywhere_.ui.list.*
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.ShortcutsUtils
import com.absinthe.anywhere_.utils.handler.Opener
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class A11yEditorFragment : BaseEditorFragment() {

    private lateinit var binding: EditorA11yBinding
    private val adapter = A11yAdapter()
    override var execWithRoot: Boolean = false

    private val nodeEditMenu by lazy {
        if (AppUtils.atLeastN()) {
            listOf(
                requireContext().getString(R.string.bsd_a11y_menu_click_text),
                requireContext().getString(R.string.bsd_a11y_menu_click_view_id),
                requireContext().getString(R.string.bsd_a11y_menu_long_press_text),
                requireContext().getString(R.string.bsd_a11y_menu_long_press_view_id),
                requireContext().getString(R.string.bsd_a11y_menu_click_coord),
                requireContext().getString(R.string.bsd_a11y_menu_long_press_coord),
            )
        } else {
            listOf(
                requireContext().getString(R.string.bsd_a11y_menu_click_text),
                requireContext().getString(R.string.bsd_a11y_menu_click_view_id),
                requireContext().getString(R.string.bsd_a11y_menu_long_press_text),
                requireContext().getString(R.string.bsd_a11y_menu_long_press_view_id),
            )
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = EditorA11yBinding.inflate(inflater)
        return binding.root
    }

    override fun initView() {
        val a11yEntity = try {
            Gson().fromJson(item.param1, A11yEntity::class.java)
        } catch (e: Exception) {
            null
        }
        item.let {
            binding.apply {
                tietAppName.setText(it.appName)
                tietDescription.setText(it.description)
                a11yEntity?.let {
                    tietApplicationId.setText(a11yEntity.applicationId)
                    tietEntryActivity.setText(a11yEntity.entryActivity)

                    val actionList = mutableListOf<A11yBaseBean>()
                    for (action in a11yEntity.actions) {
                        when(action.type) {
                            A11yType.TEXT, A11yType.LONG_PRESS_TEXT -> actionList.add(A11yTextBean(action))
                            A11yType.VIEW_ID, A11yType.LONG_PRESS_VIEW_ID -> actionList.add(A11yViewIdBean(action))
                            A11yType.COORDINATE, A11yType.LONG_PRESS_COORDINATE -> actionList.add(A11yCoordBean(action))
                        }
                    }
                    adapter.setList(actionList)
                }
            }

            val extra: A11yEntity? = try {
                Gson().fromJson(it.param1, A11yEntity::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }

            if (extra != null) {
                val actionList = mutableListOf<A11yBaseBean>()
                for (action in extra.actions) {
                    when(action.type) {
                        A11yType.TEXT, A11yType.LONG_PRESS_TEXT -> actionList.add(A11yTextBean(action))
                        A11yType.VIEW_ID, A11yType.LONG_PRESS_VIEW_ID -> actionList.add(A11yViewIdBean(action))
                        A11yType.COORDINATE, A11yType.LONG_PRESS_COORDINATE -> actionList.add(A11yCoordBean(action))
                    }
                }
                adapter.setList(actionList)
            }
        }

        binding.apply {
            list.apply {
                adapter = this@A11yEditorFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
            btnAddNode.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setItems(nodeEditMenu.toTypedArray()) { _, which ->
                        adapter.addData(when(which) {
                            A11yType.TEXT, A11yType.LONG_PRESS_TEXT -> A11yTextBean(A11yActionBean(type = which, pkgName = tietApplicationId.text.toString()))
                            A11yType.VIEW_ID, A11yType.LONG_PRESS_VIEW_ID -> A11yViewIdBean(A11yActionBean(type = which, pkgName = tietApplicationId.text.toString()))
                            A11yType.COORDINATE, A11yType.LONG_PRESS_COORDINATE -> A11yCoordBean(A11yActionBean(type = which, content = "0,0", pkgName = tietApplicationId.text.toString()))
                            else -> throw IllegalArgumentException("wrong a11y type")
                        })
                    }
                    .show()
            }
            tilApplicationId.setEndIconOnClickListener {
                startActivityForResult(Intent(requireContext(), AppListActivity::class.java).apply {
                    putExtra(EXTRA_APP_LIST_ENTRY_MODE, MODE_SELECT)
                }, Const.REQUEST_CODE_APP_LIST_SELECT)
            }
            tilEntryActivity.setEndIconOnClickListener {
                startActivityForResult(
                    Intent(
                        requireContext(),
                        AppDetailActivity::class.java
                    ).apply {
                        if (tietApplicationId.text?.isNotBlank() == true) {
                            putExtra(
                                Const.INTENT_EXTRA_APP_NAME,
                                com.blankj.utilcode.util.AppUtils.getAppName(tietApplicationId.text.toString())
                            )
                            putExtra(Const.INTENT_EXTRA_PKG_NAME, tietApplicationId.text.toString())
                        }
                        putExtra(EXTRA_APP_DETAIL_ENTRY_MODE, MODE_SELECT)
                    }, Const.REQUEST_CODE_APP_DETAIL_SELECT
                )
            }
            adapter.draggableModule.isDragEnabled = true
        }
    }

    override fun tryRunning() {
        val ae = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            description = binding.tietDescription.text.toString()

            val a11yEntity = A11yEntity().apply {
                applicationId = binding.tietApplicationId.text.toString()
                entryActivity = binding.tietEntryActivity.text.toString()
                actions = adapter.data.map { it.actionBean }
            }
            param1 = Gson().toJson(a11yEntity)
        }
        Opener.with(requireContext()).load(ae).open()
    }

    override fun doneEdit(): Boolean {
        if (binding.tietAppName.text.isNullOrBlank()) {
            binding.tilAppName.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietApplicationId.text.isNullOrBlank()) {
            binding.tilApplicationId.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }
        if (binding.tietEntryActivity.text.isNullOrBlank()) {
            binding.tilEntryActivity.error = getString(R.string.bsd_error_should_not_empty)
            return false
        }

        doneItem = AnywhereEntity(item).apply {
            appName = binding.tietAppName.text.toString()
            description = binding.tietDescription.text.toString()

            val a11yEntity = A11yEntity().apply {
                applicationId = binding.tietApplicationId.text.toString()
                entryActivity = binding.tietEntryActivity.text.toString()
                actions = adapter.data.map { it.actionBean }
            }
            param1 = Gson().toJson(a11yEntity)
            param2 = adapter.data.map { it.actionBean }.sumOf { it.delay }.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_APP_LIST_SELECT) {
            data?.getStringExtra(EXTRA_PACKAGE_NAME)?.let {
                binding.tietApplicationId.setText(it)
                binding.tietEntryActivity.setText(ActivityUtils.getLauncherActivity(it))
            }
        } else if (requestCode == Const.REQUEST_CODE_APP_DETAIL_SELECT) {
            binding.tietEntryActivity.setText(data?.getStringExtra(EXTRA_PACKAGE_NAME))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}