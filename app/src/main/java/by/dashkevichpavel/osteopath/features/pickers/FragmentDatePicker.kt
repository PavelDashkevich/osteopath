package by.dashkevichpavel.osteopath.features.pickers

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.model.setDateComponents
import java.util.*

class FragmentDatePicker :
    DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()

        if (savedInstanceState == null) {
            arguments?.let { args ->
                c.timeInMillis = args.getLong(BUNDLE_KEY_TIME_IN_MILLIS, 0L)
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
        val c = Calendar.getInstance()
        c.setDateComponents(year, month, dayOfMonth)
        setFragmentResult(KEY_RESULT, packBundle(c.timeInMillis))
    }

    companion object {
        const val KEY_RESULT = "DATE_PICKER_RESULT"
        const val BUNDLE_KEY_YEAR = "YEAR"
        const val BUNDLE_KEY_MONTH = "MONTH"
        const val BUNDLE_KEY_DAY_OF_MONTH = "DAY_OF_MONTH"
        private const val BUNDLE_KEY_TIME_IN_MILLIS = "TIME_IN_MILLIS"

        private fun packBundle(timeInMillis: Long): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_TIME_IN_MILLIS, timeInMillis)
            }
        }

        fun extractTimeInMillis(bundle: Bundle): Long {
            return bundle.getLong(BUNDLE_KEY_TIME_IN_MILLIS, 0L)
        }

        fun show(fragmentManager: FragmentManager, tag: String, timeInMillis: Long) {
            val fragment = FragmentDatePicker()
            fragment.arguments = packBundle(timeInMillis)
            fragment.show(fragmentManager, tag)
        }
    }
}