package romHandlers.abstractRomHandlers

import configurations.Gen1RomConfiguration
import configurations.RomConfiguration
import models.GBRom
import models.Rom

abstract class AbstractGBRomHandler(
    romConfiguration: RomConfiguration,
    protected var rom: GBRom
) : AbstractRomHandler(romConfiguration) {

    override fun saveROM(): Rom = rom

    protected fun calculateOffset(offset: Int, pointer: Int, bankSize: Int): Int {
        return if (pointer < bankSize) pointer else pointer % bankSize + (offset / bankSize) * bankSize
    }

    protected fun readFixedLengthString(offset: Int, length: Int): String {
        return readString(offset, length, false)
    }

    protected fun readUnsignedByte(byte: Byte): Int = byte.toInt() and 0xFF

    protected fun readWord(offset: Int): Int = readWord(rom.value, offset)

    private fun readString(offset: Int, maxLength: Int, textEngineMode: Boolean): String {
        return buildString {
            for (c in 0 until maxLength) {
                val currChar = rom.value[offset + c].toUByte()
                val textChar = romConfiguration.textLookup.lookup(currChar)
                if (textChar != null) {
                    append(textChar)
                    if (textEngineMode && (textChar == "\\r" || textChar == "\\e")) break
                } else {
                    if (currChar == Gen1RomConfiguration.STRING_TERMINATOR.toUByte()) break
                    else append("\\x${Gen1RomConfiguration.STRING_TERMINATOR.toString(16)}")
                }
            }
        }
    }

    private fun readWord(data: ByteArray, offset: Int): Int {
        return readUnsignedByte(data[offset]) + (readUnsignedByte(data[offset + 1]) shl 8)
    }

    protected fun writeFixedLengthString(str: String, offset: Int, length: Int) {
        val translated = translateString(str)
        var len = minOf(translated.size, length)
        translated.copyInto(rom.value, destinationOffset = offset, startIndex = 0, endIndex = len)
        while (len < length) {
            rom.value[offset + len] = Gen1RomConfiguration.STRING_TERMINATOR.toByte()
            len++
        }
    }

    private fun translateString(text: String): ByteArray {
        val byteList = mutableListOf<Byte>()
        val longestTableToken = romConfiguration.getLongestTokenLength()
        var textCopy = text
        while (textCopy.isNotEmpty()) {
            var i = maxOf(0, longestTableToken - textCopy.length)
            textCopy = if (textCopy.startsWith("\\x")) {
                // This is probably STRING_TERMINATOR. Since this special character is in base 16, we only
                // take the next 2 characters from the string
                byteList.add(textCopy.substring(2, 4).toInt(16).toByte())
                textCopy.substring(4)
            } else {
                var d: Byte? = null
                while (d == null && i < longestTableToken) {
                    d = romConfiguration.textLookup.getByteFromString(textCopy.substring(0, longestTableToken - i))
                    i++
                }
                if (d == null) {
                    textCopy.substring(1)
                } else {
                    byteList.add(readUnsignedByte(d).toByte())
                    textCopy.substring(longestTableToken - i + 1)
                }
            }
        }
        return byteList.toByteArray()
    }
}