package com.absinthe.anywhere_.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ShortcutsUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private Context mContext;

    static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListPreference workingModePreference = findPreference(Const.PREF_WORKING_MODE);
        ListPreference darkModePreference = findPreference(Const.PREF_DARK_MODE);
        Preference changeBgPreference = findPreference(Const.PREF_CHANGE_BACKGROUND);
        Preference resetBgPreference = findPreference(Const.PREF_RESET_BACKGROUND);
        Preference helpPreference = findPreference(Const.PREF_HELP);
        Preference clearShortcutsPreference = findPreference(Const.PREF_CLEAR_SHORTCUTS);
        Preference iconPackPreference = findPreference(Const.PREF_ICON_PACK);
        Preference tilesPreference = findPreference(Const.PREF_TILES);
        SwitchPreferenceCompat streamCardModePreference = findPreference(Const.PREF_STREAM_CARD_MODE);
        SwitchPreferenceCompat streamCardSingleLinePreference = findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE);
        SwitchPreferenceCompat cardBackgroundPreference = findPreference(Const.PREF_CARD_BACKGROUND);

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
        if (helpPreference != null) {
            helpPreference.setOnPreferenceClickListener(this);
        }
        if (clearShortcutsPreference != null) {
            clearShortcutsPreference.setOnPreferenceClickListener(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                clearShortcutsPreference.setVisible(false);
            }
        }
        if (streamCardModePreference != null) {
            streamCardModePreference.setOnPreferenceChangeListener(this);
            if (streamCardSingleLinePreference != null) {
                streamCardSingleLinePreference.setEnabled(streamCardModePreference.isChecked());
                streamCardSingleLinePreference.setOnPreferenceChangeListener(this);
            }
            if (cardBackgroundPreference != null) {
                cardBackgroundPreference.setEnabled(streamCardModePreference.isChecked());
                cardBackgroundPreference.setOnPreferenceChangeListener(this);
            }
        }
        if (iconPackPreference != null) {
            iconPackPreference.setOnPreferenceClickListener(this);
        }

        if (tilesPreference != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                tilesPreference.setVisible(false);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case Const.PREF_CHANGE_BACKGROUND:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                SettingsActivity.getInstance().startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                return true;
            case Const.PREF_RESET_BACKGROUND:
                new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                        .setTitle(R.string.dialog_reset_background_confirm_title)
                        .setMessage(R.string.dialog_reset_background_confirm_message)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                            GlobalValues.setsBackgroundUri("");
                            MainActivity.getInstance().restartActivity();
                            SettingsActivity.getInstance().finish();
                        })
                        .setNegativeButton(R.string.dialog_delete_negative_button,
                                (dialogInterface, i) -> {
                                })
                        .show();
                return true;
            case Const.PREF_HELP:
                String url = "https://zhaobozhen.github.io/Anywhere-Docs/";
                CustomTabsIntent tabsIntent = new CustomTabsIntent.Builder()
                        .build();
                tabsIntent.launchUrl(mContext, Uri.parse(url));
                return true;
            case Const.PREF_CLEAR_SHORTCUTS:
                new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_Dialog)
                        .setTitle(R.string.dialog_reset_background_confirm_title)
                        .setMessage(R.string.dialog_reset_shortcuts_confirm_message)
                        .setPositiveButton(R.string.dialog_delete_positive_button, (dialogInterface, i) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                ShortcutsUtils.clearShortcuts();
                            }
                        })
                        .setNegativeButton(R.string.dialog_delete_negative_button,
                                (dialogInterface, i) -> {
                                })
                        .show();
                return true;
            case Const.PREF_ICON_PACK:
                IconPackDialogFragment fragment = new IconPackDialogFragment();
                fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "IconPackDialogFragment");
                return true;
            default:
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case Const.PREF_WORKING_MODE:
                if (MainFragment.getViewModelInstance() != null) {
                    MainFragment.getViewModelInstance().getWorkingMode().setValue(newValue.toString());
                }
                break;
            case Const.PREF_DARK_MODE:
                Settings.setTheme(newValue.toString());
                GlobalValues.setsDarkMode(newValue.toString());
                break;
            case Const.PREF_STREAM_CARD_MODE:
                GlobalValues.setsIsStreamCardMode((boolean) newValue);
                MainFragment.getViewModelInstance().getCardMode().setValue(newValue.toString());

                SwitchPreferenceCompat streamCardSingleLinePreference = findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE);
                if (streamCardSingleLinePreference != null) {
                    streamCardSingleLinePreference.setEnabled((boolean) newValue);
                }
                SwitchPreferenceCompat cardBackgroundPreference = findPreference(Const.PREF_CARD_BACKGROUND);
                if (cardBackgroundPreference != null) {
                    cardBackgroundPreference.setEnabled((boolean) newValue);
                }
                return true;
            case Const.PREF_STREAM_CARD_SINGLE_LINE:
                GlobalValues.setsIsStreamCardModeSingleLine((boolean) newValue);
                MainFragment.getViewModelInstance().getCardMode().setValue(newValue.toString());
                return true;
            case Const.PREF_CARD_BACKGROUND:
                GlobalValues.setsIsCardBackground((boolean) newValue);
                MainFragment.getViewModelInstance().getCardMode().setValue(String.valueOf(GlobalValues.sIsStreamCardMode));
                return true;
            default:
        }
        return true;
    }
}
