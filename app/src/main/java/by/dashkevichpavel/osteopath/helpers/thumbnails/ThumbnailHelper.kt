package by.dashkevichpavel.osteopath.helpers.thumbnails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import by.dashkevichpavel.osteopath.model.Attachment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ThumbnailHelper(private val context: Context) {
    fun getPdfThumbnailUriPath(attachment: Attachment): String {
        val fileDescriptor = context.contentResolver.openFileDescriptor(
            Uri.parse(attachment.path),
            "r"
        ) ?: return ""
        val renderer = PdfRenderer(fileDescriptor)
        val page = renderer.openPage(0)
        val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        renderer.close()

        val file = File(context.cacheDir, "thumbnail_${attachment.id}")

        try {
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: IOException) {
            Log.d("OsteoApp", "${this.javaClass.simpleName}: ${object{}.javaClass.enclosingMethod.name}: exception:")
            e.printStackTrace()
        }

        return file.absolutePath
    }
}