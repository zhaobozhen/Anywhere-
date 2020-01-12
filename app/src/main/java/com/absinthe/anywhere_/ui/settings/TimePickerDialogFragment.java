package com.absinthe.anywhere_.ui.settings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.viewbuilder.entity.TimePickerBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerDialogFragment extends DialogFragment {

    private Context mContext;
    private TimePickerBuilder mBuilder;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog);
        mBuilder = new TimePickerBuilder(mContext);

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(GlobalValues.sAutoDarkModeStart);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(GlobalValues.sAutoDarkModeEnd);
        if (GlobalValues.sAutoDarkModeStart == 0) {
            mBuilder.btnStart.setText(String.format(Locale.getDefault(), "%02d:%02d", 22, 0));
        } else {
            mBuilder.btnStart.setText(String.format(Locale.getDefault(), "%02d:%02d", start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE)));
        }
        if (GlobalValues.sAutoDarkModeEnd == 0) {
            mBuilder.btnEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", 7, 0));
        } else {
            mBuilder.btnEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE)));
        }

        View.OnClickListener listener = v ->
                new TimePickerDialog(mContext, (view, hourOfDay, minute) ->
                ((MaterialButton) v).setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)), 0, 0, true)
                .show();

        mBuilder.btnStart.setOnClickListener(listener);
        mBuilder.btnEnd.setOnClickListener(listener);

        return builder.setView(mBuilder.getRoot())
                .setTitle(R.string.dialog_set_dark_mode_period_title)
                .setPositiveButton(R.string.dialog_delete_positive_button, (dialog, which) -> {
                    try {
                        GlobalValues.setsAutoDarkModeStart(format.parse(mBuilder.btnStart.getText().toString()).getTime());
                        GlobalValues.setsAutoDarkModeEnd(format.parse(mBuilder.btnEnd.getText().toString()).getTime());
                        Settings.setTheme(Const.DARK_MODE_AUTO);
                        GlobalValues.setsDarkMode(Const.DARK_MODE_AUTO);
                        dismiss();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_negative_button, null)
                .create();
    }
}
