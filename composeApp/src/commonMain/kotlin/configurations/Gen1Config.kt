package configurations

import romHandlers.Gen1RomHandler
import romHandlers.RomHandler

open class RedVersionEnglish: RomConfiguration {
    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}

class BlueVersionEnglish : RedVersionEnglish()

class YellowVersionEnglish : RomConfiguration {
    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}