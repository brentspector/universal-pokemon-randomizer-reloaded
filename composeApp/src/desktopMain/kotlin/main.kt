import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composables.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Universal Pokemon Randomizer Reloaded") {
        App()
    }
}