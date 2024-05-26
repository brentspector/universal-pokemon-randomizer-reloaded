package romHandlers

import configurations.RomConfiguration
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen5RomHandler(romConfiguration: RomConfiguration)
    : AbstractDSRomHandler(romConfiguration) {

        override fun saveROM(): Any {
            return ""
        }
}