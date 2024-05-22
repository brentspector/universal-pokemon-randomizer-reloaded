package configurations

import romHandlers.Gen2RomHandler
import romHandlers.RomHandler

open class SilverVersionEnglish : RomConfiguration {
    override fun isLoadable(rom: ByteArray): Boolean {
        return true
    }
    override fun create(rom: ByteArray): RomHandler {
        return Gen2RomHandler()
    }
}

class GoldVersionEnglish : SilverVersionEnglish()

class CrystalVersionEnglish : SilverVersionEnglish()