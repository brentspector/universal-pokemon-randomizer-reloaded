package configurations

import romHandlers.Gen1RomHandler
import romHandlers.RomHandler

abstract class Gen1RomConfiguration: RomConfiguration {
    abstract val crcInHeader: Int
    abstract val nonJapanese: Int
    abstract val version: Int
    abstract val romName: String

    companion object {
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
            val version = rom[VERSION_OFFSET].toInt() and 0xFF
            val nonjap = rom[JP_FLAG_OFFSET].toInt() and 0xFF
            // Check for specific CRC first
            val crcInHeader = (rom[CRC_OFFSET].toInt() and 0xFF shl 8
                    or (rom[CRC_OFFSET + 1].toInt() and 0xFF))
            for (re in roms) {
                if (romSig(
                        rom,
                        re.romName
                    ) && re.version == version && re.nonJapanese == nonjap && re.crcInHeader == crcInHeader
                ) {
                    return re
                }
            }
            // Now check for non-specific-CRC entries
            for (re in roms) {
                if (romSig(
                        rom,
                        re.romName
                    ) && re.version == version && re.nonJapanese == nonjap && re.crcInHeader == -1
                ) {
                    return re
                }
            }
            return null
        }

        private fun romSig(rom: ByteArray, sig: String): Boolean {
            val sigOffset: Int = ROM_SIG_OFFSET
            val sigBytes: ByteArray = sig.encodeToByteArray() //sig.getBytes("US-ASCII")
            for (i in sigBytes.indices) {
                if (rom[sigOffset + i] != sigBytes[i]) {
                    return false
                }
            }
            return true
        }
    }
}
open class RedVersionEnglish: Gen1RomConfiguration() {
    override val crcInHeader: Int
        get() = TODO("Not yet implemented")
    override val nonJapanese: Int
        get() = TODO("Not yet implemented")
    override val version: Int
        get() = TODO("Not yet implemented")
    override val romName: String
        get() = TODO("Not yet implemented")

    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}

class BlueVersionEnglish : RedVersionEnglish()

class YellowVersionEnglish : Gen1RomConfiguration() {
    override val crcInHeader: Int
        get() = TODO("Not yet implemented")
    override val nonJapanese: Int
        get() = TODO("Not yet implemented")
    override val version: Int
        get() = TODO("Not yet implemented")
    override val romName: String
        get() = TODO("Not yet implemented")

    override fun isLoadable(): Boolean {
        return true
    }

    override fun create(rom: ByteArray): RomHandler {
        return Gen1RomHandler()
    }
}