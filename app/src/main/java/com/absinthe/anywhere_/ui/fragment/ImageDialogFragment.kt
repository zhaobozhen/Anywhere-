package com.absinthe.anywhere_.ui.fragment

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.absinthe.anywhere_.AnywhereApplication
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.AnywhereEntity
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewbuilder.entity.ImageDialogBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class ImageDialogFragment : AnywhereDialogFragment {

    private lateinit var mBuilder: ImageDialogBuilder
    private var mItem: AnywhereEntity? = null
    private var mUri: String? = null
    private var mWidth = 0
    private var mHeight = 0

    constructor()

    constructor(ae: AnywhereEntity, listener: OnDismissListener? = null) {
        mItem = ae
        mUri = ae.param1

        val imageParam = getImageParams(ae.param2)
        mWidth = imageParam[0]
        mHeight = imageParam[1]
        setWrapOnDismissListener(listener)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBuilder = ImageDialogBuilder(requireContext())
        val dialog = AnywhereDialogBuilder(requireContext()).apply {
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog_shape)
        }

        if (mWidth != 0 && mHeight != 0) {
            mBuilder.root.layoutParams = LinearLayout.LayoutParams(mWidth, mHeight)
        }
        return dialog.setView(mBuilder.root).create()
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun getImageParams(param: String): IntArray {
        val params = param.split(",").toTypedArray()
        return if (params.size != 2) {
            intArrayOf(0, 0)
        } else intArrayOf(params[0].toInt(), params[1].toInt())
    }

    private fun initView() {
        Glide.with(requireContext())
                .asBitmap()
                .load(mUri)
                .override(600, 600)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        if (mWidth != 0 && mHeight != 0) {
                            mBuilder.apply {
                                root.layoutParams = FrameLayout.LayoutParams(mWidth, mHeight)
                                image.layoutParams = LinearLayout.LayoutParams(mWidth, mHeight)
                            }
                        } else {
                            val width = mBuilder.root.width
                            val height = width * resource.height / resource.width
                            mBuilder.apply {
                                root.layoutParams = FrameLayout.LayoutParams(width, height)
                                image.layoutParams = LinearLayout.LayoutParams(width, height)
                            }

                            mItem?.let {
                                it.param2 = "$width,$height"
                                AnywhereApplication.sRepository.update(it)
                            }
                        }
                        mBuilder.image.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
    }
}