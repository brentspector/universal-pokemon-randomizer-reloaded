package romHandlers

import configurations.Gen1RomConfiguration
import models.GBRom
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen1RomHandler(romConfiguration: Gen1RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {
}