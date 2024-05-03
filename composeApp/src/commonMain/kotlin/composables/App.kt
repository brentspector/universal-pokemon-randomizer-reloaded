package composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // All components available can be found at https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary
        PokemonTraits()
        Settings()
    }
}

@Composable
fun Settings() {
    Button(
        onClick = {
            saveFile()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        // Button text
        Text("Button Thing")
    }
}


