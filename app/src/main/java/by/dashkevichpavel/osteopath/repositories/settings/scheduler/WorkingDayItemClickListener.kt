package by.dashkevichpavel.osteopath.repositories.settings.scheduler

interface WorkingDayItemClickListener {
    fun onWorkingDayTimeFieldClick(
        dayOfWeek: Int,
        timeField: WorkingDaySettingsTimeField,
        initialTimeInMillis: Long
    )
    fun onIsWorkingDayClick(dayOfWeek: Int, isChecked: Boolean)
    fun onRestIncludedClick(dayOfWeek: Int, isChecked: Boolean)
}

enum class WorkingDaySettingsTimeField {
    DAY_START,
    DAY_END,
    REST_START,
    REST_END
}