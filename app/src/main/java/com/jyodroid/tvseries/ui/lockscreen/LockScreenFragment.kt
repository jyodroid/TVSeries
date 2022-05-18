package com.jyodroid.tvseries.ui.lockscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.jyodroid.tvseries.R
import com.jyodroid.tvseries.databinding.FragmentLockScreenBinding

class LockScreenFragment : Fragment() {
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
        initObservers()
        initUI()
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
}