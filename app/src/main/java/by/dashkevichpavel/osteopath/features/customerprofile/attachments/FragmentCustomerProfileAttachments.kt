package by.dashkevichpavel.osteopath.features.customerprofile.attachments

import android.app.Instrumentation
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileAttachmentsBinding
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.helpers.getStringByColumnName
import by.dashkevichpavel.osteopath.model.Attachment
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import java.io.File

class FragmentCustomerProfileAttachments :
    Fragment(R.layout.fragment_customer_profile_attachments) {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )
    private var fragmentBinding: FragmentCustomerProfileAttachmentsBinding? = null
    private val binding get() = fragmentBinding!!

    private var adapter = AttachmentItemAdapter()

    private val openFile = registerForActivityResult(OpenLocalDocument()) { fileUri: Uri ->
        /*val cursor = requireContext().contentResolver.query(
            fileUri,
            null,
            null,
            null,
            null
        ) ?: return@registerForActivityResult

        cursor.moveToFirst()

        Log.d("OsteoApp", "Uri: $fileUri")
        Log.d("OsteoApp", "type of Uri: ${requireContext().contentResolver.getType(fileUri)}")
        Log.d("OsteoApp", "display name: ${cursor.getStringByColumnName(OpenableColumns.DISPLAY_NAME, "")}")
        Log.d("OsteoApp", "canonical Uri: ${requireContext().contentResolver.canonicalize(fileUri)}")
        Log.d("OsteoApp", "uncanonicalized Uri: ${requireContext().contentResolver.uncanonicalize(fileUri)}")

        for (colIndex in 0 until cursor.columnCount) {
            when (cursor.getType(colIndex)) {
                Cursor.FIELD_TYPE_NULL ->
                    Log.d("OsteoApp", "column #$colIndex is null")
                Cursor.FIELD_TYPE_STRING ->
                    Log.d("OsteoApp", "column #$colIndex = ${cursor.getString(colIndex)}")
                Cursor.FIELD_TYPE_INTEGER ->
                    Log.d("OsteoApp", "column #$colIndex = ${cursor.getInt(colIndex)}")
                Cursor.FIELD_TYPE_FLOAT ->
                    Log.d("OsteoApp", "column #$colIndex = ${cursor.getFloat(colIndex)}")
                Cursor.FIELD_TYPE_BLOB ->
                    Log.d("OsteoApp", "column #$colIndex = ${cursor.getBlob(colIndex)}")
            }
        }

        val file = File(fileUri.path)

        Log.d("OsteoApp", "file = $file")*/

        viewModel.addAttachment(fileUri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun setupViews(view: View) {
        fragmentBinding = FragmentCustomerProfileAttachmentsBinding.bind(view)
        binding.rvAttachmentsList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvAttachmentsList.adapter = adapter
    }

    private fun setupEventListeners() {
        binding.fabAddAttachment.setOnClickListener {
            openMenu()
        }
    }

    private fun setupObservers() {
        viewModel.attachments.observe(viewLifecycleOwner, this::updateAttachmentsList)
        viewModel.startListeningForAttachmentsChanges()
    }

    private fun openMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.fabAddAttachment)
        popupMenu.inflate(R.menu.add_attachments_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.pick_file -> openFile.launch(
                    arrayOf(
                        "application/pdf",
                        "image/*",
                        "video/*",
                        "audio/*"
                    )
                )
                else -> return@setOnMenuItemClickListener false
            }
            true
        }
        popupMenu.show()
    }

    private fun updateAttachmentsList(newAttachmentsList: MutableList<Attachment>) {
        adapter.setItems(newAttachmentsList)
    }
}