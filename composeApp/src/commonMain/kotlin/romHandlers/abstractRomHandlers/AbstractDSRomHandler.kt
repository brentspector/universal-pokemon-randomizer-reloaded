package romHandlers.abstractRomHandlers

import configurations.RomConfiguration
import models.NDSFile
import models.NDSRom
import models.Rom

abstract class AbstractDSRomHandler(romConfiguration: RomConfiguration,
                                    protected var rom: NDSFile)
    : AbstractRomHandler(romConfiguration)
{
    override fun saveROM(): Rom {
        return NDSRom(rom)
    }
}