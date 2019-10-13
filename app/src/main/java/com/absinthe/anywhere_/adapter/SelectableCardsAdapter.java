package com.absinthe.anywhere_.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.AnywhereEntity;
import com.absinthe.anywhere_.model.AnywhereType;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ShortcutsUtil;
import com.absinthe.anywhere_.utils.TextUtils;
import com.absinthe.anywhere_.utils.UIUtils;
import com.absinthe.anywhere_.view.Editor;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SelectableCardsAdapter extends RecyclerView.Adapter<SelectableCardsAdapter.ItemViewHolder> {
    private static final String TAG = SelectableCardsAdapter.class.getSimpleName();

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

        int type = item.getAnywhereType();
        Log.d(TAG, "Type = " + type);

        viewHolder.materialCardView.setOnClickListener(view -> openAnywhereActivity(item));
        viewHolder.materialCardView.setOnLongClickListener(view -> {
            vibrator.vibrate(30);

            switch (type) {
                case AnywhereType.URL_SCHEME:
                    openEditor(item, Editor.URL_SCHEME, position);
                    break;
                case AnywhereType.ACTIVITY:
                    openEditor(item, Editor.ANYWHERE, position);
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
        String cmd = TextUtils.getItemCommand(item);
        if (!cmd.isEmpty()) {
            MainFragment.getViewModelInstance().getCommand().setValue(cmd);
        }
    }

    private void openEditor(AnywhereEntity item, int type, int position) {
        Editor editor = new Editor(MainActivity.getInstance(), type)
                .item(item)
                .isEditorMode(true)
                .isShortcut(item.getShortcutType() == AnywhereType.SHORTCUTS)
                .build();
        editor.setOnEditorListener(new Editor.OnEditorListener() {
            @Override
            public void onDelete() {
                deleteAnywhereActivity(editor, item, position);
            }

            @Override
            public void onChange() {
                notifyItemChanged(position);
            }
        });

        editor.show();
    }

    private void deleteAnywhereActivity(Editor editor, AnywhereEntity ae, int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(Html.fromHtml(mContext.getString(R.string.dialog_delete_message) + " <b>" + ae.getAppName() + "</b>" + " ?"))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                    MainFragment.getViewModelInstance().delete(ae);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        ShortcutsUtil.removeShortcut(ae);
                    }
                    editor.dismiss();
                    notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.dialog_delete_negative_button,
                        (dialogInterface, i) -> editor.show())
                .show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView materialCardView;
        private final AppCompatImageView appIcon;
        private final ImageView iconBadge;
        private final TextView appNameView;
        private final TextView param1View;
        private final TextView param2View;
        private final TextView param3View;
        private final TextView descriptionView;

        ItemViewHolder(View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_card);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            iconBadge = itemView.findViewById(R.id.iv_badge);
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
            appIcon.setImageDrawable(UIUtils.getAppIconByPackageName(mContext, item));
            if (item.getShortcutType() == AnywhereType.SHORTCUTS) {
                iconBadge.setImageResource(R.drawable.ic_added_shortcut);
                iconBadge.setVisibility(View.VISIBLE);
            } else {
                iconBadge.setVisibility(View.GONE);
            }
        }

    }

}

