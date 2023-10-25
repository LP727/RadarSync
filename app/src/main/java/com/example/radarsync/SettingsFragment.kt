package com.example.radarsync

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.radarsync.data.UserSettings
import com.example.radarsync.databinding.FragmentSettingsBinding
import java.io.File
import java.io.FileOutputStream

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.saveButton.setOnClickListener {
            saveSettings()
        }
        return binding.root
    }

    private fun loadSettings() {
        val directoryPath = "radarDirectory"
        val fileName = "radarSettings.txt"
        val file = File(directoryPath, fileName)

        if(file.exists()) {
            val fis = file.inputStream()
            val settings = UserSettings()

            settings.decode(viewModel.cryptoManager.decrypt(fis))

            binding.editTextUrl.setText(settings.url)
            binding.editTextPort.setText(settings.port.toString())
            binding.editTextUsr.setText(settings.username)
            binding.editTextTextPassword.setText(settings.password)
        }
    }
    private fun saveSettings() {
        val localSettings = UserSettings(
            binding.editTextUrl.text.toString(),
            binding.editTextPort.text.toString().toInt(),
            binding.editTextUsr.text.toString(),
            binding.editTextTextPassword.text.toString()
        )

        val directoryPath = "radarDirectory"
        val fileName = "radarSettings.txt"

        // Create the directory if it doesn't exist
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdir()
        }

        val file = File(directoryPath, fileName)
        if(!file.exists()) {
            file.createNewFile()
        }

        val fos = FileOutputStream(file)

        viewModel.cryptoManager.encrypt(
            bytes = localSettings.encode(),
            outputStream = fos
        )
    }
    private fun saveAndReturn() {
        saveSettings()
        findNavController().navigateUp()
    }
}
