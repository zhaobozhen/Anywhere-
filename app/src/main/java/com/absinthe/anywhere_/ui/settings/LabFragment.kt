package com.absinthe.anywhere_.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.absinthe.anywhere_.R
import com.absinthe.anywhere_.model.Const
import com.absinthe.anywhere_.model.GlobalValues.setsIsPages
import com.absinthe.anywhere_.utils.AppUtils.restart

class LabFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.settings_lab, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        findPreference<Preference>(Const.PREF_PAGES)?.apply {
            onPreferenceChangeListener = this@LabFragment
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        return false
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            Const.PREF_PAGES -> {
                setsIsPages(newValue as Boolean)
                restart()
                return true
            }
        }
        return false
    }
}