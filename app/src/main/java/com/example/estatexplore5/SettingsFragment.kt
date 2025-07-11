package com.example.estatexplore5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.estatexplore5.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val sharedPref = requireActivity().getSharedPreferences("settings", 0)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        // Switch'in ilk durumu
        binding.switchDarkMode.isChecked = isDarkMode

        // Kullanıcı değiştirdiğinde
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(mode)

            // Ayarı kaydet
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
        }

        return binding.root
    }
}

