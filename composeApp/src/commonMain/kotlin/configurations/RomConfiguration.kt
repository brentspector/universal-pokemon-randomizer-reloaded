package configurations

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

    fun isLoadable(rom: ByteArray): Boolean
    fun create(rom: ByteArray): AbstractRomHandler
}

val romConfigurations: Map<String, Lazy<RomConfiguration>> = mapOf(
    "Default" to lazy { RedVersionUSA() },
    "Red" to lazy { RedVersionUSA() },
    "Silver" to lazy { SilverVersionEnglish() }
)

fun autodetectRom(rom: ByteArray): RomConfiguration? {
    Gen1RomConfiguration.autoDetectGen1Rom(rom)?.let { return it }

    // Not found
    return null
}