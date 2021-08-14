package by.dashkevichpavel.osteopath.features.customerprofile.contacts

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.FragmentCustomerProfileContactsBinding
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.pickers.FragmentDatePicker
import by.dashkevichpavel.osteopath.helpers.actionCallPhoneNumber
import by.dashkevichpavel.osteopath.helpers.actionOpenInstagram
import by.dashkevichpavel.osteopath.helpers.formatDateAsEditable
import by.dashkevichpavel.osteopath.helpers.permissions.PermissionsGrantedListener
import by.dashkevichpavel.osteopath.helpers.permissions.ReadContactsPermissionsHelper
import by.dashkevichpavel.osteopath.helpers.toEditable
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class FragmentCustomerProfileContacts :
    Fragment(R.layout.fragment_customer_profile_contacts),
    PermissionsGrantedListener {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private var fragmentCustomerProfileContactsBinding: FragmentCustomerProfileContactsBinding? = null
    private val binding get() = fragmentCustomerProfileContactsBinding!!

    private val getContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
        contactUri: Uri? -> onContactSet(contactUri)
    }

    private lateinit var readContactsPermissionsHelper: ReadContactsPermissionsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readContactsPermissionsHelper = ReadContactsPermissionsHelper(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(view)
        setupEventListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCustomerProfileContactsBinding = null
    }

    private fun updateFields(newCustomer: Customer?) {
        newCustomer?.let { customer ->
            binding.etName.text = customer.name.toEditable()
            updateFieldBirthDate(customer.birthDate)
            binding.etPhone.text = customer.phone.toEditable()
            binding.etSocialInstagram.text = customer.instagram.toEditable()
            when (customer.customerStatusId) {
                CustomerStatus.WORK.id -> binding.rbWork.isChecked = true
                CustomerStatus.WORK_DONE.id -> binding.rbWorkDone.isChecked = true
                CustomerStatus.NO_HELP.id -> binding.rbNoHelp.isChecked = true
            }
        }
    }

    private fun updateFieldBirthDate(birthDate: Date) {
        binding.etBirthDate.text = birthDate.formatDateAsEditable()
    }

    private fun setupViews(view: View) {
        fragmentCustomerProfileContactsBinding = FragmentCustomerProfileContactsBinding.bind(view)
    }

    private fun setupEventListeners() {
        childFragmentManager.setFragmentResultListener(
            FragmentDatePicker.KEY_RESULT,
            viewLifecycleOwner,
            ::onDatePickerDateSet
        )

        binding.etName.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerName(text.toString())
        }

        binding.etPhone.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerPhone(text.toString())
        }

        binding.etSocialInstagram.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerInstagram(text.toString())
        }

        binding.rgCategory.setOnCheckedChangeListener { group, checkedId ->
            viewModel.customer.value?.let { customer ->
                customer.customerStatusId =
                    when (checkedId) {
                        R.id.rbWork -> CustomerStatus.WORK.id
                        R.id.rbWorkDone -> CustomerStatus.WORK_DONE.id
                        R.id.rbNoHelp -> CustomerStatus.NO_HELP.id
                        else -> CustomerStatus.WORK.id
                    }
            }
        }

        binding.ibSetContact.setOnClickListener {
            readContactsPermissionsHelper.requestPermissions()
        }

        binding.ibSetBirthDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                "SET_BIRTH_DATE",
                viewModel.customer.value?.birthDate?.time ?: 0
            )
        }

        binding.ibCall.setOnClickListener {
            actionCallPhoneNumber(binding.etPhone.text.toString())
        }

        binding.ibInstagramMessage.setOnClickListener {
            actionOpenInstagram(binding.etSocialInstagram.text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.customer.observe(viewLifecycleOwner, this::updateFields)
    }

    private fun onDatePickerDateSet(key: String, bundle: Bundle) {
        viewModel.setCustomerBirthDateInMillis(FragmentDatePicker.extractTimeInMillis(bundle))
        viewModel.customer.value?.let { customer ->
            updateFieldBirthDate(customer.birthDate)
        }
    }

    private fun onContactSet(contactUri: Uri?) {
        viewModel.extractContactData(requireContext().contentResolver, contactUri)
    }

    override fun onPermissionsGranted() {
        getContact.launch(null)
    }
}