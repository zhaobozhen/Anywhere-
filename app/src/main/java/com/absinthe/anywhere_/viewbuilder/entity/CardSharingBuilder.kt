package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.utils.QRCodeUtil
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CardSharingBuilder(context: Context, private val text: String) : ViewBuilder(context) {

    private lateinit var ivQrCode: ImageView

    override fun init() {
        root = LinearLayout(mContext).apply {
            layoutParams = Params.LL.MATCH_WRAP

            val padding = 20.dp
            setPadding(padding, padding, padding, padding)
            gravity = Gravity.CENTER
        }

        ivQrCode = ImageView(mContext).apply {
            layoutParams = LinearLayout.LayoutParams(250.dp, 250.dp)
        }
        addView(ivQrCode)
    }

    private fun initView() {
        ivQrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(text, 250.dp, 250.dp))
    }

    init {
        initView()
    }
}