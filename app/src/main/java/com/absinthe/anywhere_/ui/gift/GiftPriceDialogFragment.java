package com.absinthe.anywhere_.ui.gift;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.databinding.LayoutGiftPurchaseBinding;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewmodel.GiftViewModel;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

public class GiftPriceDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private LayoutGiftPurchaseBinding mBinding;
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
        mBinding = LayoutGiftPurchaseBinding.inflate(getLayoutInflater());

        initView();

        return builder.setView(mBinding.getRoot())
                .setTitle("解锁“艾薇的馈赠”")
                .create();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        if (mViewModel.getThirdTimesPrice().getValue() == null ||
                mViewModel.getInfinityPrice().getValue() == null) {

            mViewModel.getThirdTimesPrice().observe(this, integer ->
                    mBinding.tvThirdPrice.setText("￥" + integer));
            mViewModel.getInfinityPrice().observe(this, integer ->
                    mBinding.tvInfinityPrice.setText("￥" + integer));

            mViewModel.getPrice();
        } else {
            mBinding.tvThirdPrice.setText("￥" + mViewModel.getThirdTimesPrice().getValue());
            mBinding.tvInfinityPrice.setText("￥" + mViewModel.getInfinityPrice().getValue());
        }

        mBinding.btnPurchase.setOnClickListener(v -> {
            if (AlipayZeroSdk.hasInstalledAlipayClient(mContext)) {
                AlipayZeroSdk.startAlipayClient(GiftActivity.getInstance(), "fkx12584ebfzfjbjeov8h93");
            } else {
                ToastUtil.makeText("Please install Alipay");
            }
        });
    }
}
