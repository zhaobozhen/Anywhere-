package com.absinthe.anywhere_.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.services.TileOneService;
import com.absinthe.anywhere_.services.TileThreeService;
import com.absinthe.anywhere_.services.TileTwoService;
import com.absinthe.anywhere_.ui.list.CardListDialogFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.viewmodel.AnywhereViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileSettingsActivity extends BaseActivity {
    private Context mContext;
    private CardView cvTileOne, cvTileTwo, cvTileThree;
    private List<AnywhereEntity> mList;

    @Override
    protected void setViewBinding() {
        setContentView(R.layout.activity_tile_settings);
    }

    @Override
    protected void setToolbar() {
        mToolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected boolean isPaddingToolbar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void initView() {
        super.initView();

        cvTileOne = findViewById(R.id.cv_tile_one);
        cvTileTwo = findViewById(R.id.cv_tile_two);
        cvTileThree = findViewById(R.id.cv_tile_three);
        AnywhereViewModel viewModel = new ViewModelProvider(this).get(AnywhereViewModel.class);
        viewModel.getAllAnywhereEntities().observe(this,
                anywhereEntities -> {
                    mList = anywhereEntities;
                    load();
                });

    }

    @SuppressLint("SetTextI18n")
    private void initCard(CardView cardView, int cardNum) {
        TextView tvTitle = cardView.findViewById(R.id.tv_title);
        TextView tvAppName = cardView.findViewById(R.id.tv_app_name);
        TextView tvParam1 = cardView.findViewById(R.id.tv_param_1);
        TextView tvParam2 = cardView.findViewById(R.id.tv_param_2);
        ImageView ivIcon = cardView.findViewById(R.id.iv_app_icon);
        switch (cardNum) {
            case 1:
                tvTitle.setText("TileOne");
                break;
            case 2:
                tvTitle.setText("TileTwo");
                break;
            case 3:
                tvTitle.setText("TileThree");
                break;
        }
        tvAppName.setText("Anywhere-");
        tvParam1.setText("com.absinthe.anywhere_");
        tvParam2.setText(".ui.main.MainActivity");
        ivIcon.setImageResource(R.mipmap.ic_launcher);
    }

    @SuppressLint("SetTextI18n")
    private void loadCard(CardView cardView, AnywhereEntity ae) {
        if (cardView == cvTileOne) {
            ((TextView) cardView.findViewById(R.id.tv_title)).setText("TileOne");
        } else if (cardView == cvTileTwo) {
            ((TextView) cardView.findViewById(R.id.tv_title)).setText("TileTwo");
        } else if (cardView == cvTileThree) {
            ((TextView) cardView.findViewById(R.id.tv_title)).setText("TileThree");
        }

        ((TextView) cardView.findViewById(R.id.tv_app_name)).setText(ae.getAppName());
        ((TextView) cardView.findViewById(R.id.tv_param_1)).setText(ae.getParam1());
        ((TextView) cardView.findViewById(R.id.tv_param_2)).setText(ae.getParam2());
        Glide.with(mContext)
                .load(UiUtils.getAppIconByPackageName(mContext, ae))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into((ImageView) cardView.findViewById(R.id.iv_app_icon));
    }

    private void load() {
        if (SPUtils.getString(this, Const.PREF_TILE_ONE).isEmpty()) {
            initCard(cvTileOne, 1);
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_ONE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileOne, ae);
                    break;
                }
            }
        }
        if (SPUtils.getString(this, Const.PREF_TILE_TWO).isEmpty()) {
            initCard(cvTileTwo, 2);
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_TWO);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileTwo, ae);
                    break;
                }
            }
        }
        if (SPUtils.getString(this, Const.PREF_TILE_THREE).isEmpty()) {
            initCard(cvTileThree, 3);
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_THREE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileThree, ae);
                    break;
                }
            }
        }

        List<CardView> cardList = new ArrayList<>();
        cardList.add(cvTileOne);
        cardList.add(cvTileTwo);
        cardList.add(cvTileThree);

        for (CardView cardView : cardList) {
            cardView.findViewById(R.id.btn_select).setOnClickListener(view -> {
                CardListDialogFragment fragment = DialogManager.showCardListDialog(this);
                fragment.setOnItemClickListener(which -> {
                    loadCard(cardView, mList.get(which));

                    String tile = "";
                    String tileLabel = "";
                    String tileCmd = "";
                    if (cardView == cvTileOne) {
                        tile = Const.PREF_TILE_ONE;
                        tileLabel = Const.PREF_TILE_ONE_LABEL;
                        tileCmd = Const.PREF_TILE_ONE_CMD;

                        if (!AppUtils.isServiceRunning(mContext, TileOneService.class.getName())) {
                            startService(new Intent(mContext, TileOneService.class));
                        }
                    } else if (cardView == cvTileTwo) {
                        tile = Const.PREF_TILE_TWO;
                        tileLabel = Const.PREF_TILE_TWO_LABEL;
                        tileCmd = Const.PREF_TILE_TWO_CMD;

                        if (!AppUtils.isServiceRunning(mContext, TileTwoService.class.getName())) {
                            startService(new Intent(mContext, TileTwoService.class));
                        }
                    } else if (cardView == cvTileThree) {
                        tile = Const.PREF_TILE_THREE;
                        tileLabel = Const.PREF_TILE_THREE_LABEL;
                        tileCmd = Const.PREF_TILE_THREE_CMD;

                        if (!AppUtils.isServiceRunning(mContext, TileThreeService.class.getName())) {
                            startService(new Intent(mContext, TileThreeService.class));
                        }
                    }
                    SPUtils.putString(mContext, tile, mList.get(which).getId());
                    SPUtils.putString(mContext, tileLabel, mList.get(which).getAppName());
                    SPUtils.putString(mContext, tileCmd, TextUtils.getItemCommand(mList.get(which)));

                    fragment.dismiss();
                });
            });
        }
    }
}
