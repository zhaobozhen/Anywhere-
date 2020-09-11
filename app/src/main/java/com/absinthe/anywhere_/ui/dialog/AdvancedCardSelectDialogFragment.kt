package com.absinthe.anywhere_.ui.dialog

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.absinthe.anywhere_.BaseActivity
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.adapter.card.AdvancedCardItem
import com.absinthe.anywhere_.constants.AnywhereType
import com.absinthe.anywhere_.model.database.AnywhereEntity
import com.absinthe.anywhere_.ui.editor.EXTRA_EDIT_MODE
import com.absinthe.anywhere_.ui.editor.EXTRA_ENTITY
import com.absinthe.anywhere_.ui.editor.EditorActivity
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.AdvancedCardSelectDialogBuilder
import com.microsoft.appcenter.analytics.Analytics
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
class AdvancedCardSelectDialogFragment : AnywhereDialogFragment() {

    private lateinit var mBuilder: AdvancedCardSelectDialogBuilder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = AdvancedCardSelectDialogBuilder(requireContext())

        val builder = AnywhereDialogBuilder(requireContext())
        initView()
        return builder.setView(mBuilder.root)
                .create()
    }

    private fun initView() {
        val cardList = listOf(
                AdvancedCardItem(R.string.btn_url_scheme, R.drawable.ic_url_scheme, R.color.material_blue_300, getOpeningEditorListener(AnywhereType.Card.URL_SCHEME)),
                AdvancedCardItem(R.string.btn_activity, R.drawable.ic_card_activity, R.color.material_red_300, getOpeningEditorListener(AnywhereType.Card.ACTIVITY)),
                AdvancedCardItem(R.string.btn_add_image, R.drawable.ic_card_image, R.color.material_green_300, getOpeningEditorListener(AnywhereType.Card.IMAGE)),
                AdvancedCardItem(R.string.btn_add_shell, R.drawable.ic_card_shell, R.color.material_pink_300, getOpeningEditorListener(AnywhereType.Card.SHELL)),
                AdvancedCardItem(R.string.btn_add_switch_shell, R.drawable.ic_card_switch, R.color.material_purple_300, getOpeningEditorListener(AnywhereType.Card.SWITCH_SHELL)),
                AdvancedCardItem(R.string.btn_add_file, R.drawable.ic_card_file, R.color.material_cyan_300, getOpeningEditorListener(AnywhereType.Card.FILE)),
                AdvancedCardItem(R.string.btn_add_broadcast, R.drawable.ic_card_broadcast, R.color.material_lime_300, getOpeningEditorListener(AnywhereType.Card.BROADCAST)),
                AdvancedCardItem(R.string.btn_add_workflow, R.drawable.ic_card_workflow, R.color.material_orange_300, getOpeningEditorListener(AnywhereType.Card.WORKFLOW))
        )
        mBuilder.adapter.setList(cardList.toMutableList())
    }

    private fun getOpeningEditorListener(type: Int): View.OnClickListener {
        return View.OnClickListener {
            val ae = AnywhereEntity.Builder().apply {
                this.type = type
                appName = AnywhereType.Card.NEW_TITLE_MAP[type] ?: "New Card"
            }

            val options = ActivityOptions.makeSceneTransitionAnimation(
                    context as BaseActivity,
                    it,
                    requireContext().getString(R.string.trans_item_container)
            )
            requireContext().startActivity(Intent(context, EditorActivity::class.java).apply {
                putExtra(EXTRA_ENTITY, ae)
                putExtra(EXTRA_EDIT_MODE, false)
            }, options.toBundle())

            Analytics.trackEvent("Fab ${ae.appName} clicked")
        }
    }
}