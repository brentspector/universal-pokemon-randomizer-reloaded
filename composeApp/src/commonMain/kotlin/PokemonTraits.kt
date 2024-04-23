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

    Row(Modifier.selectableGroup()) {
        Text("Pokemon Base Stats")
        BaseStatisticsMod.entries.forEach { state ->
                Text(state.toString())
                RadioButton(
                    selected = pokemonTraitsState.baseStatisticsModState == state,
                    onClick = { pokemonTraitsState.baseStatisticsModState = state },
                    modifier = Modifier.semantics { contentDescription = state.toString() }
                )
        }
    }
}

//        var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
// This is from compose-multiplatform.xml and Res.kt
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }