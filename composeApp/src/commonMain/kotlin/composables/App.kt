package composables

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        // All components available can be found at https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
        PokemonTraits()
    }
}