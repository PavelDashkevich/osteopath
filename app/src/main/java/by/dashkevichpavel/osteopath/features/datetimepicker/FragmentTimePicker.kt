package by.dashkevichpavel.osteopath.features.datetimepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.model.setTimeComponents
import java.util.*

class FragmentTimePicker :
    DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()

        if (savedInstanceState == null) {
            arguments?.let { args ->
                c.timeInMillis = args.getLong(BUNDLE_KEY_TIME_IN_MILLIS, 0L)
            }
        }

        return TimePickerDialog(
            requireContext(),
            this,
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.setTimeComponents(hourOfDay, minute)
        setFragmentResult(KEY_RESULT, packBundle(c.timeInMillis))
    }

    companion object {
        const val KEY_RESULT = "TIME_PICKER_RESULT"
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
            val fragment = FragmentTimePicker()
            fragment.arguments = packBundle(timeInMillis)
            fragment.show(fragmentManager, tag)
        }
    }
}