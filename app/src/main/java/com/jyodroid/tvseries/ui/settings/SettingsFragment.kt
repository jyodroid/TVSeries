package com.jyodroid.tvseries.ui.settings

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.jyodroid.tvseries.R

const val pinCodeKey = "pin_code"
const val enablePinKey = "enable_pin"
const val enableFingerPrintKey = "enable_fingerprint"

class SettingsFragment : PreferenceFragmentCompat() {
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private val alertDialog by lazy {
        val editText = EditText(requireContext()).also {
            it.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            it.hint = getString(R.string.pin_code)
        }
        val currentPin = prefs.getString(pinCodeKey, null)
        currentPin?.let { editText.text.insert(0, currentPin) }

        AlertDialog.Builder(requireContext()).also {
            it.setView(editText)
            it.setPositiveButton(android.R.string.ok) { _, _ ->
                val pin = editText.text.toString()
                setPinCode(pin)
            }
            it.setNegativeButton(android.R.string.cancel, null)
        }.create()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_screen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            pinCodeKey -> {
                showPinEdit()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPinEdit() {
        alertDialog.show()
    }

    private fun setPinCode(pin: String) {
        prefs.edit().putString(pinCodeKey, pin).apply()
    }
}