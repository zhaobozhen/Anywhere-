package com.absinthe.anywhere_.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.absinthe.anywhere_.AnywhereApplication;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ConstUtil;
import com.absinthe.anywhere_.viewmodel.SettingsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 1001;
    private SettingsViewModel mViewModel;
    private Context mContext;

    static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        mContext = getActivity();

        ListPreference workingModePreference = findPreference(ConstUtil.SP_KEY_WORKING_MODE);
        Preference changeBgPreference = findPreference(ConstUtil.SP_KEY_CHANGE_BACKGROUND);
        Preference resetBgPreference = findPreference(ConstUtil.SP_KEY_RESET_BACKGROUND);
        ListPreference darkModePreference = findPreference(ConstUtil.SP_KEY_DARK_MODE);

        if (workingModePreference != null) {
            workingModePreference.setOnPreferenceChangeListener(this);
        }
        if (changeBgPreference != null) {
            changeBgPreference.setOnPreferenceClickListener(this);
        }
        if (resetBgPreference != null) {
            resetBgPreference.setOnPreferenceClickListener(this);
        }
        if (darkModePreference != null) {
            darkModePreference.setOnPreferenceChangeListener(this);
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case ConstUtil.SP_KEY_CHANGE_BACKGROUND:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity)mContext).startActivityForResult(intent,REQUEST_CODE_IMAGE_CAPTURE);
                break;
            case ConstUtil.SP_KEY_RESET_BACKGROUND:
                new MaterialAlertDialogBuilder(mContext)
                        .setTitle(R.string.dialog_reset_background_confirm_title)
                        .setMessage(R.string.dialog_reset_background_confirm_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                            MainFragment.getViewModelInstance().getBackground().setValue("");
                        })
                        .setNegativeButton(R.string.dialog_delete_negative_button,
                                (dialogInterface, i) -> { })
                        .show();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case ConstUtil.SP_KEY_WORKING_MODE:
                MainFragment.getViewModelInstance().getWorkingMode().setValue(newValue.toString());
                break;
            case ConstUtil.SP_KEY_DARK_MODE:
                AnywhereApplication.setTheme(newValue.toString());
                break;
            default:
        }
        return true;
    }
}
