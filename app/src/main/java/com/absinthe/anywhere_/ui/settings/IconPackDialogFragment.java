package com.absinthe.anywhere_.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.applist.AppListAdapter;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.manager.IconPackManager;
import com.absinthe.anywhere_.view.AnywhereDialogBuilder;
import com.absinthe.anywhere_.view.AnywhereDialogFragment;
import com.absinthe.anywhere_.viewbuilder.entity.IconPackDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconPackDialogFragment extends AnywhereDialogFragment {
    private Context mContext;
    private IconPackDialogBuilder mBuilder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AnywhereDialogBuilder builder = new AnywhereDialogBuilder(mContext);
        mBuilder = new IconPackDialogBuilder(mContext);
        initView();

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_title_choose_icon_pack)
                .create();
    }

    private void initView() {
        mBuilder.rvIconPack.setLayoutManager(new LinearLayoutManager(mContext));
        AppListAdapter adapter = new AppListAdapter(mContext, AppListAdapter.MODE_ICON_PACK);
        adapter.setIconPackDialogFragment(this);
        mBuilder.rvIconPack.setAdapter(adapter);

        HashMap<String, IconPackManager.IconPack> hashMap = Settings.sIconPackManager.getAvailableIconPacks(true);
        List<AppListBean> listBeans = new ArrayList<>();

        listBeans.add(new AppListBean(mContext.getString(R.string.bsd_default), "default.icon.pack", "", -1));
        for (Map.Entry<String, IconPackManager.IconPack> entry : hashMap.entrySet()) {
            IconPackManager.IconPack iconPack = entry.getValue();
            listBeans.add(new AppListBean(iconPack.name, iconPack.packageName, "", -1));
        }
        adapter.setList(listBeans);
    }
}
