package com.brentspector.upr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import composables.App
import composables.FileChooserLifecycleObserver
import composables.fileChooserObserver


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileChooserObserver = FileChooserLifecycleObserver(activityResultRegistry)
        lifecycle.addObserver(fileChooserObserver)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}