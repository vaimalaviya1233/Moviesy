package com.kpstv.yts.activities.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.kpstv.yts.AppInterface
import com.kpstv.yts.R

class LookSettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.look_preference, rootKey)

        val darkPref = findPreference<SwitchPreferenceCompat>("IS_DARK_THEME")
        darkPref?.isChecked = AppInterface.IS_DARK_THEME
        darkPref?.setOnPreferenceChangeListener { _, newValue ->
            AppInterface.IS_DARK_THEME = newValue as Boolean
            return@setOnPreferenceChangeListener true
        }
    }
}