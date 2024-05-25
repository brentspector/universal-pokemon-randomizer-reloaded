package configurations

import romHandlers.Gen2RomHandler
import romHandlers.RomHandler

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

    override fun isLoadable(rom: ByteArray): Boolean {
        return true
    }
    override fun create(rom: ByteArray): RomHandler {
        return Gen2RomHandler(this)
    }
}

class GoldVersionEnglish : SilverVersionEnglish()

class CrystalVersionEnglish : SilverVersionEnglish()