package romHandlers

import configurations.RomConfiguration
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen2RomHandler(romConfiguration: RomConfiguration, rom: ByteArray)
    : AbstractGBRomHandler(romConfiguration, rom) {

}