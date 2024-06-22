package configurations

import models.GBRom
import models.Rom
import romHandlers.Gen2RomHandler
import romHandlers.abstractRomHandlers.AbstractRomHandler

open class SilverVersionEnglish : RomConfiguration {
    override val crcInHeader: Int
        get() = TODO("Not yet implemented")
    override val nonJapanese: Int
        get() = TODO("Not yet implemented")
    override val version: Int
        get() = TODO("Not yet implemented")
    override val romName: String
        get() = TODO("Not yet implemented")
    override val numStarters: Int = 3

    override fun isLoadable(rom: Rom): Boolean {
        return true
    }
    override fun create(rom: Rom): AbstractRomHandler {
        return Gen2RomHandler(this, rom as GBRom)
    }
}

class GoldVersionEnglish : SilverVersionEnglish()

class CrystalVersionEnglish : SilverVersionEnglish()