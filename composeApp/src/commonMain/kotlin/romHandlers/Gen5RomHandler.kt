package romHandlers

import configurations.RomConfiguration
import models.NDSRom
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen5RomHandler(romConfiguration: RomConfiguration, rom: NDSRom)
    : AbstractDSRomHandler(romConfiguration, rom) {

}