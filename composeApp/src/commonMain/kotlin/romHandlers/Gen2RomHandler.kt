package romHandlers

import configurations.RomConfiguration
import models.GBRom
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen2RomHandler(romConfiguration: RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {

}