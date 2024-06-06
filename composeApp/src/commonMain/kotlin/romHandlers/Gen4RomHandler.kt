package romHandlers

import configurations.RomConfiguration
import models.NDSFile
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen4RomHandler(romConfiguration: RomConfiguration, rom: NDSFile)
    : AbstractDSRomHandler(romConfiguration, rom) {

}