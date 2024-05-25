package composables

import BaseStatisticsViewModel
import androidx.compose.runtime.Composable

@Composable
fun PokemonTraits() {
    RadioButtonGroup("Pokemon Base Stats", BaseStatisticsViewModel)
}