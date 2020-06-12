package com.absinthe.anywhere_.ui.dialog

import android.app.Dialog
import android.os.Bundle
import com.absinthe.anywhere_.view.app.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.app.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.ImageDialogBuilder
import com.bumptech.glide.Glide

class ImageDialogFragment : AnywhereDialogFragment {

    private lateinit var mBuilder: ImageDialogBuilder
    private var mUri: String? = null

    constructor()

    constructor(uri: String, listener: OnDismissListener? = null) {
        mUri = uri

        setWrapOnDismissListener(listener)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = ImageDialogBuilder(requireContext())
        return AnywhereDialogBuilder(requireContext()).setView(mBuilder.root).create()
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        Glide.with(requireContext())
                .load(mUri)
                .into(mBuilder.image)
    }
}