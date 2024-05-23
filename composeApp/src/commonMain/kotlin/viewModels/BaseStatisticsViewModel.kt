import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import viewModels.ModViewModel

enum class BaseStatisticsMod {
    UNCHANGED, SHUFFLE_ORDER, SHUFFLE_BST, SHUFFLE_ALL, RANDOM_WITHIN_BST, RANDOM_UNRESTRICTED, RANDOM_COMPLETELY
}
object BaseStatisticsViewModel: ModViewModel<BaseStatisticsMod> {
    private var baseStatisticsModState by mutableStateOf(BaseStatisticsMod.UNCHANGED)
    override fun getEnumValues(): Iterable<BaseStatisticsMod> {
        return BaseStatisticsMod.entries
    }

    override fun isSelected(state: BaseStatisticsMod): Boolean {
        return baseStatisticsModState == state
    }

    override fun setState(state: BaseStatisticsMod) {
        baseStatisticsModState = state
    }
}