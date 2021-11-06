package by.dashkevichpavel.osteopath.features.pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil
import by.dashkevichpavel.osteopath.helpers.setTimeComponents
import java.util.*

class FragmentTimePicker :
    DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    private var additionalBundle: Bundle? = null
    private var mode = TimePickerMode.TIME

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()

        arguments?.let { args ->
            additionalBundle = extractAdditionalBundle(args)
            mode = TimePickerMode.values()[args.getInt(BUNDLE_KEY_TIME_PICKER_MODE, 0)]
        }

        if (savedInstanceState == null) {
            arguments?.let { args ->
                val timeInMillis = args.getLong(BUNDLE_KEY_TIME_IN_MILLIS, 0L)
                when (mode) {
                    TimePickerMode.TIME -> c.timeInMillis = timeInMillis
                    TimePickerMode.DURATION -> {
                        val hourOfDayAndMinutes =
                            DateTimeUtil.getHourOfDayAndMinutesFromDuration(timeInMillis)
                        c.setTimeComponents(hourOfDayAndMinutes.first, hourOfDayAndMinutes.second)
                    }
                }
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
        val timeInMillis = when (mode) {
            TimePickerMode.TIME -> {
                c.setTimeComponents(hourOfDay, minute)
                c.timeInMillis
            }
            TimePickerMode.DURATION ->
                DateTimeUtil.getDurationInMillis(hourOfDay, minute)
        }
        setFragmentResult(
            tag ?: KEY_RESULT,
            packResultBundle(timeInMillis, additionalBundle)
        )
    }

    companion object {
        const val KEY_RESULT = "TIME_PICKER_RESULT"
        private const val BUNDLE_KEY_TIME_IN_MILLIS = "TIME_IN_MILLIS"
        private const val BUNDLE_KEY_TIME_PICKER_MODE = "TIME_PICKER_MODE"
        private const val BUNDLE_KEY_ADDITIONAL_BUNDLE = "ADDITIONAL_BUNDLE"

        private fun packArgsBundle(
            timeInMillis: Long,
            mode: TimePickerMode,
            additionalBundle: Bundle? = null
        ): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_TIME_IN_MILLIS, timeInMillis)
                putInt(BUNDLE_KEY_TIME_PICKER_MODE, mode.ordinal)
                additionalBundle?.let { bundle -> putBundle(BUNDLE_KEY_ADDITIONAL_BUNDLE, bundle) }
            }
        }

        private fun packResultBundle(
            timeInMillis: Long,
            additionalBundle: Bundle? = null
        ): Bundle {
            return Bundle().apply {
                putLong(BUNDLE_KEY_TIME_IN_MILLIS, timeInMillis)
                additionalBundle?.let { bundle -> putBundle(BUNDLE_KEY_ADDITIONAL_BUNDLE, bundle) }
            }
        }

        fun extractTimeInMillis(bundle: Bundle): Long =
            bundle.getLong(BUNDLE_KEY_TIME_IN_MILLIS, 0L)

        fun extractAdditionalBundle(bundle: Bundle): Bundle? =
            bundle.getBundle(BUNDLE_KEY_ADDITIONAL_BUNDLE)

        fun show(
            fragmentManager: FragmentManager,
            tag: String,
            timeInMillis: Long,
            mode: TimePickerMode = TimePickerMode.TIME,
            additionalBundle: Bundle? = null
        ) {
            val fragment = FragmentTimePicker()
            fragment.arguments = packArgsBundle(timeInMillis, mode, additionalBundle)
            fragment.show(fragmentManager, tag)
        }
    }

    enum class TimePickerMode {
        TIME,
        DURATION
    }
}