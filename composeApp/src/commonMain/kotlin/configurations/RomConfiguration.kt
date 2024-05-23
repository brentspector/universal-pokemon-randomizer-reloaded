package configurations

import romHandlers.RomHandler

interface RomConfiguration {
    fun isLoadable(rom: ByteArray): Boolean
    fun create(rom: ByteArray): RomHandler
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