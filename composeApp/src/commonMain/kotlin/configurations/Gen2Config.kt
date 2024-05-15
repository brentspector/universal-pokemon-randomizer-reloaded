package configurations

import romHandlers.Gen2RomHandler
import romHandlers.RomHandler

open class SilverVersionEnglish : RomConfiguration {
    override fun isLoadable(): Boolean {
        return true
    }
    override fun create(): RomHandler {
        return Gen2RomHandler()
    }
}

class GoldVersionEnglish : SilverVersionEnglish()

class CrystalVersionEnglish : SilverVersionEnglish()