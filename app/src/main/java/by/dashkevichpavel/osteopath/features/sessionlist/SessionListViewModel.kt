package by.dashkevichpavel.osteopath.features.sessionlist

import android.util.Log
import androidx.lifecycle.ViewModel

class SessionListViewModel : ViewModel() {
    var test: Int = 0

    override fun onCleared() {
        super.onCleared()
        Log.d("OsteoApp", "SessionListViewModel: onCleared()")
    }
}