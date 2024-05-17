package configurations

import romHandlers.Gen1RomHandler
import romHandlers.RomHandler

abstract class Gen1RomConfiguration: RomConfiguration {
    abstract val crcInHeader: Int
    abstract val nonJapanese: Int
    abstract val version: Int
    abstract val romName: String

    companion object {
        private const val MIN_ROM_SIZE = 0x80000
        private const val MAX_ROM_SIZE = 0x200000

        private const val JP_FLAG_OFFSET = 0x14A
        private const val VERSION_OFFSET = 0x14C
        private const val CRC_OFFSET = 0x14E
        private const val ROM_SIG_OFFSET = 0x134

        private val roms = mutableListOf<Gen1RomConfiguration>().apply {
            add(RedVersionEnglish())
            add(BlueVersionEnglish())
            add(YellowVersionEnglish())
        }

        fun autoDetectGen1Rom(rom: ByteArray): RomConfiguration? {
            if (rom.size < MIN_ROM_SIZE || rom.size > MAX_ROM_SIZE) {
                println("Returning null")
                return null
            }
            val version = rom[VERSION_OFFSET].toInt() and 0xFF
            val nonjap = rom[JP_FLAG_OFFSET].toInt() and 0xFF
            for (re in roms) {
                if (romSig(
                        rom,
                        re.romName
                    ) && re.version == version && re.nonJapanese == nonjap
                ) {
                    // No CRC provided in header for this RomConfiguration
                    if (re.crcInHeader == -1) {
                        return re
                    }
                    // CRC is provided and should match
                    else {
                        val crcInHeader = (rom[CRC_OFFSET].toInt() and 0xFF shl 8
                                or (rom[CRC_OFFSET + 1].toInt() and 0xFF))
                        if (re.crcInHeader == crcInHeader) {
                            return re
                        }
                    }
                }
            }
            return null
        }

        private fun romSig(rom: ByteArray, sig: String): Boolean {
            val sigBytes: ByteArray = sig.encodeToByteArray() //sig.getBytes("US-ASCII")
            for (i in sigBytes.indices) {
                if (rom[ROM_SIG_OFFSET + i] != sigBytes[i]) {
                    return false
                }
            }
            return true
        }
    }
}
open class RedVersionEnglish: Gen1RomConfiguration() {
    override val crcInHeader: Int = -1
    override val nonJapanese: Int = 1
    override val version: Int = 0
    override val romName: String = "POKEMON RED"

    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}

class BlueVersionEnglish : RedVersionEnglish() {
    override val romName: String = "POKEMON BLUE"
}

class YellowVersionEnglish : RedVersionEnglish() {
    override val romName: String = "POKEMON YELLOW"

    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}