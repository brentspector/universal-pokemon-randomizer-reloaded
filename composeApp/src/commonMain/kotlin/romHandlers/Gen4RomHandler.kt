package romHandlers

import configurations.RomConfiguration
import romHandlers.abstractRomHandlers.AbstractDSRomHandler

class Gen4RomHandler(romConfiguration: RomConfiguration)
    : AbstractDSRomHandler(romConfiguration) {

        override fun saveROM(): Any {
            return ""
        }
}