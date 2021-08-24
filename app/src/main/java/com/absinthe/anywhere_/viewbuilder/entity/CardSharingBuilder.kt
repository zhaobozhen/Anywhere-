package com.absinthe.anywhere_.viewbuilder.entity

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.absinthe.anywhere_.utils.QRCodeUtil
import com.absinthe.anywhere_.viewbuilder.ViewBuilder

class CardSharingBuilder(context: Context, text: String) : ViewBuilder(context) {

  private var ivQrCode: ImageView

  init {
    root = LinearLayout(context).apply {
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      )

      val padding = 20.dp
      setPadding(padding, padding, padding, padding)
      gravity = Gravity.CENTER
    }

    ivQrCode = ImageView(context).apply {
      layoutParams = LinearLayout.LayoutParams(250.dp, 250.dp)
    }
    addView(ivQrCode)

    ivQrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(text, 250.dp, 250.dp))
  }
}
