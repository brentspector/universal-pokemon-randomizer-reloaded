package configurations

import romHandlers.RomHandler

interface RomConfiguration {
    fun isLoadable() : Boolean
    fun create(rom: ByteArray): RomHandler
}