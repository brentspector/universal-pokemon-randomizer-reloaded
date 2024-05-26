package romHandlers.abstractRomHandlers

import configurations.RomConfiguration

abstract class AbstractGBRomHandler(romConfiguration: RomConfiguration,
                                    protected var rom: ByteArray)
    : AbstractRomHandler(romConfiguration)
{
     override fun saveROM(): ByteArray {
         return rom
     }
}