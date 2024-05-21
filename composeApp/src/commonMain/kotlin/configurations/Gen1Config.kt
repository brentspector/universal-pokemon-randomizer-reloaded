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
            add(RedVersionUSA())
            add(RedVersionFrance())
            add(RedVersionGermany())
            add(RedVersionItaly())
            add(RedVersionJapan())
            add(RedVersionSpain())
            add(BlueVersionUSA())
            add(BlueVersionFrance())
            add(BlueVersionGermany())
            add(BlueVersionItaly())
            add(BlueVersionJapan())
            add(BlueVersionSpain())
            add(YellowVersionUSA())
            add(YellowVersionFrance())
            add(YellowVersionGermany())
            add(YellowVersionItaly())
            add(YellowVersionJapan())
            add(YellowVersionSpain())
            add(GreenVersionJapan())
            add(GreenVersionJapanTranslatedEnglish())
        }

        fun autoDetectGen1Rom(rom: ByteArray): RomConfiguration? {
            if (rom.size < MIN_ROM_SIZE || rom.size > MAX_ROM_SIZE) {
                println("Returning null")
                return null
            }
            val version = rom[VERSION_OFFSET].toInt() and 0xFF
            val nonjap = rom[JP_FLAG_OFFSET].toInt() and 0xFF

            val romConfigWithCrc = roms.asSequence()
                .filter { it.crcInHeader != -1 }
                .find {
                    romSig(rom, it.romName) && it.version == version && it.nonJapanese == nonjap &&
                            crcInHeaderCheck(it.crcInHeader, rom)
                }

            val romConfigWithoutCrc = roms.asSequence()
                .filter { it.crcInHeader == -1 }
                .find { romSig(rom, it.romName) && it.version == version && it.nonJapanese == nonjap }

            // Returns first non-null value, or null if both are null
            return romConfigWithCrc ?: romConfigWithoutCrc
        }

        /**
         * Checks if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration.
         *
         * @param romConfigCRC The CRC value stored in the ROM configuration.
         * @param rom The byte array representing the ROM.
         * @return `true` if the CRC value extracted from the ROM header matches the CRC value stored in the ROM configuration, `false` otherwise.
         */
        private fun crcInHeaderCheck(romConfigCRC: Int, rom: ByteArray): Boolean {
            return romConfigCRC == (rom[CRC_OFFSET].toInt() and 0xFF shl 8 or (rom[CRC_OFFSET + 1].toInt() and 0xFF))
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
open class RedVersionUSA: Gen1RomConfiguration() {
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

class RedVersionJapan : RedVersionUSA() {
    override val nonJapanese: Int = 0
}

class RedVersionFrance : RedVersionUSA() {
    override val crcInHeader: Int = 0x7AFC
}

class RedVersionSpain : RedVersionUSA() {
    override val crcInHeader: Int = 0x384A
}

class RedVersionGermany : RedVersionUSA() {
    override val crcInHeader: Int = 0x5CDC
}

class RedVersionItaly : RedVersionUSA() {
    override val crcInHeader: Int = 0x89D2
}

open class BlueVersionUSA : RedVersionUSA() {
    override val romName: String = "POKEMON BLUE"
}

class BlueVersionJapan : BlueVersionUSA() {
    override val nonJapanese: Int = 0
}

class BlueVersionFrance : BlueVersionUSA() {
    override val crcInHeader: Int = 0x56A4
}

class BlueVersionSpain : BlueVersionUSA() {
    override val crcInHeader: Int = 0x14D7
}

class BlueVersionGermany : BlueVersionUSA() {
    override val crcInHeader: Int = 0x2EBC
}

class BlueVersionItaly : BlueVersionUSA() {
    override val crcInHeader: Int = 0x5E9C
}

open class YellowVersionUSA : RedVersionUSA() {
    override val romName: String = "POKEMON YELLOW"
}

class YellowVersionJapan : YellowVersionUSA() {
    override val nonJapanese: Int = 0
}

class YellowVersionFrance : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSF"
}

class YellowVersionSpain : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSS"
}

class YellowVersionGermany : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSD"
}

class YellowVersionItaly : YellowVersionUSA() {
    override val romName: String = "POKEMON YELAPSI"
}

class GreenVersionJapan : RedVersionUSA() {
    override val nonJapanese: Int = 0
    override val romName: String = "POKEMON GREEN"
}

class GreenVersionJapanTranslatedEnglish : RedVersionUSA() {
    override val crcInHeader: Int = 0xF57E
    override val nonJapanese: Int = 0
    override val romName: String = "POKEMON GREEN"
}