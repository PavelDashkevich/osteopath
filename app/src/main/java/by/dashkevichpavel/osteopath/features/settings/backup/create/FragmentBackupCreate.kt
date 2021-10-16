package by.dashkevichpavel.osteopath.features.settings.backup.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentBackupCreateBinding
import by.dashkevichpavel.osteopath.features.BackClickHandler
import by.dashkevichpavel.osteopath.features.BackClickListener
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.helpers.permissions.PermissionsGrantedListener
import by.dashkevichpavel.osteopath.helpers.setupToolbar
import by.dashkevichpavel.osteopath.helpers.toEditable
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory

class FragmentBackupCreate :
    Fragment(R.layout.fragment_backup_create),
    PermissionsGrantedListener,
    BackClickListener {
    private val viewModel: BackupCreateViewModel by viewModels(
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentBackupCreateBinding: FragmentBackupCreateBinding? = null
    private val binding get() = fragmentBackupCreateBinding!!

    private var backClickHandler: BackClickHandler? = null

    private val pickDirectory =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uriDir: Uri? ->
            uriDir?.let { onBackupDirPicked(uriDir) }
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
        fragmentBackupCreateBinding = null
    }

    private fun setupViews(view: View) {
        fragmentBackupCreateBinding = FragmentBackupCreateBinding.bind(view)
        setupToolbar(binding.lToolbar.tbActions)
        binding.lToolbar.tbActions.title = getString(R.string.settings_backup_make)
        binding.tvMaxBackups.text = getString(
            R.string.backup_create_max_copies,
            BackupHelper.MAX_BACKUPS_NUMBER,
            requireContext().resources.getQuantityText(
                R.plurals.backups_number,
                BackupHelper.MAX_BACKUPS_NUMBER
            )
        )
    }

    private fun setupEventListeners() {
        backClickHandler = (requireActivity() as BackClickHandler)
        backClickHandler?.addBackClickListener(this)

        binding.ibSetBackupDir.setOnClickListener {
            pickDirectory.launch(
                if (viewModel.backupDir.value == "")
                    null
                else
                    Uri.parse(viewModel.backupDir.value)
            )
        }
        binding.btnCreateBackupManual.setOnClickListener {
            viewModel.createBackup()
        }
        binding.swEnableAutoBackup.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEnableAutoBackup(isChecked)
        }
    }

    private fun setupObservers() {
        viewModel.backupDir.observe(viewLifecycleOwner, this::updateFieldBackupDir)
        viewModel.autoBackupEnabled.observe(viewLifecycleOwner, this::updateFieldAutoBackupEnabled)
        viewModel.lastBackupDateTime.observe(viewLifecycleOwner, this::updateLastBackupStatus)
        viewModel.uiState.observe(viewLifecycleOwner, this::handleUiStateChange)
    }

    private fun updateFieldBackupDir(backupDir: String) {
        binding.etBackupDir.text = Uri.decode(backupDir).toEditable()
    }

    private fun updateFieldAutoBackupEnabled(autoBackupEnabled: Boolean) {
        binding.swEnableAutoBackup.isChecked = autoBackupEnabled
    }

    private fun updateLastBackupStatus(dateTime: Long) {
        if (dateTime == 0L) {
            binding.tvLastBackupStatus.visibility = View.GONE
        } else {
            binding.tvLastBackupStatus.visibility = View.VISIBLE

            val backupType = getString(
                if (viewModel.getLastBackupIsAuto())
                    R.string.backup_type_auto
                else
                    R.string.backup_type_manual
            )

            if (viewModel.getLastBackupIsFailed()) {
                binding.tvLastBackupStatus.text = getString(
                    R.string.last_backup_status_failed,
                    backupType,
                    viewModel.getLastBackupDateFormatted(),
                    viewModel.getLastBackupTimeFormatted(),
                    viewModel.getLastBackupFailureMessage()
                )
            } else {
                binding.tvLastBackupStatus.text = getString(
                    R.string.last_backup_status_successful,
                    viewModel.getLastBackupDateFormatted(),
                    viewModel.getLastBackupTimeFormatted(),
                    backupType
                )
            }
        }
    }

    private fun handleUiStateChange(uiState: BackupCreateUIState) {
        when (uiState) {
            BackupCreateUIState.START -> {
                updateUIState()
                viewModel.checkSettings()
            }
            BackupCreateUIState.NO_BACKUP_DIR -> updateUIState()
            BackupCreateUIState.BACKUP_DIR_NOT_EXISTS ->
                updateUIState(
                    showBackupDirError = true,
                    backupDirErrorText = getString(R.string.backup_dir_not_exists)
                )
            BackupCreateUIState.BACKUP_DIR_NOT_ACCESSIBLE ->
                updateUIState(
                    showBackupDirError = true,
                    backupDirErrorText = getString(R.string.backup_dir_not_accessible)
                )
            BackupCreateUIState.READY ->
                updateUIState(enableManualCreation = true, enableAutoBackupSetting = true)
            BackupCreateUIState.MANUAL_BACKUP_IN_PROGRESS ->
                updateUIState(showProgressIndicator = true)
            BackupCreateUIState.FINISH -> findNavController().navigateUp()
        }
    }

    private fun updateUIState(
        enableManualCreation: Boolean = false,
        enableAutoBackupSetting: Boolean = false,
        showProgressIndicator: Boolean = false,
        showBackupDirError: Boolean = false,
        backupDirErrorText: String = ""
    ) {
        binding.btnCreateBackupManual.isEnabled = enableManualCreation
        binding.swEnableAutoBackup.isEnabled = enableAutoBackupSetting

        binding.tilBackupDir.isErrorEnabled = showBackupDirError
        binding.tilBackupDir.error = backupDirErrorText

        binding.btnCreateBackupManual.visibility =
            if (showProgressIndicator) View.GONE else View.VISIBLE
        binding.lpiProgress.visibility =
            if (showProgressIndicator) View.VISIBLE else View.GONE
        binding.tvBackupCreationProgress.visibility =
            if (showProgressIndicator) View.VISIBLE else View.GONE
        binding.ibSetBackupDir.isEnabled = !showProgressIndicator
    }

    private fun onBackupDirPicked(uriDir: Uri) {
        requireContext().contentResolver.takePersistableUriPermission(
            uriDir,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        viewModel.pickDirectory(uriDir)
        viewModel.checkSettings()
    }

    override fun onPermissionsGranted() {

    }

    override fun onBackClick(): Boolean {
        viewModel.navigateUp()
        return true
    }
}