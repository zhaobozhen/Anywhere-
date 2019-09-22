package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ConstUtil;
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
        ((ItemViewHolder) viewHolder).bind(item, position);

        ((ItemViewHolder) viewHolder).materialCardView.setOnClickListener(view -> openAnywhereActivity(item.getPackageName(), item.getClassName(), item.getClassNameType()));
        ((ItemViewHolder) viewHolder).materialCardView.setOnLongClickListener(view -> {
            vibrator.vibrate(30);
            deleteAnywhereActivity(item, position);
            return false;
        });
    }

    private void openAnywhereActivity(String packageName, String className, int classNameType) {
        String cmd = null;

        if (classNameType == ConstUtil.FULL_CLASS_NAME_TYPE) {
            cmd = "am start -n " + packageName + "/" + className;
        } else if (classNameType == ConstUtil.SHORT_CLASS_NAME_TYPE) {
            cmd = "am start -n " + packageName + "/" + packageName + className;
        } else {
            Log.d(TAG, "className has problem.");
        }

        Log.d(TAG, packageName + "\n" + className + "\n" + classNameType);
        MainFragment.getViewModelInstance().getCommand().setValue(cmd);
    }

    private void deleteAnywhereActivity(AnywhereEntity ae, int posiion) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(mContext.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    notifyItemRemoved(posiion);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> { })
                .show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView materialCardView;
        private final TextView appNameView;
        private final TextView packageNameView;
        private final TextView classNameView;
        private final TextView customTextureView;

        ItemViewHolder(View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_card);
            appNameView = itemView.findViewById(R.id.tv_card_app_name);
            packageNameView = itemView.findViewById(R.id.tv_card_package_name);
            classNameView = itemView.findViewById(R.id.tv_card_class_name);
            customTextureView = itemView.findViewById(R.id.tv_card_custom_texture);
        }

        private void bind(AnywhereEntity item, int position) {
            appNameView.setText(item.getAppName());
            packageNameView.setText(item.getPackageName());
            classNameView.setText(item.getClassName());
            customTextureView.setText(item.getCustomTexture());
        }

    }

}

