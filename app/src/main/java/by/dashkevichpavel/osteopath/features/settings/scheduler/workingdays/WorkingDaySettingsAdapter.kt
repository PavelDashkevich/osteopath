package by.dashkevichpavel.osteopath.features.settings.scheduler.workingdays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDaySettings
import by.dashkevichpavel.osteopath.repositories.settings.scheduler.WorkingDayItemClickListener

class WorkingDaySettingsAdapter(
    private val workingDayItemClickListener: WorkingDayItemClickListener
) : RecyclerView.Adapter<WorkingDaySettingsViewHolder>() {
    private val workingDaySettingsItems: MutableList<WorkingDaySettings> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkingDaySettingsViewHolder = WorkingDaySettingsViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_working_day_settings, parent, false)
        )

    override fun onBindViewHolder(holder: WorkingDaySettingsViewHolder, position: Int) {
        holder.bind(workingDaySettingsItems[position], workingDayItemClickListener)
    }

    override fun getItemCount(): Int = workingDaySettingsItems.size

    fun setItems(newItems: List<WorkingDaySettings>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(workingDaySettingsItems, newItems))
        workingDaySettingsItems.clear()
        workingDaySettingsItems.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}