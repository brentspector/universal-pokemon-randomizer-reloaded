package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import configurations.autodetectRom
import configurations.romConfigurations
import logicModules.Randomizer


object RandomizerViewModel {
    private var rom: ByteArray = ByteArray(0)
    var randomizer by mutableStateOf(generateRandomizer())

    fun loadROM(readBytes: ByteArray) {
        rom = readBytes
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

    fun autoGenerateRandomizer(): Randomizer {
        val config = autodetectRom(rom)
        return Randomizer(config.create(rom))
    }
}

