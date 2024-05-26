package romHandlers

import configurations.Gen1RomConfiguration
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen1RomHandler(romConfiguration: Gen1RomConfiguration, rom: ByteArray)
    : AbstractGBRomHandler(romConfiguration, rom) {
}