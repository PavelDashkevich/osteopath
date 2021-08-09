package by.dashkevichpavel.osteopath.features.customerprofile.contacts

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.features.pickers.FragmentDatePicker
import by.dashkevichpavel.osteopath.helpers.permissions.PermissionsGrantedListener
import by.dashkevichpavel.osteopath.helpers.permissions.ReadContactsPermissionsHelper
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

    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var ibSetContact: AppCompatImageButton
    private lateinit var tilBirthDate: TextInputLayout
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var ibSetBirthDate: AppCompatImageButton
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etPhone: TextInputEditText
    private lateinit var ibCall: AppCompatImageButton
    private lateinit var tilSocialInstagram: TextInputLayout
    private lateinit var etSocialInstagram: TextInputEditText
    private lateinit var ibInstagramMessage: AppCompatImageButton
    private lateinit var rgCategory: RadioGroup
    private lateinit var rbCategoryWork: RadioButton
    private lateinit var rbCategoryWorkDone: RadioButton
    private lateinit var rbCategoryNoHelp: RadioButton

    private val getContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
        contactUri: Uri? -> onContactSet(contactUri)
    }

    private lateinit var readContactsPermissionsHelper: ReadContactsPermissionsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readContactsPermissionsHelper = ReadContactsPermissionsHelper(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupListeners()

        viewModel.customer.observe(viewLifecycleOwner, this::updateFields)
    }

    private fun updateFields(newCustomer: Customer?) {
        newCustomer?.let { customer ->
            etName.text = customer.name.toEditable()
            updateFieldBirthDate(customer.birthDate)
            etPhone.text = customer.phone.toEditable()
            etSocialInstagram.text = customer.instagram.toEditable()
            when (customer.customerStatusId) {
                CustomerStatus.WORK.id -> rbCategoryWork.isChecked = true
                CustomerStatus.WORK_DONE.id -> rbCategoryWorkDone.isChecked = true
                CustomerStatus.NO_HELP.id -> rbCategoryNoHelp.isChecked = true
            }
        }
    }

    private fun updateFieldBirthDate(birthDate: Date) {
        etBirthDate.text = birthDate.formatDateAsEditable()
    }

    private fun setupViews(view: View) {
        tilName = view.findViewById(R.id.til_name)
        etName = view.findViewById(R.id.et_name)
        ibSetContact = view.findViewById(R.id.ib_set_contact)
        tilBirthDate = view.findViewById(R.id.til_birth_date)
        etBirthDate = view.findViewById(R.id.et_birth_date)
        ibSetBirthDate = view.findViewById(R.id.ib_set_birth_date)
        tilPhone = view.findViewById(R.id.til_phone)
        etPhone = view.findViewById(R.id.et_phone)
        ibCall = view.findViewById(R.id.ib_call)
        tilSocialInstagram = view.findViewById(R.id.til_social_instagram)
        etSocialInstagram = view.findViewById(R.id.et_social_instagram)
        ibInstagramMessage = view.findViewById(R.id.ib_instagram_message)
        rgCategory = view.findViewById(R.id.rg_category)
        rbCategoryWork = view.findViewById(R.id.rb_work)
        rbCategoryWorkDone = view.findViewById(R.id.rb_work_done)
        rbCategoryNoHelp = view.findViewById(R.id.rb_no_help)
    }

    private fun setupListeners() {
        childFragmentManager.setFragmentResultListener(
            FragmentDatePicker.KEY_RESULT,
            viewLifecycleOwner,
            ::onDatePickerDateSet
        )

        etName.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerName(text.toString())
        }

        etPhone.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerPhone(text.toString())
        }

        etSocialInstagram.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerInstagram(text.toString())
        }

        rgCategory.setOnCheckedChangeListener { group, checkedId ->
            viewModel.customer.value?.let { customer ->
                customer.customerStatusId =
                    when (checkedId) {
                        R.id.rb_work -> CustomerStatus.WORK.id
                        R.id.rb_work_done -> CustomerStatus.WORK_DONE.id
                        R.id.rb_no_help -> CustomerStatus.NO_HELP.id
                        else -> CustomerStatus.WORK.id
                    }
            }
        }

        ibSetContact.setOnClickListener {
            readContactsPermissionsHelper.requestPermissions()
        }

        ibSetBirthDate.setOnClickListener {
            FragmentDatePicker.show(
                childFragmentManager,
                "SET_BIRTH_DATE",
                viewModel.customer.value?.birthDate?.time ?: 0
            )
        }

        ibCall.setOnClickListener {
            actionCallPhoneNumber(etPhone.text.toString())
        }

        ibInstagramMessage.setOnClickListener {
            actionOpenInstagram(etSocialInstagram.text.toString())
        }
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