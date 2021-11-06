package by.dashkevichpavel.osteopath.features.settings.backup.restore

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.BackClickHandler
import by.dashkevichpavel.osteopath.BackClickListener
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentBackupRestoreBinding
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.helpers.takePersistableReadWritePermissions
import by.dashkevichpavel.osteopath.helpers.toEditable
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FragmentBackupRestore :
    Fragment(R.layout.fragment_backup_restore),
    BackClickListener {
    private val viewModel: BackupRestoreViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentBackupRestoreBinding: FragmentBackupRestoreBinding? = null
    private val binding get() = fragmentBackupRestoreBinding!!

    private var backClickHandler: BackClickHandler? = null

    private val pickBackupFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        backupFileUri: Uri? ->
        backupFileUri?.let { onBackupFilePicked(backupFileUri) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> viewModel.navigateUp()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backClickHandler?.removeBackClickListener(this)
        fragmentBackupRestoreBinding = null
    }

    private fun setupViews(view: View) {
        fragmentBackupRestoreBinding = FragmentBackupRestoreBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
        requireActivity().title = getString(R.string.screen_settings_all_backup_restore)
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        binding.ibPickBackupFile.setOnClickListener {
            pickBackupFile.launch(arrayOf(BackupHelper.MIME_TYPE))
        }
        binding.btnRestoreFromBackup.setOnClickListener {
            viewModel.startRestoringDBFromBackup()
        }
    }

    private fun setupObservers() {
        viewModel.backupFile.observe(viewLifecycleOwner, this::updateFieldBackupFile)
        viewModel.uiState.observe(viewLifecycleOwner, this::handleUiStateChange)
    }

    private fun handleUiStateChange(uiState: BackupRestoreUIState) {
        when (uiState) {
            BackupRestoreUIState.START -> updateUIState()
            BackupRestoreUIState.BACKUP_FILE_SET -> updateUIState(enableRestoring = true)
            BackupRestoreUIState.RESTORE_IN_PROGRESS ->
                updateUIState(
                    enableRestoring = false,
                    showProgressIndicator = true
                )
            BackupRestoreUIState.RESTORE_COMPLETE_SUCCESS -> {
                updateUIState(showSuccessMessage = true)
                viewModel.resetState()
            }
            BackupRestoreUIState.RESTORE_COMPLETE_FAIL -> {
                updateUIState(showFailMessage = true)
                viewModel.resetState()
            }
            BackupRestoreUIState.FINISH -> findNavController().navigateUp()
        }
    }

    private fun updateUIState(
        enableRestoring: Boolean = false,
        showProgressIndicator: Boolean = false,
        showSuccessMessage: Boolean = false,
        showFailMessage: Boolean = false) {
        binding.btnRestoreFromBackup.isEnabled = enableRestoring

        binding.btnRestoreFromBackup.visibility =
            if (showProgressIndicator) View.GONE else View.VISIBLE
        binding.lpiProgress.visibility =
            if (showProgressIndicator) View.VISIBLE else View.GONE
        binding.tvBackupOperationProgress.visibility =
            if (showProgressIndicator) View.VISIBLE else View.GONE
        binding.ibPickBackupFile.isEnabled = !showProgressIndicator

        if (showSuccessMessage) {
            Snackbar
                .make(requireView(), R.string.backup_restore_success_message, Snackbar.LENGTH_SHORT)
                .show()
        }

        if (showFailMessage) {
            Snackbar
                .make(
                    requireView(),
                    getString(R.string.backup_restore_fail_message, viewModel.getErrorMessage()),
                    Snackbar.LENGTH_LONG
                )
                .show()
        }
    }

    private fun updateFieldBackupFile(backupFile: String) {
        binding.etBackupFile.text = Uri.decode(backupFile).toEditable()
    }

    private fun onBackupFilePicked(backupFileUri: Uri) {
        backupFileUri.takePersistableReadWritePermissions(requireContext())
        viewModel.pickBackupFile(backupFileUri)
    }

    override fun onBackClick(): Boolean {
        viewModel.navigateUp()
        return true
    }

}