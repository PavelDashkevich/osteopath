package by.dashkevichpavel.osteopath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import by.dashkevichpavel.osteopath.helpers.backups.BackupHelper
import by.dashkevichpavel.osteopath.services.AutoBackupWorkManager
import by.dashkevichpavel.osteopath.services.AutoBackupWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(R.layout.activity_main), BackClickHandler {
    private val backClickListeners = mutableListOf<BackClickListener?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AutoBackupWorkManager(applicationContext).setupWork()
    }

    override fun onBackPressed() {
        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        if (!isBackInterceptedByFragments()) {
            super.onBackPressed()
        }
    }

    private fun isBackInterceptedByFragments(): Boolean {
        var intercepted = false

        for (backClickListener in backClickListeners) {
            intercepted = intercepted || (backClickListener?.onBackClick() == true)
        }

        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: intercepted = $intercepted")

        return intercepted
    }

    override fun addBackClickListener(backClickListener: BackClickListener) {
        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        backClickListeners.add(backClickListener)
    }

    override fun removeBackClickListener(backClickListener: BackClickListener) {
        //Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        backClickListeners.remove(backClickListener)
    }
}

interface BackClickListener {
    fun onBackClick(): Boolean
}

interface BackClickHandler {
    fun addBackClickListener(backClickListener: BackClickListener)
    fun removeBackClickListener(backClickListener: BackClickListener)
}