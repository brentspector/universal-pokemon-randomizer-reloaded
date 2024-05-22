/**
 * Classes and utilities related to ROM configurations for Gen 1 ROMs.
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
package configurations

import romHandlers.Gen1RomHandler
import romHandlers.RomHandler

/**
 * Abstract class to define the behavior of all Gen 1 ROMs. Exposes shared behaviors as companion objects
 */
abstract class Gen1RomConfiguration: RomConfiguration {
    /**
     * CRC value stored in the ROM header.
     */
    abstract val crcInHeader: Int

    /**
     * Bit flag indicating whether the ROM is non-Japanese.
     */
    abstract val nonJapanese: Int

    /**
     * Version number of the ROM.
     */
    abstract val version: Int

    /**
     * Name of the ROM.
     */
    abstract val romName: String

    companion object {
        // Constants for shared Gen 1 ROM attributes
        private const val MIN_ROM_SIZE = 0x80000
        private const val MAX_ROM_SIZE = 0x200000
        private const val JP_FLAG_OFFSET = 0x14A
        private const val VERSION_OFFSET = 0x14C
        private const val CRC_OFFSET = 0x14E
        private const val ROM_SIG_OFFSET = 0x134

        // List of ROM configurations for loops
        private val roms = mutableListOf<Gen1RomConfiguration>().apply {
            add(RedVersionUSA())
            add(RedVersionFrance())
            add(RedVersionGermany())
            add(RedVersionItaly())
            add(RedVersionJapan())
            add(RedVersionSpain())
            add(BlueVersionUSA())
            add(BlueVersionFrance())
            add(BlueVersionGermany())
            add(BlueVersionItaly())
            add(BlueVersionJapan())
            add(BlueVersionSpain())
            add(YellowVersionUSA())
            add(YellowVersionFrance())
            add(YellowVersionGermany())
            add(YellowVersionItaly())
            add(YellowVersionJapan())
            add(YellowVersionSpain())
            add(GreenVersionJapan())
            add(GreenVersionJapanTranslatedEnglish())
        }

        /**
         * Auto-detects the appropriate Gen 1 ROM configuration based on the provided ROM byte array.
         *
         * @param rom The byte array representing the ROM.
         * @return The detected ROM configuration, or null if none is found.
         */
        fun autoDetectGen1Rom(rom: ByteArray): RomConfiguration? {
            // Check ROM size validity
            if (rom.size < MIN_ROM_SIZE || rom.size > MAX_ROM_SIZE) {
                println("Returning null")
                return null
            }

            // Get the version and japanese flags from ROM
            val version = rom[VERSION_OFFSET].toInt() and 0xFF
            val nonjap = rom[JP_FLAG_OFFSET].toInt() and 0xFF

            // Find ROM configuration with CRC and without CRC
            val romConfigWithCrc = roms.asSequence()
                .filter { it.crcInHeader != -1 }
                .find {
                    romSig(rom, it.romName) && it.version == version && it.nonJapanese == nonjap &&
                            crcInHeaderCheck(it.crcInHeader, rom)
                }

            val romConfigWithoutCrc = roms.asSequence()
                .filter { it.crcInHeader == -1 }
                .find { romSig(rom, it.romName) && it.version == version && it.nonJapanese == nonjap }

            // Returns first non-null value, or null if both are null
            return romConfigWithCrc ?: romConfigWithoutCrc
        }

        /**
         * Checks if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration.
         *
         * @param romConfigCRC The CRC value stored in the ROM configuration.
         * @param rom The byte array representing the ROM.
         * @return `true` if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration, `false` otherwise.
         */
        private fun crcInHeaderCheck(romConfigCRC: Int, rom: ByteArray): Boolean {
            return romConfigCRC == (rom[CRC_OFFSET].toInt() and 0xFF shl 8 or (rom[CRC_OFFSET + 1].toInt() and 0xFF))
        }

        /**
         * Checks if the ROM signature matches the specified signature.
         *
         * @param rom The byte array representing the ROM.
         * @param sig The ROM signature to compare against.
         * @return `true` if the ROM signature matches the specified signature, `false` otherwise.
         */
        private fun romSig(rom: ByteArray, sig: String): Boolean {
            val sigBytes: ByteArray = sig.encodeToByteArray() //sig.getBytes("US-ASCII")
            for (i in sigBytes.indices) {
                if (rom[ROM_SIG_OFFSET + i] != sigBytes[i]) {
                    return false
                }
            }
            return true
        }
    }
}

/**
 * RedVersionUSA represents the configuration for the US version of Pokémon Red version.
 *
 * This is the basic configuration that all other configurations derive from.
 * The most frequent occurrence of values should be specified here, and overridden
 * in subclasses.
 */
