package composables

import viewModels.RandomizerViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Settings() {
    Button(
        onClick = {
            getFile()
            RandomizerViewModel.randomizer = RandomizerViewModel.generateRandomizer("Red")
        },
        modifier = Modifier.padding(16.dp)
    ) {
        // Button text
        Text("Load ROM")
    }
    Button(
        onClick = {
            saveFile()
            RandomizerViewModel.randomizer.randomize("Bob")
        },
        modifier = Modifier.padding(16.dp)
    ) {
        // Button text
        Text("Save File")
    }
}