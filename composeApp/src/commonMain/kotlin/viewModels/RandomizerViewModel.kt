package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import configurations.RedVersionEnglish
import configurations.RomConfiguration
import configurations.SilverVersionEnglish
import logicModules.Randomizer
import romHandlers.RomHandler


object RandomizerViewModel {
    var randomizer by mutableStateOf(Randomizer(getRandomizer("Red")))
}

private val romHandlers: Map<String, Lazy<RomConfiguration>> = mapOf(
    "Red" to lazy { RedVersionEnglish() },
    "Silver" to lazy { SilverVersionEnglish() }
)

private fun getRandomizer(romHandler: String): RomHandler {
    val configFactory = romHandlers[romHandler]
        ?: throw Exception("No ROMHandler Found")

    val config = configFactory.value

    if (config.isLoadable()) {
        return config.create()
    } else {
        throw Exception("ROMHandler $romHandler is not loadable.")
    }
}