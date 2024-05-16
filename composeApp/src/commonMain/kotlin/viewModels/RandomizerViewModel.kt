package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import configurations.RedVersionEnglish
import configurations.RomConfiguration
import configurations.SilverVersionEnglish
import logicModules.Randomizer


object RandomizerViewModel {
    private var rom: ByteArray = ByteArray(0)
    var randomizer by mutableStateOf(generateRandomizer())

    private val romConfigurations: Map<String, Lazy<RomConfiguration>> = mapOf(
        "Default" to lazy { RedVersionEnglish() },
        "Red" to lazy { RedVersionEnglish() },
        "Silver" to lazy { SilverVersionEnglish() }
    )

    fun loadROM(readFile: (ByteArray) -> Unit) {
        readFile(rom)
    }
    fun generateRandomizer(targetConfig: String = "Default"): Randomizer {
        val configFactory = romConfigurations[targetConfig]
            ?: throw Exception("No configuration for $targetConfig Found")

        val config = configFactory.value

        if (config.isLoadable()) {
            return Randomizer(config.create(rom))
        } else {
            throw Exception("ROMHandler for $targetConfig is not loadable.")
        }
    }
}