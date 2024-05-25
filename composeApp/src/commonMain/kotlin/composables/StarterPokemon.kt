package composables

import androidx.compose.runtime.Composable
import viewModels.StarterPokemonViewModel

@Composable
fun StarterPokemon() {
    RadioButtonGroup("Starter Pokemon", StarterPokemonViewModel)
}
