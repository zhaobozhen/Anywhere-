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
import com.absinthe.anywhere_.ui.main.MainActivity;

public class LabFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

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

        if (md2Preference != null) {
            md2Preference.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case Const.PREF_MD2_TOOLBAR:
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
