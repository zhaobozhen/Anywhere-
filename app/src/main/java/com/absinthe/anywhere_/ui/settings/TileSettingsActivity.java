package com.absinthe.anywhere_.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.services.TileOneService;
import com.absinthe.anywhere_.services.TileThreeService;
import com.absinthe.anywhere_.services.TileTwoService;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.AppUtils;
import com.absinthe.anywhere_.utils.SPUtils;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileSettingsActivity extends BaseActivity {
    private Context mContext;
    private CardView cvTileOne, cvTileTwo, cvTileThree;
    private List<AnywhereEntity> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_settings);
        mContext = this;

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        cvTileOne = findViewById(R.id.cv_tile_one);
        cvTileTwo = findViewById(R.id.cv_tile_two);
        cvTileThree = findViewById(R.id.cv_tile_three);
        mList = MainFragment.getViewModelInstance().getAllAnywhereEntities().getValue();

        List<CardView> cardList = new ArrayList<>();
        cardList.add(cvTileOne);
        cardList.add(cvTileTwo);
        cardList.add(cvTileThree);

        if (SPUtils.getString(this, Const.SP_KEY_TILE_ONE).isEmpty()) {
            initCard(cvTileOne, 1);
        } else {
            String id = SPUtils.getString(mContext, Const.SP_KEY_TILE_ONE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileOne, ae);
                    break;
                }
            }
        }
        if (SPUtils.getString(this, Const.SP_KEY_TILE_TWO).isEmpty()) {
            initCard(cvTileTwo, 2);
        } else {
            String id = SPUtils.getString(mContext, Const.SP_KEY_TILE_TWO);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileTwo, ae);
                    break;
                }
            }
        }
        if (SPUtils.getString(this, Const.SP_KEY_TILE_THREE).isEmpty()) {
            initCard(cvTileThree, 3);
        } else {
            String id = SPUtils.getString(mContext, Const.SP_KEY_TILE_THREE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    loadCard(cvTileThree, ae);
                    break;
                }
            }
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        for (AnywhereEntity ae : Objects.requireNonNull(mList)) {
            arrayAdapter.add(ae.getAppName());
        }

        for (CardView cardView : cardList) {
            cardView.findViewById(R.id.btn_select).setOnClickListener(view ->
                    new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                    .setAdapter(arrayAdapter, (dialogInterface, i) -> {
                        loadCard(cardView, mList.get(i));

                        String tile = "";
                        String tileCmd = "";
                        Tile quickTile = null;
                        if (cardView == cvTileOne) {
                            tile = Const.SP_KEY_TILE_ONE;
                            tileCmd = Const.SP_KEY_TILE_ONE_CMD;

                            if (!AppUtils.isServiceRunning(this, TileOneService.class.getName())) {
                                startService(new Intent(this, TileOneService.class));
                            }
                            quickTile = TileOneService.getInstance().getQsTile();
                        } else if (cardView == cvTileTwo) {
                            tile = Const.SP_KEY_TILE_TWO;
                            tileCmd = Const.SP_KEY_TILE_TWO_CMD;

                            if (!AppUtils.isServiceRunning(this, TileTwoService.class.getName())) {
                                startService(new Intent(this, TileTwoService.class));
                            }
                            quickTile = TileTwoService.getInstance().getQsTile();
                        } else if (cardView == cvTileThree) {
                            tile = Const.SP_KEY_TILE_THREE;
                            tileCmd = Const.SP_KEY_TILE_THREE_CMD;

                            if (!AppUtils.isServiceRunning(this, TileThreeService.class.getName())) {
                                startService(new Intent(this, TileThreeService.class));
                            }
                            quickTile = TileThreeService.getInstance().getQsTile();
                        }
                        SPUtils.putString(mContext, tile, mList.get(i).getId());
                        SPUtils.putString(mContext, tileCmd, TextUtils.getItemCommand(mList.get(i)));
                        if (quickTile != null) {
                            quickTile.setLabel(mList.get(i).getAppName());
                            quickTile.updateTile();
                        }
                    })
                    .show());
        }

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
            ((TextView)cardView.findViewById(R.id.tv_title)).setText("TileOne");
        } else if (cardView == cvTileTwo) {
            ((TextView)cardView.findViewById(R.id.tv_title)).setText("TileTwo");
        } else if (cardView == cvTileThree) {
            ((TextView)cardView.findViewById(R.id.tv_title)).setText("TileThree");
        }

        ((TextView)cardView.findViewById(R.id.tv_app_name)).setText(ae.getAppName());
        ((TextView)cardView.findViewById(R.id.tv_param_1)).setText(ae.getParam1());
        ((TextView)cardView.findViewById(R.id.tv_param_2)).setText(ae.getParam2());
        Glide.with(mContext)
                .load(UiUtils.getAppIconByPackageName(mContext, ae))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into((ImageView) cardView.findViewById(R.id.iv_app_icon));
    }
}
