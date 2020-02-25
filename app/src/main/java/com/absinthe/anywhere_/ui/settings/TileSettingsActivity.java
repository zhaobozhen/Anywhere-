package com.absinthe.anywhere_.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.absinthe.anywhere_.BaseActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.tile.TileCardAdapter;
import com.absinthe.anywhere_.databinding.ActivityTileSettingsBinding;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AppListBean;
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

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TileSettingsActivity extends BaseActivity {

    private Context mContext;
    private ActivityTileSettingsBinding mBinding;
    private TileCardAdapter mAdapter;
    private List<AnywhereEntity> mList;

    @Override
    protected void setViewBinding() {
        mBinding = ActivityTileSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void setToolbar() {
        mToolbar = mBinding.toolbar;
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

        mAdapter = new TileCardAdapter();
        mBinding.rvTiles.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvTiles.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_select) {
                CardListDialogFragment fragment = DialogManager.showCardListDialog(this);
                fragment.setOnItemClickListener((bean, which) -> {
                    mAdapter.setData(position, bean);

                    String tile = "";
                    String tileLabel = "";
                    String tileCmd = "";

                    switch (position) {
                        case 0:
                            tile = Const.PREF_TILE_ONE;
                            tileLabel = Const.PREF_TILE_ONE_LABEL;
                            tileCmd = Const.PREF_TILE_ONE_CMD;

                            if (!AppUtils.isServiceRunning(mContext, TileOneService.class.getName())) {
                                startService(new Intent(mContext, TileOneService.class));
                            }
                            break;
                        case 1:
                            tile = Const.PREF_TILE_TWO;
                            tileLabel = Const.PREF_TILE_TWO_LABEL;
                            tileCmd = Const.PREF_TILE_TWO_CMD;

                            if (!AppUtils.isServiceRunning(mContext, TileTwoService.class.getName())) {
                                startService(new Intent(mContext, TileTwoService.class));
                            }
                            break;
                        case 2:
                            tile = Const.PREF_TILE_THREE;
                            tileLabel = Const.PREF_TILE_THREE_LABEL;
                            tileCmd = Const.PREF_TILE_THREE_CMD;

                            if (!AppUtils.isServiceRunning(mContext, TileThreeService.class.getName())) {
                                startService(new Intent(mContext, TileThreeService.class));
                            }
                            break;
                        default:
                    }

                    SPUtils.putString(mContext, tile, mList.get(which).getId());
                    SPUtils.putString(mContext, tileLabel, mList.get(which).getAppName());
                    SPUtils.putString(mContext, tileCmd, TextUtils.getItemCommand(mList.get(which)));

                    fragment.dismiss();
                });
            }
        });

        AnywhereViewModel viewModel = new ViewModelProvider(this).get(AnywhereViewModel.class);
        viewModel.getAllAnywhereEntities().observe(this, anywhereEntities -> {
                    mList = anywhereEntities;
                    load();
                });
    }

    private AppListBean initCard() {
        AppListBean bean = new AppListBean();
        bean.setAppName(getString(R.string.app_name));
        bean.setPackageName(getPackageName());
        bean.setClassName(getLocalClassName());
        bean.setIcon(getDrawable(R.mipmap.ic_launcher));
        return bean;
    }

    private AppListBean initCard(AnywhereEntity item) {
        AppListBean bean = new AppListBean();
        bean.setAppName(item.getAppName());
        bean.setPackageName(item.getParam1());
        bean.setClassName(item.getParam2());
        bean.setIcon(UiUtils.getAppIconByPackageName(this, item));
        return bean;
    }

    private void load() {
        if (SPUtils.getString(this, Const.PREF_TILE_ONE).isEmpty()) {
            mAdapter.addData(initCard());
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_ONE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    mAdapter.addData(initCard(ae));
                    break;
                }
            }
        }

        if (SPUtils.getString(this, Const.PREF_TILE_TWO).isEmpty()) {
            mAdapter.addData(initCard());
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_TWO);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    mAdapter.addData(initCard(ae));
                    break;
                }
            }
        }

        if (SPUtils.getString(this, Const.PREF_TILE_THREE).isEmpty()) {
            mAdapter.addData(initCard());
        } else {
            String id = SPUtils.getString(mContext, Const.PREF_TILE_THREE);
            for (AnywhereEntity ae : mList) {
                if (ae.getId().equals(id)) {
                    mAdapter.addData(initCard(ae));
                    break;
                }
            }
        }
    }
}
