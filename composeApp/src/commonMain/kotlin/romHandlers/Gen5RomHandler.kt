package romHandlers

import configurations.RomConfiguration
import models.NDSFile
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen5RomHandler(romConfiguration: RomConfiguration, rom: NDSFile)
    : AbstractDSRomHandler(romConfiguration, rom) {

}