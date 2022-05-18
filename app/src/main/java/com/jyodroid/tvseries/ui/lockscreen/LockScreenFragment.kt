package com.jyodroid.tvseries.ui.lockscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentLockScreenBinding
import com.jyodroid.tvseries.ui.settings.enableFingerPrintKey
import com.jyodroid.tvseries.utils.hide
import com.jyodroid.tvseries.utils.show

class LockScreenFragment : BiometricAuthListener, Fragment() {
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private var _binding: FragmentLockScreenBinding? = null
    private val binding get() = _binding!!

    private val lockViewModel: LockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockScreenBinding.inflate(inflater, container, false)
        initUI()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        binding.pinInput.error = null
        super.onResume()
    }

    private fun initUI() {
        binding.pinAuthButton.setOnClickListener {
            val pin = binding.pinEdit.text.toString()
            lockViewModel.unlockScreen(pin, prefs)
        }

        val fingerprintEnabled = prefs.getBoolean(enableFingerPrintKey, false)
        if (fingerprintEnabled && hasBiometricCapability()) {
            binding.pinAuthButton.hide()
            binding.pinInput.hide()
            binding.biometricsAuth.show()
        } else {
            binding.pinAuthButton.show()
            binding.pinInput.show()
            binding.biometricsAuth.hide()
        }

        binding.biometricsAuth.setOnClickListener { showBiometricPrompt(listener = this) }
    }

    private fun initObservers() {
        lockViewModel.screenLockedLiveData.observe(viewLifecycleOwner) { locked ->
            if (!locked) {
                val navController = findNavController()
                navController.navigate(R.id.navigation_home)
            } else {
                binding.pinInput.error = getString(R.string.wrong_pin)
            }
        }
    }

    private fun initBiometricPrompt(
        listener: BiometricAuthListener
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener.onBiometricAuthenticationError(errorCode, errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w(this.javaClass.simpleName, "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onBiometricAuthenticationSuccess(result)
            }
        }
        return BiometricPrompt(requireActivity(), executor, callback)
    }

    private fun hasBiometricCapability(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        val isBiometricReady = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        return isBiometricReady == BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt(
        title: String = getString(R.string.biometrics_title),
        subtitle: String = getString(R.string.biometrics_subtitle),
        description: String = getString(R.string.biometrics_description),
        listener: BiometricAuthListener,
        cryptoObject: BiometricPrompt.CryptoObject? = null,
        allowDeviceCredential: Boolean = false
    ) {
        val promptInfo =
            setBiometricPromptInfo(title, subtitle, description, allowDeviceCredential)

        val biometricPrompt = initBiometricPrompt(listener)

        biometricPrompt.apply {
            if (cryptoObject == null) authenticate(promptInfo)
            else authenticate(promptInfo, cryptoObject)
        }
    }

    /**
     * Prepares PromptInfo dialog with provided configuration
     */
    private fun setBiometricPromptInfo(
        title: String,
        subtitle: String,
        description: String,
        allowDeviceCredential: Boolean
    ): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)

        // Use Device Credentials if allowed, otherwise show Cancel Button
        builder.apply {
            if (allowDeviceCredential) setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            else setNegativeButtonText(getString(android.R.string.cancel))
        }

        return builder.build()
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        lockViewModel.unlockScreen()
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) =
        Toast.makeText(requireContext(), getString(R.string.biometrics_error), Toast.LENGTH_LONG)
            .show()
}

interface BiometricAuthListener {
    fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult)
    fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String)
}