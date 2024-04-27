package composables

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MyLifecycleObserver(private val registry : ActivityResultRegistry)
    : DefaultLifecycleObserver {
    lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, GetContent()) { uri -> processURI(uri) }
    }

    private fun processURI(uri: Uri?) {
        println(uri)
    }
}

lateinit var observer: MyLifecycleObserver
actual fun getFile() {
    observer.getContent.launch("image/*")
}