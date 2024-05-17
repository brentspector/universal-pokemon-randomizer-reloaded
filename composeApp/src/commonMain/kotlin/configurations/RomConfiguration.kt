package configurations

import romHandlers.RomHandler

interface RomConfiguration {
    fun isLoadable() : Boolean
    fun create(rom: ByteArray): RomHandler
}

val romConfigurations: Map<String, Lazy<RomConfiguration>> = mapOf(
    "Default" to lazy { RedVersionEnglish() },
    "Red" to lazy { RedVersionEnglish() },
    "Silver" to lazy { SilverVersionEnglish() }
)

fun autodetectRom(rom: ByteArray): RomConfiguration {
    Gen1RomConfiguration.autoDetectGen1Rom(rom)?.let { return it }

    // Not found
    val default = romConfigurations["Default"] ?: throw Exception("No default configuration found")
    return default.value
}