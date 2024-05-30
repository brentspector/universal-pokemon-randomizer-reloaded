package composables

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import viewModels.RandomizerViewModel.loadROM
import viewModels.RandomizerViewModel.saveROM
import java.io.FileOutputStream

class FileChooserLifecycleObserver(private val registry: ActivityResultRegistry, private val contentResolver: ContentResolver)
    : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    lateinit var saveContent: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("processURI", owner, GetContent()) { uri -> processURI(uri) }
        saveContent = registry.register("writeFile", owner, CreateDocument(mimeType)) { uri ->
            writeFile(contentResolver, uri)
        }
    }

    private fun processURI(uri: Uri?) {
        try {
            if (uri != null) {
                contentResolver.openInputStream(uri)?.use {
                    loadROM(it.readBytes())
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun writeFile(contentResolver: ContentResolver, uri: Uri?) {
        try {
            val rom : Any = saveROM()

            if (rom is ByteArray) {
                saveBytes(contentResolver, uri, rom)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun saveBytes(contentResolver: ContentResolver, uri: Uri?, rom: ByteArray) {
        if (uri != null) {
            contentResolver.openFileDescriptor(uri, "w")?.use { fileDescriptor ->
                FileOutputStream(fileDescriptor.fileDescriptor).use {
                    it.write(rom)
                }
            }
        }
    }
}


lateinit var fileChooserObserver: FileChooserLifecycleObserver
const val mimeType = "application/octet-stream"
actual fun getFile() {
    fileChooserObserver.getContent.launch(mimeType)
}

actual fun saveFile() {
    fileChooserObserver.saveContent.launch("myimage.txt")
}