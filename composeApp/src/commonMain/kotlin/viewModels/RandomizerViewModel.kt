/**
 * Classes and utilities related to Randomizer state
 *
 *  * BSD 3-Clause License
 *  *
 *  * Copyright (c) 2024, Brent Spector
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice,
 *  *    this list of conditions and the following disclaimer.
 *  *
 *  * 2. Redistributions in binary form must reproduce the above copyright
 *  *    notice, this list of conditions and the following disclaimer in the
 *  *    documentation and/or other materials provided with the distribution.
 *  *
 *  * 3. Neither the name of the copyright holder nor the names of its
 *  *    contributors may be used to endorse or promote products derived from
 *  *    this software without specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  * POSSIBILITY OF SUCH DAMAGE.
 */
package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import configurations.RomConfiguration
import configurations.autodetectRom
import configurations.romConfigurations
import logicModules.Randomizer


/**
 * Singleton object representing the view model for managing ROM randomization.
 * This object handles ROM data, generating randomizers, and observing changes
 * to the randomizer state.
 */
object RandomizerViewModel {
    /** The byte array representing the currently loaded ROM. */
    private var rom: ByteArray = ByteArray(0)

    /**
     * A mutable state reference to the current randomizer instance.
     * Android tracks this throughout activity lifecycle events
     */
    var randomizer by mutableStateOf(generateRandomizer())

    /**
     * Loads a new ROM into the view model.
     * @param readBytes The byte array representing the ROM to be loaded.
     * Automatically generates a randomizer using auto-detection.
     */
    fun loadROM(readBytes: ByteArray) {
        rom = readBytes
        randomizer = generateRandomizer(autoDetect = true)
    }

    fun saveROM(): Any {
        return randomizer.saveROM()
    }

    /**
     * Generates a randomizer instance based on the provided parameters.
     * @param targetConfig The name of the configuration to use. If null, attempts to
     * autodetect or uses the default configuration if autodetect is false.
     * @param autoDetect Whether to automatically detect a suitable configuration for the loaded ROM.
     * @return A randomizer instance based on the specified configuration.
     */
    fun generateRandomizer(targetConfig: String? = null, autoDetect: Boolean = false): Randomizer {
        val config = when {
            targetConfig != null -> getConfig(targetConfig)
            autoDetect -> autodetectConfig()
            else -> getDefaultConfig()
        }
        return Randomizer(config.create(rom))
    }

    /**
     * Retrieves the configuration with the specified name.
     * @param targetConfig The name of the configuration to retrieve.
     * @return The configuration with the specified name.
     * @throws Exception if no configuration with the specified name is found or if the configuration is not loadable.
     */
    private fun getConfig(targetConfig: String): RomConfiguration {
        val config = romConfigurations[targetConfig]?.value
            ?: throw Exception("No configuration for $targetConfig Found")

        if (config.isLoadable(rom)) {
            return config
        } else {
            throw Exception("ROMHandler for $targetConfig is not loadable.")
        }
    }

    /**
     * Retrieves the default configuration.
     * @return The default configuration.
     * @throws Exception if the default configuration is not loadable.
     */
    private fun getDefaultConfig(): RomConfiguration {
        return romConfigurations["Default"]!!.value
    }

    /**
     * Automatically detects a suitable configuration for the loaded ROM.
     * @return A suitable configuration for the loaded ROM.
     * @throws Exception if no suitable configuration is found for the loaded ROM.
     */
    private fun autodetectConfig(): RomConfiguration {
        return autodetectRom(rom)
            ?: throw Exception("No suitable configuration found for ROM")
    }
}
