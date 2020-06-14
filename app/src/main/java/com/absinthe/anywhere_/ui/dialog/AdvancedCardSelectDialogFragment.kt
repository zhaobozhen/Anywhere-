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
                AdvancedCardItem(R.string.btn_add_image, R.drawable.ic_card_image, getOpeningEditorListener(AnywhereType.Card.IMAGE)),
                AdvancedCardItem(R.string.btn_add_shell, R.drawable.ic_card_shell, getOpeningEditorListener(AnywhereType.Card.SHELL)),
                AdvancedCardItem(R.string.btn_add_file, R.drawable.ic_card_file, getOpeningEditorListener(AnywhereType.Card.FILE)),
                AdvancedCardItem(R.string.btn_add_intent, R.drawable.ic_card_intent, getOpeningEditorListener(AnywhereType.Card.INTENT)),
                AdvancedCardItem(R.string.btn_add_broadcast, R.drawable.ic_card_broadcast, getOpeningEditorListener(AnywhereType.Card.BROADCAST))
        )
        mBuilder.adapter.setNewInstance(cardList.toMutableList())
    }

    private fun getOpeningEditorListener(type: Int): View.OnClickListener {
        return View.OnClickListener {
            val ae = AnywhereEntity.Builder().apply {
                this.type = type
                appName = when (type) {
                    AnywhereType.Card.IMAGE -> "New Image"
                    AnywhereType.Card.SHELL -> "New Shell"
                    AnywhereType.Card.FILE -> "New File"
                    AnywhereType.Card.BROADCAST -> "New Broadcast"
                    else -> "New Card"
                }
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
        }
    }
}