package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import viewModels.StarterPokemonMod
import viewModels.StarterPokemonViewModel

@Composable
fun StarterPokemon() {
    Column(Modifier.selectableGroup()) {
        Text("Starter Pokemon")
        StarterPokemonMod.entries.forEach { state ->
            Row {
                RadioButton(
                    selected = StarterPokemonViewModel.starterPokemonModState == state,
                    onClick = { StarterPokemonViewModel.starterPokemonModState = state },
                    modifier = Modifier.semantics { contentDescription = state.toString() }
                )
                Text(state.toString())
            }
        }
    }
}