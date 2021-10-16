package by.dashkevichpavel.osteopath.features.customerprofile.attachments

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class OpenLocalDocument : ActivityResultContracts.OpenDocument() {
    override fun createIntent(context: Context, input: Array<out String>): Intent {
        return super
            .createIntent(context, input)
            .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    }
}