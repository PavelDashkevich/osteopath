package by.dashkevichpavel.osteopath.features.customerprofile.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.features.datetimepicker.FragmentDatePicker
import by.dashkevichpavel.osteopath.features.customerprofile.CustomerProfileViewModel
import by.dashkevichpavel.osteopath.model.*
import by.dashkevichpavel.osteopath.viewmodel.OsteoViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class FragmentCustomerProfileContacts : Fragment(R.layout.fragment_customer_profile_contacts) {
    private val viewModel: CustomerProfileViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { OsteoViewModelFactory(requireContext().applicationContext) }
    )

    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
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

    /*override fun onAttach(context: Context) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")

        setupViews(view)
        setupListeners()

        viewModel.customer.observe(viewLifecycleOwner, this::updateFields)
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStart()
    }

    override fun onResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroy()
    }

    override fun onDestroyOptionsMenu() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroyOptionsMenu()
    }

    override fun onDetach() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDetach()
    }*/

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
}