package romHandlers.abstractRomHandlers

import configurations.Gen1RomConfiguration
import configurations.RomConfiguration
import models.GBRom
import models.Rom

abstract class AbstractGBRomHandler(romConfiguration: RomConfiguration,
                                    protected var rom: GBRom)
    : AbstractRomHandler(romConfiguration)
{
     override fun saveROM(): Rom {
         return rom
     }

    protected fun calculateOffset(offset: Int, pointer: Int, bankSize: Int): Int {
        return if (pointer < bankSize) {
            pointer
        } else {
            pointer % bankSize + (offset/bankSize) * bankSize
        }
    }

    protected fun readFixedLengthString(offset: Int, length: Int): String {
        return readString(offset, length, false)
    }

    protected fun readUnsignedByte(byte: Byte): Int {
        return byte.toInt() and 0xFF
    }

    protected fun readWord(offset: Int): Int {
        return readWord(rom.value, offset)
    }

    private fun readString(offset: Int, maxLength: Int, textEngineMode: Boolean): String {
        return buildString {
            for (c in 0 until maxLength) {
                val currChar: UByte = rom.value[offset + c].toUByte()
                val textChar = romConfiguration.textLookup.lookup(currChar)
                if (textChar != null) {
                    append(textChar)
                    if (textEngineMode && (textChar == "\\r" || textChar == "\\e")) {
                        break
                    }
                } else {
                    if (currChar == Gen1RomConfiguration.STRING_TERMINATOR.toUByte()) {
                        break
                    } else {
                        append("\\x${currChar.toString(16)}")
                    }
                }
            }
        }
    }

    private fun readWord(data: ByteArray, offset: Int): Int {
        return readUnsignedByte(data[offset]) + (readUnsignedByte(data[offset+1]) shl 8)
    }
}