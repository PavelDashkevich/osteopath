package by.dashkevichpavel.osteopath.features.settings.scheduler.workingdays

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.databinding.ListitemWorkingDaySettingsBinding
import by.dashkevichpavel.osteopath.helpers.datetime.DateTimeUtil
import by.dashkevichpavel.osteopath.helpers.formatTimeAsEditable
import by.dashkevichpavel.osteopath.helpers.toCapitalized
import by.dashkevichpavel.osteopath.helpers.toNameOfWeekDay
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettings
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettingsTimeField
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDayItemClickListener
import java.util.*

class WorkingDaySettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ListitemWorkingDaySettingsBinding.bind(itemView)

    fun bind(daySettings: WorkingDaySettings, clickListener: WorkingDayItemClickListener) {
        setupValues(daySettings)
        setupVisibility(daySettings)
        setupEventListeners(daySettings, clickListener)
    }

    private fun setupValues(daySettings: WorkingDaySettings) {
        binding.tvDayName.text = daySettings.dayOfWeek.toNameOfWeekDay().toCapitalized()
        binding.cbIsWorkingDay.isChecked = daySettings.isWorkingDay
        binding.cbRestIncluded.isChecked = daySettings.restIncluded
        binding.etDayStartTime.text =
            DateTimeUtil.durationToTime(daySettings.dayStartInMillis).formatTimeAsEditable()
        binding.etDayEndTime.text =
            DateTimeUtil.durationToTime(daySettings.dayEndInMillis).formatTimeAsEditable()
        binding.etRestStartTime.text =
            DateTimeUtil.durationToTime(daySettings.restStartInMillis).formatTimeAsEditable()
        binding.etRestEndTime.text =
            DateTimeUtil.durationToTime(daySettings.restEndInMillis).formatTimeAsEditable()
    }

    private fun setupVisibility(daySettings: WorkingDaySettings) {
        binding.tilDayStartTime.isVisible = daySettings.isWorkingDay
        binding.tilDayEndTime.isVisible = daySettings.isWorkingDay
        binding.cbRestIncluded.isVisible = daySettings.isWorkingDay
        binding.tilRestStartTime.isVisible = daySettings.isWorkingDay && daySettings.restIncluded
        binding.tilRestEndTime.isVisible = daySettings.isWorkingDay && daySettings.restIncluded
    }

    private fun setupEventListeners(
        daySettings: WorkingDaySettings,
        clickListener: WorkingDayItemClickListener
    ) {
        binding.etDayStartTime.setOnClickListener {
            clickListener.onWorkingDayTimeFieldClick(
                daySettings.dayOfWeek,
                WorkingDaySettingsTimeField.DAY_START,
                daySettings.dayStartInMillis
            )
        }
        binding.etDayEndTime.setOnClickListener {
            clickListener.onWorkingDayTimeFieldClick(
                daySettings.dayOfWeek,
                WorkingDaySettingsTimeField.DAY_END,
                daySettings.dayEndInMillis
            )
        }
        binding.etRestStartTime.setOnClickListener {
            clickListener.onWorkingDayTimeFieldClick(
                daySettings.dayOfWeek,
                WorkingDaySettingsTimeField.REST_START,
                daySettings.restStartInMillis
            )
        }
        binding.etRestEndTime.setOnClickListener {
            clickListener.onWorkingDayTimeFieldClick(
                daySettings.dayOfWeek,
                WorkingDaySettingsTimeField.REST_END,
                daySettings.restEndInMillis
            )
        }

        binding.cbIsWorkingDay.setOnClickListener {
            clickListener.onIsWorkingDayClick(
                daySettings.dayOfWeek,
                binding.cbIsWorkingDay.isChecked
            )
        }
        binding.cbRestIncluded.setOnClickListener {
            clickListener.onRestIncludedClick(
                daySettings.dayOfWeek,
                binding.cbRestIncluded.isChecked
            )
        }
    }
}