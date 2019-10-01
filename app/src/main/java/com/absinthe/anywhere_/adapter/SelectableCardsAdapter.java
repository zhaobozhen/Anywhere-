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

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.utils.EditUtils;
import com.absinthe.anywhere_.utils.ImageUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SelectableCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AnywhereEntity item = items.get(position);
        ((ItemViewHolder) viewHolder).bind(item);

        ((ItemViewHolder) viewHolder).materialCardView.setOnClickListener(view -> openAnywhereActivity(item));
        ((ItemViewHolder) viewHolder).materialCardView.setOnLongClickListener(view -> {
            vibrator.vibrate(30);
            if (item.getClassNameType() == ConstUtil.URL_SCHEME_TYPE) {
                EditUtils.editUrlScheme((Activity) mContext, this, item, position, true);
            } else {
                EditUtils.editAnywhere((Activity) mContext, this, item, position, true);
            }

            return true;
        });

        if (item.getClassNameType() == ConstUtil.URL_SCHEME_TYPE) {
            ((ItemViewHolder) viewHolder).urlSchemeView.setVisibility(View.VISIBLE);
            ((ItemViewHolder) viewHolder).packageNameView.setVisibility(View.GONE);
            ((ItemViewHolder) viewHolder).classNameView.setVisibility(View.GONE);
        } else {
            ((ItemViewHolder) viewHolder).urlSchemeView.setVisibility(View.GONE);
            ((ItemViewHolder) viewHolder).packageNameView.setVisibility(View.VISIBLE);
            ((ItemViewHolder) viewHolder).classNameView.setVisibility(View.VISIBLE);
        }

        if (((ItemViewHolder) viewHolder).customTextureView.getText().toString().isEmpty()) {
            ((ItemViewHolder) viewHolder).customTextureView.setVisibility(View.GONE);
        } else {
            ((ItemViewHolder) viewHolder).customTextureView.setVisibility(View.VISIBLE);
        }
    }

    private void openAnywhereActivity(AnywhereEntity item) {
        String cmd = null;

        String packageName = item.getPackageName();
        String className = item.getClassName();
        String urlScheme = item.getUrlScheme();
        int classNameType = item.getClassNameType();

        if (classNameType == ConstUtil.FULL_CLASS_NAME_TYPE) {
            if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
                Toast.makeText(mContext, mContext.getString(R.string.toast_change_work_mode), Toast.LENGTH_LONG).show();
                return;
            }
            cmd = "am start -n " + packageName + "/" + className;
        } else if (classNameType == ConstUtil.SHORT_CLASS_NAME_TYPE) {
            if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
                Toast.makeText(mContext, mContext.getString(R.string.toast_change_work_mode), Toast.LENGTH_LONG).show();
                return;
            }
            cmd = "am start -n " + packageName + "/" + packageName + className;
        } else if (classNameType == ConstUtil.URL_SCHEME_TYPE) {
            if (AnywhereApplication.workingMode.equals(ConstUtil.WORKING_MODE_URL_SCHEME)) {
                cmd = urlScheme;
            } else {
                cmd = "am start -a android.intent.action.VIEW -d " + urlScheme;
            }
        } else {
            Log.d(TAG, "className has problem.");
        }

        Log.d(TAG, packageName + "\n" + className + "\n" + urlScheme + "\n" + classNameType);
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
        private final TextView packageNameView;
        private final TextView classNameView;
        private final TextView urlSchemeView;
        private final TextView customTextureView;

        ItemViewHolder(View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_card);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            appNameView = itemView.findViewById(R.id.tv_card_app_name);
            packageNameView = itemView.findViewById(R.id.tv_card_package_name);
            classNameView = itemView.findViewById(R.id.tv_card_class_name);
            urlSchemeView = itemView.findViewById(R.id.tv_card_url_scheme);
            customTextureView = itemView.findViewById(R.id.tv_card_custom_texture);
        }

        private void bind(AnywhereEntity item) {
            appNameView.setText(item.getAppName());
            packageNameView.setText(item.getPackageName());
            classNameView.setText(item.getClassName());
            urlSchemeView.setText(item.getUrlScheme());
            customTextureView.setText(item.getCustomTexture());
            appIcon.setImageDrawable(ImageUtils.getAppIconByPackageName(mContext, item.getPackageName()));
        }

    }

}

