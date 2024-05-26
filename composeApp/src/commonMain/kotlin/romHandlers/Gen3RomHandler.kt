package romHandlers

import configurations.RomConfiguration
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen3RomHandler(romConfiguration: RomConfiguration, rom: ByteArray)
    : AbstractGBRomHandler(romConfiguration, rom) {

}