package by.dashkevichpavel.osteopath.helpers.savechanges

import android.view.View
import by.dashkevichpavel.osteopath.R
import com.google.android.material.snackbar.Snackbar

class SavingStateIndicator(view: View) {
    private var sbSaving: Snackbar = Snackbar.make(
        view,
        R.string.snackbar_saving_changes,
        Snackbar.LENGTH_INDEFINITE
    )

    fun onSavingStateChanges(savingState: SavingState) {
        when (savingState) {
            SavingState.NOT_STARTED -> return
            SavingState.STARTED -> {
                if (!sbSaving.isShownOrQueued) {
                    sbSaving.show()
                }
            }
            SavingState.COMPLETED -> {
                if (sbSaving.isShownOrQueued) {
                    sbSaving.dismiss()
                }
            }
        }
    }
}