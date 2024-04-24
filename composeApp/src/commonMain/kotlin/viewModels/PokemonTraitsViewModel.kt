import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class BaseStatisticsMod {
    UNCHANGED, SHUFFLE_ORDER, SHUFFLE_BST, SHUFFLE_ALL, RANDOM_WITHIN_BST, RANDOM_UNRESTRICTED, RANDOM_COMPLETELY
}
object PokemonTraitsViewModel {
    var baseStatisticsModState by mutableStateOf(BaseStatisticsMod.UNCHANGED)
}