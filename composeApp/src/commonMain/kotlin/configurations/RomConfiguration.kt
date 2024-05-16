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