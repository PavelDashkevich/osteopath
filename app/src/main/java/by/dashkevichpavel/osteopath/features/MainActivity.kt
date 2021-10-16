package by.dashkevichpavel.osteopath.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import by.dashkevichpavel.osteopath.R

class MainActivity : AppCompatActivity(R.layout.activity_main), BackClickHandler {
    private val backClickListeners = mutableListOf<BackClickListener?>()

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPostCreate(savedInstanceState)
    }

    override fun onContentChanged() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onContentChanged()
    }

    override fun onStart() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResume()
    }

    override fun onResumeFragments() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onResumeFragments()
    }

    override fun onPostResume() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPostResume()
    }

    override fun onAttachedToWindow() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onAttachedToWindow()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStop() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onStop()
    }

    override fun onRestart() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}")
        super.onDestroy()
    }*/

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