package com.absinthe.anywhere_.view;

import android.content.Context;
import android.content.res.Resources;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.GlobalValues;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class FabBuilder {
    public static void build(Context context, SpeedDialView fab) {
        Resources resources = context.getResources();
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_url_scheme, R.drawable.ic_url_scheme)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_url_scheme))
                .setLabelClickable(false)
                .create());
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_activity_list, R.drawable.ic_activity_list)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_activity_list))
                .setLabelClickable(false)
                .create());
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_qr_code_collection, R.drawable.ic_qr_code)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(context.getString(R.string.btn_qr_code_collection))
                .setLabelClickable(false)
                .create());
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_collector, R.drawable.ic_logo)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel(GlobalValues.getCollectorMode())
                .setLabelClickable(false)
                .create());
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_image, R.drawable.ic_logo)
                .setFabBackgroundColor(resources.getColor(R.color.white))
                .setLabel("Add Image")
                .setLabelClickable(false)
                .create());
    }
}
