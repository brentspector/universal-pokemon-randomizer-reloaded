package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        // All components available can be found at https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
        Column(
            Modifier.verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            PokemonTraits()
            PokemonTraits()
            PokemonTraits()
            PokemonTraits()
            PokemonTraits()
            Settings()
        }
    }
}


