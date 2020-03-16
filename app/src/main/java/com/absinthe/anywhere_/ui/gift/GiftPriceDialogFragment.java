package com.absinthe.anywhere_.ui.gift;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewmodel.GiftViewModel;
import com.google.android.material.button.MaterialButton;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

public class GiftPriceDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private GiftViewModel mViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mViewModel = new ViewModelProvider(this).get(GiftViewModel.class);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
        View inflate = layoutInflater.inflate(R.layout.layout_gift_purchase, null, false);

        initView(inflate);

        return builder.setView(inflate)
                .setTitle("解锁“艾薇的馈赠”")
                .create();
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view) {
        TextView tvThirdPrice = view.findViewById(R.id.tv_third_price);
        TextView tvInfinityPrice = view.findViewById(R.id.tv_infinity_price);

        if (mViewModel.getThirdTimesPrice().getValue() == null ||
                mViewModel.getInfinityPrice().getValue() == null) {
            mViewModel.getThirdTimesPrice().observe(this, integer -> {
                if (tvThirdPrice != null) {
                    tvThirdPrice.setText("￥" + integer);
                }
            });
            mViewModel.getInfinityPrice().observe(this, integer -> {
                if (tvInfinityPrice != null) {
                    tvInfinityPrice.setText("￥" + integer);
                }
            });
            mViewModel.getPrice();
        } else {
            if (tvThirdPrice != null) {
                tvThirdPrice.setText("￥" + mViewModel.getThirdTimesPrice().getValue());
            }
            if (tvInfinityPrice != null) {
                tvInfinityPrice.setText("￥" + mViewModel.getInfinityPrice().getValue());
            }
        }

        MaterialButton btnPurchase = view.findViewById(R.id.btn_purchase);
        if (btnPurchase != null) {
            btnPurchase.setOnClickListener(v -> {
                if (AlipayZeroSdk.hasInstalledAlipayClient(mContext)) {
                    AlipayZeroSdk.startAlipayClient(GiftActivity.Companion.getInstance(), "fkx12584ebfzfjbjeov8h93");
                } else {
                    ToastUtil.makeText("Please install Alipay");
                }
            });
        }
    }
}
