package by.dashkevichpavel.osteopath.features.sessions

import android.util.Log
import androidx.lifecycle.ViewModel

class SessionsViewModel : ViewModel() {
    var test: Int = 0

    override fun onCleared() {
        super.onCleared()
        Log.d("OsteoApp", "SessionListViewModel: onCleared()")
    }
}