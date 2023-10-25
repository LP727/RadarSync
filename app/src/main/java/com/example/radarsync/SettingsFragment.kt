package com.example.radarsync

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
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
            saveAndReturn()
        }
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        loadSettings()
        return binding.root
    }

    private fun loadSettings() {
        val directoryPath = viewModel.app.getExternalFilesDir(null)?.absolutePath + File.separator + "radarDirectory"
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

        val imm = requireActivity()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.saveButton.windowToken, 0) // close the keyboard as we leave the fragment

        val localSettings = UserSettings(
            binding.editTextUrl.text.toString(),
            binding.editTextPort.text.toString().toIntOrNull(),
            binding.editTextUsr.text.toString(),
            binding.editTextTextPassword.text.toString()
        )
        val directoryPath = viewModel.app.getExternalFilesDir(null)?.absolutePath + File.separator + "radarDirectory"
        val fileName = "radarSettings.txt"

        Log.i(
            LOG_TAG,
            "Directory : $directoryPath"
        )
        // Create the directory if it doesn't exist

        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdir()
        }

        Log.i(
            LOG_TAG,
            "File : ${directoryPath + File.separator + fileName}}"
        )

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
