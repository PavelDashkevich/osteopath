package by.dashkevichpavel.osteopath.features.settings.scheduler

interface SessionDurationItemActionListener {
    fun onSessionDurationClick(action: SessionDurationItemAction)
}

sealed class SessionDurationItemAction {
    class Delete(val durationInMillis: Long) : SessionDurationItemAction()
}