package com.absinthe.anywhere_.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.model.Const;
import com.absinthe.anywhere_.model.GlobalValues;
import com.absinthe.anywhere_.ui.main.MainActivity;

public class LabFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    static LabFragment newInstance() {
        return new LabFragment();
    }

    private Context mContext;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_lab, rootKey);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference md2Preference = findPreference(Const.PREF_MD2_TOOLBAR);
        Preference pagesPreference = findPreference(Const.PREF_PAGES);

        if (md2Preference != null) {
            md2Preference.setOnPreferenceChangeListener(this);
        }
        if (pagesPreference != null) {
            pagesPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case Const.PREF_MD2_TOOLBAR:
                GlobalValues.setsIsMd2Toolbar((boolean) newValue);
                MainActivity.getInstance().restartActivity();
                ((Activity) mContext).finish();
                SettingsActivity.getInstance().finish();
                return true;
            case Const.PREF_PAGES:
                GlobalValues.setsIsPages((boolean) newValue);
                MainActivity.getInstance().restartActivity();
                ((Activity) mContext).finish();
                SettingsActivity.getInstance().finish();
                return true;
            default:
                break;
        }
        return false;
    }
}
