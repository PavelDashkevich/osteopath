package by.dashkevichpavel.osteopath.features.datetimepicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class FragmentDatePicker :
    DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()

        if (savedInstanceState == null) {
            arguments?.let { args ->
                c.set(Calendar.YEAR, args.getInt(BUNDLE_KEY_YEAR, 0))
                c.set(Calendar.MONTH, args.getInt(BUNDLE_KEY_MONTH, 0))
                c.set(Calendar.DAY_OF_MONTH, args.getInt(BUNDLE_KEY_DAY_OF_MONTH, 0))
            }
        }

        return DatePickerDialog(
            requireContext(),
            this,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setFragmentResult(
            KEY_RESULT,
            Bundle().apply {
                putInt(BUNDLE_KEY_YEAR, year)
                putInt(BUNDLE_KEY_MONTH, month)
                putInt(BUNDLE_KEY_DAY_OF_MONTH, dayOfMonth)
            }
        )
    }

    companion object {
        const val KEY_RESULT = "DATE_PICKER_RESULT"
        const val BUNDLE_KEY_YEAR = "YEAR"
        const val BUNDLE_KEY_MONTH = "MONTH"
        const val BUNDLE_KEY_DAY_OF_MONTH = "DAY_OF_MONTH"
    }
}