open class RedVersionUSA: Gen1RomConfiguration() {
    override val crcInHeader: Int = -1
    override val nonJapanese: Int = 1
    override val version: Int = 0
    override val romName: String = "POKEMON RED"

    /**
     * Checks if the ROM is loadable.
     *
     * @return `true` if the ROM is loadable, `false` otherwise.
     */
    override fun isLoadable(): Boolean {
        return true
    }

    /**
     * Creates a ROM handler for Gen 1 ROMs using the configuration
     * of the invoked class.
     *
     * @param rom The byte array representing the ROM.
     * @return A ROM handler for Gen 1 ROMs.
     */
    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}

/**
 * RedVersionJapan represents the configuration for the Japanese version of Pokémon Red version.
 */
class RedVersionJapan : RedVersionUSA() {
    override val nonJapanese: Int = 0
}

/**
 * RedVersionFrance represents the configuration for the French version of Pokémon Red version.
 */
class RedVersionFrance : RedVersionUSA() {
    override val crcInHeader: Int = 0x7AFC
}

/**
 * RedVersionSpain represents the configuration for the Spanish version of Pokémon Red version.
 */
class RedVersionSpain : RedVersionUSA() {
    override val crcInHeader: Int = 0x384A
}

/**
 * RedVersionGermany represents the configuration for the German version of Pokémon Red version.
 */
class RedVersionGermany : RedVersionUSA() {
    override val crcInHeader: Int = 0x5CDC
}

/**
 * RedVersionItaly represents the configuration for the Italian version of Pokémon Red version.
 */
class RedVersionItaly : RedVersionUSA() {
    override val crcInHeader: Int = 0x89D2
}

/**
 * BlueVersionUSA represents the configuration for the US version of Pokémon Blue version.
 *
 * If Blue version has a shared value that is different than Red version, it should be
 * specified here rather than in every subclass
 */
open class BlueVersionUSA : RedVersionUSA() {
    override val romName: String = "POKEMON BLUE"
}

/**
 * BlueVersionJapan represents the configuration for the Japanese version of Pokémon Blue version.
 */
class BlueVersionJapan : BlueVersionUSA() {
    override val nonJapanese: Int = 0
}

/**
 * BlueVersionFrance represents the configuration for the French version of Pokémon Blue version.
 */
class BlueVersionFrance : BlueVersionUSA() {
    override val crcInHeader: Int = 0x56A4
}

/**
 * BlueVersionSpain represents the configuration for the Spanish version of Pokémon Blue version.
 */
class BlueVersionSpain : BlueVersionUSA() {
    override val crcInHeader: Int = 0x14D7
}

/**
 * BlueVersionGermany represents the configuration for the German version of Pokémon Blue version.
 */
class BlueVersionGermany : BlueVersionUSA() {
    override val crcInHeader: Int = 0x2EBC
}

/**
 * BlueVersionItaly represents the configuration for the Italian version of Pokémon Blue version.
 */
class BlueVersionItaly : BlueVersionUSA() {
    override val crcInHeader: Int = 0x5E9C
}

/**
 * YellowVersionUSA represents the configuration for the US version of Pokémon Yellow version.
 *
 * If Yellow version has a shared value that is different than Red version, it should be
 * specified here rather than in every subclass
 */
open class YellowVersionUSA : RedVersionUSA() {
    override val romName: String = "POKEMON YELLOW"
}

/**
 * YellowVersionJapan represents the configuration for the Japanese version of Pokémon Yellow version.
 */
class YellowVersionJapan : YellowVersionUSA() {
    override val nonJapanese: Int = 0
}

/**
 * YellowVersionFrance represents the configuration for the French version of Pokémon Yellow version.
 */
class YellowVersionFrance : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSF"
}

/**
 * YellowVersionSpain represents the configuration for the Spanish version of Pokémon Yellow version.
 */
class YellowVersionSpain : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSS"
}

/**
 * YellowVersionGermany represents the configuration for the German version of Pokémon Yellow version.
 */
class YellowVersionGermany : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSD"
}

/**
 * YellowVersionItaly represents the configuration for the Italian version of Pokémon Yellow version.
 */
class YellowVersionItaly : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSI"
}

/**
 * GreenVersionJapan represents the configuration for the Japanese version of Pokémon Green version.
 */
class GreenVersionJapan : RedVersionUSA() {
    override val nonJapanese: Int = 0
    override val romName: String = "POKEMON GREEN"
}

/**
 * GreenVersionJapanTranslatedEnglish represents the configuration for the English-translated Japanese version of Pokémon Green version.
 */
class GreenVersionJapanTranslatedEnglish : RedVersionUSA() {
    override val crcInHeader: Int = 0xF57E
    override val nonJapanese: Int = 0
    override val romName: String = "POKEMON GREEN"
}