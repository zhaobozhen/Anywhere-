package com.absinthe.anywhere_.ui.dialog

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.card.AdvancedCardItem
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.constants.Const
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.AdvancedCardSelectDialogBuilder
import com.microsoft.appcenter.analytics.Analytics
import java.lang.ref.WeakReference

const val EXTRA_FROM_WORKFLOW = "EXTRA_FROM_WORKFLOW"

class AdvancedCardSelectDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: AdvancedCardSelectDialogBuilder
    private val isFromWorkflow by lazy { arguments?.getBoolean(EXTRA_FROM_WORKFLOW) ?: false }
    private val weakContext by lazy { WeakReference(requireActivity()) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AdvancedCardSelectDialogBuilder(requireContext())

        val builder = AnywhereDialogBuilder(requireContext())
        initView()
        return builder.setView(mBuilder.root)
                .create()
    }

    private fun initView() {
        val cardList = listOf(
                AdvancedCardItem(R.string.btn_url_scheme, AnywhereType.Card.URL_SCHEME, getOpeningEditorListener(AnywhereType.Card.URL_SCHEME)),
                AdvancedCardItem(R.string.btn_activity, AnywhereType.Card.ACTIVITY, getOpeningEditorListener(AnywhereType.Card.ACTIVITY)),
                AdvancedCardItem(R.string.btn_add_image, AnywhereType.Card.IMAGE, getOpeningEditorListener(AnywhereType.Card.IMAGE)),
                AdvancedCardItem(R.string.btn_add_shell, AnywhereType.Card.SHELL, getOpeningEditorListener(AnywhereType.Card.SHELL)),
                AdvancedCardItem(R.string.btn_add_switch_shell, AnywhereType.Card.SWITCH_SHELL, getOpeningEditorListener(AnywhereType.Card.SWITCH_SHELL)),
                AdvancedCardItem(R.string.btn_add_file, AnywhereType.Card.FILE, getOpeningEditorListener(AnywhereType.Card.FILE)),
                AdvancedCardItem(R.string.btn_add_broadcast, AnywhereType.Card.BROADCAST, getOpeningEditorListener(AnywhereType.Card.BROADCAST)),
                AdvancedCardItem(R.string.btn_add_accessibility, AnywhereType.Card.ACCESSIBILITY, getOpeningEditorListener(AnywhereType.Card.ACCESSIBILITY)),
                AdvancedCardItem(R.string.btn_add_workflow, AnywhereType.Card.WORKFLOW, getOpeningEditorListener(AnywhereType.Card.WORKFLOW)),
        )
        mBuilder.adapter.setList(cardList.toMutableList())
    }

    private fun getOpeningEditorListener(type: Int): View.OnClickListener {
        return View.OnClickListener {
            val ae = AnywhereEntity().apply {
                this.type = type
                appName = AnywhereType.Card.NEW_TITLE_MAP[type] ?: "New Card"
            }

            val options = ActivityOptions.makeSceneTransitionAnimation(
                    weakContext.get() as BaseActivity,
                    it,
                    requireContext().getString(R.string.trans_item_container)
            )
            startActivityForResult(Intent(requireActivity(), EditorActivity::class.java).apply {
                putExtra(EXTRA_ENTITY, ae)
                putExtra(EXTRA_EDIT_MODE, false)
                putExtra(EXTRA_FROM_WORKFLOW, isFromWorkflow)
            }, Const.REQUEST_CODE_OPEN_EDITOR, options.toBundle())

            Analytics.trackEvent("Fab ${ae.appName} clicked")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.REQUEST_CODE_OPEN_EDITOR && resultCode == Activity.RESULT_OK) {
            dismiss()
        }
    }

}