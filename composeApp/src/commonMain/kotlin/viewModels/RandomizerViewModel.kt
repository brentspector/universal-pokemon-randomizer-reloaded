package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import configurations.RomConfiguration
import configurations.autodetectRom
import configurations.romConfigurations
import logicModules.Randomizer


object RandomizerViewModel {
    private var rom: ByteArray = ByteArray(0)
    var randomizer by mutableStateOf(generateRandomizer())

    fun loadROM(readBytes: ByteArray) {
        rom = readBytes
        randomizer = generateRandomizer(autoDetect = true)
    }

    fun generateRandomizer(targetConfig: String? = null, autoDetect: Boolean = false): Randomizer {
        val config = when {
            targetConfig != null -> getConfig(targetConfig)
            autoDetect -> autodetectConfig()
            else -> getDefaultConfig()
        }
        println(config)
        return Randomizer(config.create(rom))
    }

    private fun getConfig(targetConfig: String): RomConfiguration {
        println("getConfig")
        val config = romConfigurations[targetConfig]?.value
            ?: throw Exception("No configuration for $targetConfig Found")

        if (config.isLoadable(rom)) {
            return config
        } else {
            throw Exception("ROMHandler for $targetConfig is not loadable.")
        }
    }

    private fun getDefaultConfig(): RomConfiguration {
        println("getDefault")
        return romConfigurations["Default"]!!.value
    }

    private fun autodetectConfig(): RomConfiguration {
        println("autodetect")
        return autodetectRom(rom)
            ?: throw Exception("No suitable configuration found for ROM")
    }
}
