package romHandlers

import configurations.RomConfiguration
import models.NDSRom
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen4RomHandler(romConfiguration: RomConfiguration, rom: NDSRom)
    : AbstractDSRomHandler(romConfiguration, rom) {
    override fun parseRom() {
        TODO("Not yet implemented")
    }
}