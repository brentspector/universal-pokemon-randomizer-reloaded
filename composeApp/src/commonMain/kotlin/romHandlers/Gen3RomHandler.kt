package romHandlers

import configurations.RomConfiguration
import models.GBRom
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen3RomHandler(romConfiguration: RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {
    override fun parseRom() {
        TODO("Not yet implemented")
    }
}