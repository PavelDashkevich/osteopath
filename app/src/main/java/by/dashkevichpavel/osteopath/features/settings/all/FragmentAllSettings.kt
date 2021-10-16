package by.dashkevichpavel.osteopath.features.settings.all

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentSettingsAllBinding
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import java.lang.IllegalArgumentException

class FragmentAllSettings : Fragment(R.layout.fragment_settings_all) {
    private var fragmentAllSettingsBinding: FragmentSettingsAllBinding? = null
    private val binding get() = fragmentAllSettingsBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> binding.dlDrawerLayout.open()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentAllSettingsBinding = null
    }

    private fun setupViews(view: View) {
        fragmentAllSettingsBinding = FragmentSettingsAllBinding.bind(view)
        setupToolbar(binding.tbActions)
        binding.lNavMenu.nvMain.setupWithNavController(findNavController())
    }

    private fun setupEventListeners() {
        binding.vMenuItemMakeBackup.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_fragmentAllSettings_to_fragmentBackupCreate)
            } catch (e: IllegalArgumentException) {
                Log.d("OsteoApp", "FragmentAllSettings: setupEventListeners(): exception: ${e.message}")
            }
        }

        binding.vMenuItemRestoreFromBackup.setOnClickListener {
            // TODO go to Restore from backup screen
        }
    }
}