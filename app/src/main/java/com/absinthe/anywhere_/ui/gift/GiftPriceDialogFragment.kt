package com.absinthe.anywhere_.ui.gift

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.ui.gift.GiftActivity.Companion.instance
import com.absinthe.anywhere_.utils.ToastUtil
import com.absinthe.anywhere_.view.AnywhereDialogBuilder
import com.absinthe.anywhere_.view.AnywhereDialogFragment
import com.absinthe.anywhere_.viewmodel.GiftViewModel
import com.google.android.material.button.MaterialButton
import moe.feng.alipay.zerosdk.AlipayZeroSdk

class GiftPriceDialogFragment : AnywhereDialogFragment() {

    private lateinit var mViewModel: GiftViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = ViewModelProvider(this).get(GiftViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AnywhereDialogBuilder(requireContext())
        val layoutInflater = requireActivity().layoutInflater
        val inflate = layoutInflater.inflate(R.layout.layout_gift_purchase, null, false)
        initView(inflate)

        return builder.setView(inflate)
                .setTitle("解锁“艾薇的馈赠”")
                .create()
    }

    @SuppressLint("SetTextI18n")
    private fun initView(view: View) {
        val tvThirdPrice = view.findViewById<TextView>(R.id.tv_third_price)
        val tvInfinityPrice = view.findViewById<TextView>(R.id.tv_infinity_price)

        if (mViewModel.thirdTimesPrice.value == null ||
                mViewModel.infinityPrice.value == null) {
            mViewModel.thirdTimesPrice.observe(this, Observer { integer: Int ->
                tvThirdPrice.text = "￥$integer"
            })
            mViewModel.infinityPrice.observe(this, Observer { integer: Int ->
                tvInfinityPrice.text = "￥$integer"
            })
            mViewModel.price
        } else {
            tvThirdPrice.text = "￥" + mViewModel.thirdTimesPrice.value
            tvInfinityPrice.text = "￥" + mViewModel.infinityPrice.value
        }
        val btnPurchase: MaterialButton = view.findViewById(R.id.btn_purchase)
        btnPurchase.setOnClickListener {
            if (AlipayZeroSdk.hasInstalledAlipayClient(context)) {
                AlipayZeroSdk.startAlipayClient(instance, "fkx12584ebfzfjbjeov8h93")
            } else {
                ToastUtil.makeText("Please install Alipay")
            }
        }
    }
}