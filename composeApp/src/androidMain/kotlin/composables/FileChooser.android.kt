package composables

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class FileChooserLifecycleObserver(private val registry : ActivityResultRegistry)
    : DefaultLifecycleObserver {
    lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, GetContent()) { uri -> processURI(uri) }
    }

    private fun processURI(uri: Uri?) {
        println(uri)
    }
}

lateinit var fileChooserObserver: FileChooserLifecycleObserver
actual fun getFile() {
    fileChooserObserver.getContent.launch("image/*")
}

actual fun saveFile() {
    TODO("Not yet implemented")
}