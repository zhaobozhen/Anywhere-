package com.absinthe.anywhere_.ui.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.model.Settings;
import com.absinthe.anywhere_.ui.main.MainActivity;
import com.absinthe.anywhere_.ui.main.MainFragment;
import com.absinthe.anywhere_.utils.ToastUtil;
import com.absinthe.anywhere_.utils.manager.DialogManager;
import com.absinthe.anywhere_.utils.manager.IzukoHelper;
import com.absinthe.anywhere_.utils.manager.Logger;
import com.absinthe.anywhere_.utils.manager.URLManager;


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

        DropDownPreference workingModePreference = findPreference(Const.PREF_WORKING_MODE);
        DropDownPreference darkModePreference = findPreference(Const.PREF_DARK_MODE);
        DropDownPreference cardBackgroundPreference = findPreference(Const.PREF_CARD_BACKGROUND);
        Preference changeBgPreference = findPreference(Const.PREF_CHANGE_BACKGROUND);
        Preference resetBgPreference = findPreference(Const.PREF_RESET_BACKGROUND);
        Preference helpPreference = findPreference(Const.PREF_HELP);
        Preference clearShortcutsPreference = findPreference(Const.PREF_CLEAR_SHORTCUTS);
        Preference iconPackPreference = findPreference(Const.PREF_ICON_PACK);
        Preference tilesPreference = findPreference(Const.PREF_TILES);
        Preference giftPreference = findPreference(Const.PREF_GIFT);
        SwitchPreferenceCompat streamCardModePreference = findPreference(Const.PREF_STREAM_CARD_MODE);
        SwitchPreferenceCompat streamCardSingleLinePreference = findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE);
        SwitchPreferenceCompat collectorPlusPreference = findPreference(Const.PREF_COLLECTOR_PLUS);
        SwitchPreferenceCompat md2Preference = findPreference(Const.PREF_MD2_TOOLBAR);
        SwitchPreferenceCompat excludePreference = findPreference(Const.PREF_EXCLUDE_FROM_RECENT);

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
        if (collectorPlusPreference != null) {
            collectorPlusPreference.setOnPreferenceChangeListener(this);
        }
        if (giftPreference != null) {
            if (IzukoHelper.isHitagi()) {
                giftPreference.setSummary(getText(R.string.settings_gift_purchase_summary));
            } else {
                giftPreference.setSummary(getText(R.string.settings_gift_summary));
            }
        }
        if (md2Preference != null) {
            md2Preference.setOnPreferenceChangeListener(this);
        }
        if (excludePreference != null) {
            excludePreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Preference giftPreference = findPreference(Const.PREF_GIFT);
        if (giftPreference != null) {
            if (IzukoHelper.isHitagi()) {
                giftPreference.setSummary(getText(R.string.settings_gift_purchase_summary));
            } else {
                giftPreference.setSummary(getText(R.string.settings_gift_summary));
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case Const.PREF_CHANGE_BACKGROUND:
                try {
                    if (IzukoHelper.isHitagi()) {
                        mContext.startActivity(new Intent(mContext, BackgroundActivity.class));
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        SettingsActivity.getInstance().startActivityForResult(intent, Const.REQUEST_CODE_IMAGE_CAPTURE);
                        SettingsActivity.getInstance().setDocumentResultListener(uri -> {
                            if (uri != null) {
                                Logger.d("backgroundUri = " + uri);
                                GlobalValues.setsBackgroundUri(uri.toString());
                                GlobalValues.setsActionBarType("");
                                MainActivity instance = MainActivity.getInstance();
                                if (instance != null) {
                                    instance.getViewModel().getBackground().setValue(uri.toString());
                                    instance.restartActivity();
                                    SettingsActivity.getInstance().finish();
                                }
                            }
                        });
                    }
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtil.makeText(R.string.toast_no_document_app);
                }
                return true;
            case Const.PREF_RESET_BACKGROUND:
                DialogManager.showResetBackgroundDialog(mContext);
                return true;
            case Const.PREF_HELP:
                CustomTabsIntent tabsIntent = new CustomTabsIntent.Builder()
                        .build();
                tabsIntent.launchUrl(mContext, Uri.parse(URLManager.OLD_DOCUMENT_PAGE));
                return true;
            case Const.PREF_CLEAR_SHORTCUTS:
                DialogManager.showClearShortcutsDialog(mContext);
                return true;
            case Const.PREF_ICON_PACK:
                DialogManager.showIconPackChoosingDialog((AppCompatActivity) mContext);
                return true;
            default:
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case Const.PREF_WORKING_MODE:
                if (MainActivity.getInstance() != null && MainActivity.getInstance().getViewModel() != null) {
                    MainActivity.getInstance().getViewModel().getWorkingMode().setValue(newValue.toString());
                }
                break;
            case Const.PREF_DARK_MODE:
                if (newValue.toString().equals(Const.DARK_MODE_AUTO)) {
                    DialogManager.showDarkModeTimePickerDialog((AppCompatActivity) mContext);
                } else {
                    Settings.setTheme(newValue.toString());
                    GlobalValues.setsDarkMode(newValue.toString());
                }
                break;
            case Const.PREF_STREAM_CARD_MODE:
                GlobalValues.setsIsStreamCardMode((boolean) newValue);
                MainFragment.getViewModelInstance().getCardMode().setValue(newValue.toString());

                SwitchPreferenceCompat streamCardSingleLinePreference = findPreference(Const.PREF_STREAM_CARD_SINGLE_LINE);
                if (streamCardSingleLinePreference != null) {
                    streamCardSingleLinePreference.setEnabled((boolean) newValue);
                }
                DropDownPreference cardBackgroundPreference = findPreference(Const.PREF_CARD_BACKGROUND);
                if (cardBackgroundPreference != null) {
                    cardBackgroundPreference.setEnabled((boolean) newValue);
                }
                return true;
            case Const.PREF_STREAM_CARD_SINGLE_LINE:
                GlobalValues.setsIsStreamCardModeSingleLine((boolean) newValue);
                MainFragment.getViewModelInstance().getCardMode().setValue(newValue.toString());
                return true;
            case Const.PREF_CARD_BACKGROUND:
                GlobalValues.setsCardBackgroundMode(newValue.toString());
                MainFragment.getViewModelInstance().getCardMode().setValue(newValue.toString());
                return true;
            case Const.PREF_COLLECTOR_PLUS:
                GlobalValues.setsIsCollectorPlus((boolean) newValue);
                if ((boolean) newValue) {
                    DialogManager.showIntervalSetupDialog((AppCompatActivity) mContext);
                }
                return true;
            case Const.PREF_MD2_TOOLBAR:
                GlobalValues.setsIsMd2Toolbar((boolean) newValue);
                MainActivity.getInstance().restartActivity();
                ((Activity) mContext).finish();
                return true;
            case Const.PREF_EXCLUDE_FROM_RECENT:
                GlobalValues.setsIsExcludeFromRecent((boolean) newValue);
                return true;
            default:
        }
        return true;
    }
}
