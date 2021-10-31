package by.dashkevichpavel.osteopath.helpers.recyclerviewutils

import android.widget.TextView
import androidx.core.view.isVisible

class SessionViewHolderUtil {
    companion object {
        fun setContentAndVisibilityOfBlock(headerView: TextView, textView: TextView, text: String) {
            textView.text = text
            headerView.isVisible = text.isNotBlank()
            textView.isVisible = text.isNotBlank()
        }
    }
}