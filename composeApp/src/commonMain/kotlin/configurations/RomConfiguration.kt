package configurations

import models.GBRom
import models.NDSRom
import models.Rom
import pokemon.Type
import romHandlers.abstractRomHandlers.AbstractRomHandler

interface RomConfiguration {
    /**
     * CRC value stored in the ROM header.
     */
    val crcInHeader: Int

    /**
     * Bit flag indicating whether the ROM is non-Japanese.
     */
    val nonJapanese: Int

    /**
     * Version number of the ROM.
     */
    val version: Int

    /**
     * Name of the ROM.
     */
    val romName: String

    /**
     * Number of starters this game supports
     */
    val numStarters: Int

    val pokemonNamesOffset: Int
    val pokemonNamesLength: Int
    val internalPokemonCount: Int
    val pokemonStatsOffset: Int
    val mewStatsOffset: Int
    val textLookup: TextLookup
    val typeTable: MutableMap<Int, Type>

    fun isLoadable(rom: Rom): Boolean
    fun create(rom: Rom): AbstractRomHandler
}

val romConfigurations: Map<String, Lazy<RomConfiguration>> = mapOf(
    "Default" to lazy { RedVersionUSA() },
    "Red" to lazy { RedVersionUSA() },
    "Silver" to lazy { SilverVersionEnglish() }
)

fun autodetectRom(rom: Rom): RomConfiguration? {
    when (rom) {
        is GBRom -> {
            Gen1RomConfiguration.autoDetectGen1Rom(rom)?.let { return it }
        }
        is NDSRom -> {

        }
    }

    // Not found
    return null
}