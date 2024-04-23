package composables

import BaseStatisticsMod
import PokemonTraitsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun PokemonTraits() {

    val pokemonTraitsState = remember { PokemonTraitsViewModel() }

    Column(Modifier.selectableGroup()) {
        Text("Pokemon Base Stats")
        BaseStatisticsMod.entries.forEach { state ->
            Row {
                RadioButton(
                    selected = pokemonTraitsState.baseStatisticsModState == state,
                    onClick = { pokemonTraitsState.baseStatisticsModState = state },
                    modifier = Modifier.semantics { contentDescription = state.toString() }
                )
                Text(state.toString())
            }
        }
    }
}