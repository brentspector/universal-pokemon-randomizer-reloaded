package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class StarterPokemonMod {
    UNCHANGED, CUSTOM, RANDOM
}
object StarterPokemonViewModel {
    var starterPokemonModState by mutableStateOf(StarterPokemonMod.UNCHANGED)
}