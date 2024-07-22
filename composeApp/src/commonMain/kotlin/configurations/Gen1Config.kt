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

import models.GBRom
import models.Rom
import pokemon.Type
import romHandlers.Gen1RomHandler
import romHandlers.abstractRomHandlers.AbstractRomHandler

/**
 * Abstract class to define the behavior of all Gen 1 ROMs. Exposes shared behaviors as companion objects
 */
abstract class Gen1RomConfiguration: RomConfiguration {
    companion object {
        // Constants for shared Gen 1 ROM attributes
        const val MIN_ROM_SIZE = 0x80000
        const val MAX_ROM_SIZE = 0x200000
        const val BANK_SIZE = 0x4000
        const val JP_FLAG_OFFSET = 0x14A
        const val VERSION_OFFSET = 0x14C
        const val CRC_OFFSET = 0x14E
        const val ROM_SIG_OFFSET = 0x134
        const val STRING_TERMINATOR = 0x50
        const val MEW_INDEX = 151
        const val MAROWAK_INDEX = 105
        const val BASE_STATS_ENTRY_SIZE = 0x1C
        const val BASE_STATS_HP_OFFSET = 1
        const val BASE_STATS_ATTACK_OFFSET = 2
        const val BASE_STATS_DEFENSE_OFFSET = 3
        const val BASE_STATS_SPEED_OFFSET = 4
        const val BASE_STATS_SPECIAL_OFFSET = 5
        const val BASE_STATS_PRIMARY_TYPE_OFFSET = 6
        const val BASE_STATS_SECONDARY_TYPE_OFFSET = 7
        const val BASE_STATS_CATCH_RATE_OFFSET = 8
        const val BASE_STATS_EXP_YIELD_OFFSET = 9
        const val BASE_STATS_LEVEL1_MOVES_OFFSET = 15
        const val BASE_STATS_GROWTH_CURVE_OFFSET = 19
        const val BASE_STATS_TM_HM_COMPAT_OFFSET = 20


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
         * @param rom The GBRom object representing the ROM.
         * @return The detected ROM configuration, or null if none is found.
         */
        fun autoDetectGen1Rom(rom: GBRom): RomConfiguration? {
            // Check ROM size validity
            if (rom.value.size < MIN_ROM_SIZE || rom.value.size > MAX_ROM_SIZE) {
                return null
            }

            // Find ROM configuration with a matching CRC
            return roms.find { it.crcInHeader != -1 && it.isLoadable(rom) }
            // If no match is found, find ROM configuration without CRC, or null if no match found
                ?: return roms.find { it.crcInHeader == -1 && it.isLoadable(rom) }
        }

        /**
         * Checks if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration.
         *
         * @param romConfigCRC The CRC value stored in the ROM configuration.
         * @param rom The byte array representing the ROM.
         * @return `true` if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration, `false` otherwise.
         */
        fun crcInHeaderCheck(romConfigCRC: Int, rom: ByteArray): Boolean {
            return romConfigCRC == (rom[CRC_OFFSET].toInt() and 0xFF shl 8 or (rom[CRC_OFFSET + 1].toInt() and 0xFF))
        }

        /**
         * Checks if the ROM signature matches the specified signature.
         *
         * @param rom The byte array representing the ROM.
         * @param sig The ROM signature to compare against.
         * @return `true` if the ROM signature matches the specified signature, `false` otherwise.
         */
        fun romSig(rom: ByteArray, sig: String): Boolean {
            val sigBytes: ByteArray = sig.encodeToByteArray() //sig.getBytes("US-ASCII")
            for (i in sigBytes.indices) {
                if (rom[ROM_SIG_OFFSET + i] != sigBytes[i]) {
                    return false
                }
            }
            return true
        }
    } // end companion object

    /**
     * Checks if the ROM is loadable.
     *
     * @return `true` if the ROM is loadable, `false` otherwise.
     */
    override fun isLoadable(rom: Rom): Boolean {
        if (rom !is GBRom)
            return false

        // Check ROM size validity
        if (rom.value.size < MIN_ROM_SIZE || rom.value.size > MAX_ROM_SIZE) {
            return false
        }

        // Get the version and japanese flags from ROM
        val romVersion = rom.value[VERSION_OFFSET].toInt() and 0xFF
        val nonjap = rom.value[JP_FLAG_OFFSET].toInt() and 0xFF

        return if (crcInHeader != -1) {
            romSig(rom.value, romName) && version == romVersion && nonJapanese == nonjap &&
                    crcInHeaderCheck(crcInHeader, rom.value)
        } else {
            romSig(rom.value, romName) && version == romVersion && nonJapanese == nonjap
        }
    }

