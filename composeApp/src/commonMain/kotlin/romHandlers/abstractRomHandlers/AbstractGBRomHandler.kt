package romHandlers.abstractRomHandlers

import configurations.RomConfiguration
import models.GBRom
import models.Rom

abstract class AbstractGBRomHandler(romConfiguration: RomConfiguration,
                                    protected var rom: GBRom)
    : AbstractRomHandler(romConfiguration)
{
     override fun saveROM(): Rom {
         return rom
     }
}