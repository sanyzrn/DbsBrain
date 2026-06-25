package ir.dbsgraphic.secondbrain

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

/**
 * Copies a shared image into app-private storage so the capture survives after
 * the sharing app revokes the temporary URI permission. Everything stays local
 * (Constitution §10, §11).
 */
object ShareIngest {
    fun copyToBlobs(context: Context, uri: Uri): String? = try {
        val dir = File(context.filesDir, "blobs").apply { mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.blob")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}