    /**
     * Creates a ROM handler for Gen 1 ROMs using the configuration
     * of the invoked class.
     *
     * @param rom The byte array representing the ROM.
     * @return A ROM handler for Gen 1 ROMs.
     */
    override fun create(rom: Rom): AbstractRomHandler {
        return Gen1RomHandler(this, rom as GBRom)
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
    override val numStarters: Int = 3
    // TODO: Update these values
    override val pokemonNamesOffset: Int= 0x1C21E
    override val pokemonNamesLength: Int = 10
    override val pokedexOrderOffset: Int = 0x41024
    override val pokemonMovesetsTableOffset: Int = 0x3B05C
    override val internalPokemonCount: Int = 190
    override val pokemonStatsOffset: Int = 0x383DE
    override val mewStatsOffset: Int = 0x425B
    override val textLookup: TextLookup = TextLookup.RBY_ENGLISH
    override val typeTable: MutableMap<Int, Type> = mutableMapOf(
        0x00 to Type.NORMAL,
        0x01 to Type.FIGHTING,
        0x02 to Type.FLYING,
        0x03 to Type.POISON,
        0x04 to Type.GROUND,
        0x05 to Type.ROCK,
        0x07 to Type.BUG,
        0x08 to Type.GHOST,
        0x14 to Type.FIRE,
        0x15 to Type.WATER,
        0x16 to Type.GRASS,
        0x17 to Type.ELECTRIC,
        0x18 to Type.PSYCHIC,
        0x19 to Type.ICE,
        0x1A to Type.DRAGON
    )
}

/**
 * RedVersionJapan represents the configuration for the Japanese version of Pokémon Red version.
 */
class RedVersionJapan : RedVersionUSA() {
    override val nonJapanese: Int = 0
    override val pokemonStatsOffset: Int = 0x38000
    override val mewStatsOffset: Int = 0x4200
    override val pokemonNamesOffset: Int = 0x39068
    override val pokemonNamesLength: Int = 5
    override val pokedexOrderOffset: Int = 0x4279A
}

/**
 * RedVersionFrance represents the configuration for the French version of Pokémon Red version.
 */
class RedVersionFrance : RedVersionUSA() {
    override val crcInHeader: Int = 0x7AFC
    override val pokedexOrderOffset: Int = 0x40FAA
}

/**
 * RedVersionSpain represents the configuration for the Spanish version of Pokémon Red version.
 */
class RedVersionSpain : RedVersionUSA() {
    override val crcInHeader: Int = 0x384A
    override val pokedexOrderOffset: Int = 0x40FB4
}

/**
 * RedVersionGermany represents the configuration for the German version of Pokémon Red version.
 */
class RedVersionGermany : RedVersionUSA() {
    override val crcInHeader: Int = 0x5CDC
    override val pokedexOrderOffset: Int = 0x40F96
}

/**
 * RedVersionItaly represents the configuration for the Italian version of Pokémon Red version.
 */
class RedVersionItaly : RedVersionUSA() {
    override val crcInHeader: Int = 0x89D2
    override val pokedexOrderOffset: Int = 0x40FB6
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
    override val pokemonStatsOffset: Int = 0x383DE
    override val mewStatsOffset: Int = 0x425B
    override val pokemonNamesOffset: Int = 0x39446
    override val pokemonNamesLength: Int = 5
    override val pokedexOrderOffset: Int = 0x42784
}

/**
 * BlueVersionFrance represents the configuration for the French version of Pokémon Blue version.
 */
class BlueVersionFrance : BlueVersionUSA() {
    override val crcInHeader: Int = 0x56A4
    override val pokedexOrderOffset: Int = 0x40FAA
}

/**
 * BlueVersionSpain represents the configuration for the Spanish version of Pokémon Blue version.
 */
class BlueVersionSpain : BlueVersionUSA() {
    override val crcInHeader: Int = 0x14D7
    override val pokedexOrderOffset: Int = 0x40FB4
}

/**
 * BlueVersionGermany represents the configuration for the German version of Pokémon Blue version.
 */
class BlueVersionGermany : BlueVersionUSA() {
    override val crcInHeader: Int = 0x2EBC
    override val pokedexOrderOffset: Int = 0x40F96
}

/**
 * BlueVersionItaly represents the configuration for the Italian version of Pokémon Blue version.
 */
class BlueVersionItaly : BlueVersionUSA() {
    override val crcInHeader: Int = 0x5E9C
    override val pokedexOrderOffset: Int = 0x40FB6
}

/**
 * YellowVersionUSA represents the configuration for the US version of Pokémon Yellow version.
 *
 * If Yellow version has a shared value that is different than Red version, it should be
 * specified here rather than in every subclass
 */
open class YellowVersionUSA : RedVersionUSA() {
    override val romName: String = "POKEMON YELLOW"
    override val numStarters: Int = 2
    override val mewStatsOffset: Int = 0
    override val pokemonNamesOffset: Int = 0xE8000
    override val pokedexOrderOffset: Int = 0x410B1
    override val pokemonMovesetsTableOffset: Int = 0x3B1E5
}

/**
 * YellowVersionJapan represents the configuration for the Japanese version of Pokémon Yellow version.
 */
class YellowVersionJapan : YellowVersionUSA() {
    override val nonJapanese: Int = 0
    override val pokemonStatsOffset: Int = 0x383DE
    override val mewStatsOffset: Int = 0
    override val pokemonNamesOffset: Int = 0x39462
    override val pokemonNamesLength: Int = 5
    override val pokedexOrderOffset: Int = 0x4282D
}

/**
 * YellowVersionFrance represents the configuration for the French version of Pokémon Yellow version.
 */
class YellowVersionFrance : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSF"
    override val pokedexOrderOffset: Int = 0x41036
}

/**
 * YellowVersionSpain represents the configuration for the Spanish version of Pokémon Yellow version.
 */
class YellowVersionSpain : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSS"
    override val pokedexOrderOffset: Int = 0x41041

}

/**
 * YellowVersionGermany represents the configuration for the German version of Pokémon Yellow version.
 */
class YellowVersionGermany : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSD"
    override val pokedexOrderOffset: Int = 0x41023
}

/**
 * YellowVersionItaly represents the configuration for the Italian version of Pokémon Yellow version.
 */
class YellowVersionItaly : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSI"
    override val pokedexOrderOffset: Int = 0x41043
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