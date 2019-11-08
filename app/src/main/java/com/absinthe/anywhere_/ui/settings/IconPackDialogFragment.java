package com.absinthe.anywhere_.ui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.adapter.AppListAdapter;
import com.absinthe.anywhere_.model.AppListBean;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.utils.IconPackManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconPackDialogFragment extends DialogFragment {
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_icon_pack, container, false);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog);
        LayoutInflater layoutInflater = ((Activity)mContext).getLayoutInflater();
        @SuppressLint("InflateParams")
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_icon_pack, null, false);

        initView(inflate);

        return builder.setView(inflate)
                .setTitle(R.string.dialog_title_choose_icon_pack)
                .create();
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_app_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AppListAdapter adapter = new AppListAdapter(mContext, AppListAdapter.MODE_ICON_PACK);
        adapter.setIconPackDialogFragment(this);
        recyclerView.setAdapter(adapter);

        HashMap<String, IconPackManager.IconPack> hashMap = Settings.sIconPackManager.getAvailableIconPacks(true);
        List<AppListBean> listBeans = new ArrayList<>();

        listBeans.add(new AppListBean("Default", "default.icon.pack", ""));
        for (Map.Entry<String, IconPackManager.IconPack> entry : hashMap.entrySet()) {
            IconPackManager.IconPack iconPack = entry.getValue();
            listBeans.add(new AppListBean(iconPack.name, iconPack.packageName, ""));
        }
        adapter.setList(listBeans);
    }
}
