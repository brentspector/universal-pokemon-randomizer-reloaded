package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class StarterPokemonMod {
    UNCHANGED, CUSTOM, RANDOM
}
object StarterPokemonViewModel: ModViewModel<StarterPokemonMod> {
    private var starterPokemonModState by mutableStateOf(StarterPokemonMod.UNCHANGED)
    override fun getState(): StarterPokemonMod {
        return starterPokemonModState
    }

    override fun setState(state: StarterPokemonMod) {
        starterPokemonModState = state
    }

    override fun getEnumValues(): Iterable<StarterPokemonMod> {
        return StarterPokemonMod.entries
    }

    override fun isSelected(state: StarterPokemonMod): Boolean {
        return starterPokemonModState == state
    }
}
