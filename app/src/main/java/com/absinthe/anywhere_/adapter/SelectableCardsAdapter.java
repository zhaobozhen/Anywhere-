package com.absinthe.anywhere_.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ImageUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SelectableCardsAdapter extends RecyclerView.Adapter<SelectableCardsAdapter.ItemViewHolder> {
    private static final String TAG = "SelectableCardsAdapter";

    private List<AnywhereEntity> items;
    private Context mContext;
    private Vibrator vibrator;

    public SelectableCardsAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setItems(List<AnywhereEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int position) {
        AnywhereEntity item = items.get(position);
        viewHolder.bind(item);

        int type = item.getType();
        Log.d(TAG, "Type = " + type);

        viewHolder.materialCardView.setOnClickListener(view -> openAnywhereActivity(item));
        viewHolder.materialCardView.setOnLongClickListener(view -> {
            vibrator.vibrate(30);

            switch (type) {
                case AnywhereType.URL_SCHEME:
                    EditUtils.editUrlScheme((Activity) mContext, this, item, position, true);
                    break;
                case AnywhereType.ACTIVITY:
                    EditUtils.editAnywhere((Activity) mContext, this, item, position, true);
                    break;
                case AnywhereType.MINI_PROGRAM:
                    break;
            }
            return true;
        });

        switch (type) {
            case AnywhereType.URL_SCHEME:
                viewHolder.param1View.setVisibility(View.VISIBLE);
                viewHolder.param2View.setVisibility(View.GONE);
                viewHolder.param3View.setVisibility(View.GONE);
                break;
            case AnywhereType.ACTIVITY:
            case AnywhereType.MINI_PROGRAM:
                viewHolder.param1View.setVisibility(View.VISIBLE);
                viewHolder.param2View.setVisibility(View.VISIBLE);
                viewHolder.param3View.setVisibility(View.GONE);
                break;
        }

        if (viewHolder.descriptionView.getText().toString().isEmpty()) {
            viewHolder.descriptionView.setVisibility(View.GONE);
        } else {
            viewHolder.descriptionView.setVisibility(View.VISIBLE);
        }
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        String cmd = null;
        int type = item.getType();

        String packageName;
        String className;
        String urlScheme;
        int classNameType;

        if (type == AnywhereType.ACTIVITY) {
            if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                Toast.makeText(mContext, mContext.getString(R.string.toast_change_work_mode), Toast.LENGTH_LONG).show();
                return;
            }
            packageName = item.getParam1();
            className = item.getParam2();
            classNameType = Integer.valueOf(item.getParam3());
            Log.d(TAG, "packageName = " + packageName + ", className = " + className + ", classNameType = " + classNameType);

            if (classNameType == Const.FULL_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + className;
            } else if (classNameType == Const.SHORT_CLASS_NAME_TYPE) {
                cmd = "am start -n " + packageName + "/" + packageName + className;
            }
        } else if (type == AnywhereType.URL_SCHEME) {
            urlScheme = item.getParam1();
            Log.d(TAG, "urlScheme = " + urlScheme);

            if (GlobalValues.sWorkingMode.equals(Const.WORKING_MODE_URL_SCHEME)) {
                cmd = urlScheme;
            } else {
                cmd = "am start -a android.intent.action.VIEW -d " + urlScheme;
            }

        } else if (type == AnywhereType.MINI_PROGRAM) {
            //Todo
        } else {
            Log.d(TAG, "AnywhereType has problem.");
        }

        MainFragment.getViewModelInstance().getCommand().setValue(cmd);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView materialCardView;
        private final AppCompatImageView appIcon;
        private final TextView appNameView;
        private final TextView param1View;
        private final TextView param2View;
        private final TextView param3View;
        private final TextView descriptionView;

        ItemViewHolder(View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_card);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            appNameView = itemView.findViewById(R.id.tv_card_app_name);
            param1View = itemView.findViewById(R.id.tv_card_param_1);
            param2View = itemView.findViewById(R.id.tv_card_param_2);
            param3View = itemView.findViewById(R.id.tv_card_param_3);
            descriptionView = itemView.findViewById(R.id.tv_card_description);
        }

        private void bind(AnywhereEntity item) {
            appNameView.setText(item.getAppName());
            param1View.setText(item.getParam1());
            param2View.setText(item.getParam2());
            param3View.setText(item.getParam3());
            descriptionView.setText(item.getDescription());
            appIcon.setImageDrawable(ImageUtils.getAppIconByPackageName(mContext, item));
        }

    }

}

