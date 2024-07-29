package configurations

class TextLookup(private val map: MutableMap<Int, String>) {
    companion object {
        val GAMEBOY_JAP: TextLookup = TextLookup(mutableMapOf(
            0x05 to "ガ",
            0x06 to "ギ",
            0x07 to "グ",
            0x08 to "ゲ",
            0x09 to "ゴ",
            0x0A to "ザ",
            0x0B to "ジ",
            0x0C to "ズ",
            0x0D to "ゼ",
            0x0E to "ゾ",
            0x0F to "ダ",
            0x10 to "ヂ",
            0x11 to "ヅ",
            0x12 to "デ",
            0x13 to "ド",
            0x19 to "バ",
            0x1A to "ビ",
            0x1B to "ブ",
            0x1C to "ボ",
            0x26 to "が",
            0x27 to "ぎ",
            0x28 to "ぐ",
            0x29 to "げ",
            0x2A to "ご",
            0x2B to "ざ",
            0x2C to "じ",
            0x2D to "ず",
            0x2E to "ぜ",
            0x2F to "ぞ",
            0x30 to "だ",
            0x31 to "ぢ",
            0x32 to "づ",
            0x33 to "で",
            0x34 to "ど",
            0x3A to "ば",
            0x3B to "び",
            0x3C to "ぶ",
            0x3D to "べ",
            0x3E to "ぼ",
            0x40 to "パ",
            0x41 to "ピ",
            0x42 to "プ",
            0x43 to "ポ",
            0x44 to "ぱ",
            0x45 to "ぴ",
            0x46 to "ぷ",
            0x47 to "ぺ",
            0x48 to "ぽ",
            0x54 to "ポケモン",
            0x7F to " ",
            0x80 to "ア",
            0x81 to "イ",
            0x82 to "ウ",
            0x83 to "エ",
            0x84 to "ォ",
            0x85 to "カ",
            0x86 to "キ",
            0x87 to "ク",
            0x88 to "ケ",
            0x89 to "コ",
            0x8A to "サ",
            0x8B to "シ",
            0x8C to "ス",
            0x8D to "セ",
            0x8E to "ソ",
            0x8F to "タ",
            0x90 to "チ",
            0x91 to "ツ",
            0x92 to "テ",
            0x93 to "ト",
            0x94 to "ナ",
            0x95 to "ニ",
            0x96 to "ヌ",
            0x97 to "ネ",
            0x98 to "ノ",
            0x99 to "ハ",
            0x9A to "ヒ",
            0x9B to "フ",
            0x9C to "ホ",
            0x9D to "マ",
            0x9E to "ミ",
            0x9F to "ム",
            0xA0 to "メ",
            0xA1 to "モ",
            0xA2 to "ヤ",
            0xA3 to "ユ",
            0xA4 to "ヨ",
            0xA5 to "ラ",
            0xA6 to "ル",
            0xA7 to "レ",
            0xA8 to "ロ",
            0xA9 to "ワ",
            0xAA to "ヲ",
            0xAB to "ン",
            0xAC to "ッ",
            0xAD to "ャ",
            0xAE to "ュ",
            0xAF to "ョ",
            0xB0 to "ィ",
            0xB1 to "あ",
            0xB2 to "い",
            0xB3 to "う",
            0xB4 to "え",
            0xB5 to "お",
            0xB6 to "か",
            0xB7 to "き",
            0xB8 to "く",
            0xB9 to "け",
            0xBA to "こ",
            0xBB to "さ",
            0xBC to "し",
            0xBD to "す",
            0xBE to "せ",
            0xBF to "そ",
            0xC0 to "た",
            0xC1 to "ち",
            0xC2 to "つ",
            0xC3 to "て",
            0xC4 to "と",
            0xC5 to "な",
            0xC6 to "に",
            0xC7 to "ぬ",
            0xC8 to "ね",
            0xC9 to "の",
            0xCA to "は",
            0xCB to "ひ",
            0xCC to "ふ",
            0xCD to "へ",
            0xCE to "ほ",
            0xCF to "ま",
            0xD0 to "み",
            0xD1 to "む",
            0xD2 to "め",
            0xD3 to "も",
            0xD4 to "や",
            0xD5 to "ゆ",
            0xD6 to "よ",
            0xD7 to "ら",
            0xD8 to "り",
            0xD9 to "る",
            0xDA to "れ",
            0xDB to "ろ",
            0xDC to "わ",
            0xDD to "を",
            0xDE to "ん",
            0xDF to "っ",
            0xE0 to "ゃ",
            0xE1 to "ゅ",
            0xE2 to "ょ",
            0xE3 to "ー",
            0xE4 to "。",
            0xE5 to "゚",
            0xE6 to "?",
            0xE7 to "!",
            0xE8 to "。",
            0xE9 to "ァ",
            0xEB to "ェ",
            0xEF to "♂",
            0xF0 to "円",
            0xF1 to "×",
            0xF2 to "[.]",
            0xF3 to "/",
            0xF4 to "ォ",
            0xF5 to "♀",
            0xF6 to "0",
            0xF7 to "1",
            0xF8 to "2",
            0xF9 to "3",
            0xFA to "4",
            0xFB to "5",
            0xFC to "6",
            0xFD to "7",
            0xFE to "8",
            0xFF to "9",
            0x4F to "\\n",
            0x51 to "\\p",
            0x55 to "\\l",
            0x57 to "\\e",
            0x58 to "\\r"
        ))
        val RBY_ENGLISH: TextLookup = TextLookup(mutableMapOf(
            0x4A to "[pk]",
            0x54 to "[POKé]",
            0x74 to "№",
            0x75 to "…",
            0x7F to " ",
            0x79 to "┌",
            0x7A to "─",
            0x7B to "┐",
            0x7C to "│",
            0x7D to "└",
            0x7E to "┘",
            0x80 to "A",
            0x81 to "B",
            0x82 to "C",
            0x83 to "D",
            0x84 to "E",
            0x85 to "F",
            0x86 to "G",
            0x87 to "H",
            0x88 to "I",
            0x89 to "J",
            0x8A to "K",
            0x8B to "L",
            0x8C to "M",
            0x8D to "N",
            0x8E to "O",
            0x8F to "P",
            0x90 to "Q",
            0x91 to "R",
            0x92 to "S",
            0x93 to "T",
            0x94 to "U",
            0x95 to "V",
            0x96 to "W",
            0x97 to "X",
            0x98 to "Y",
            0x99 to "Z",
            0x9A to "(",
            0x9B to ")",
            0x9C to ":",
            0x9D to ";",
            0x9E to "[",
            0x9F to "]",
            0xA0 to "a",
            0xA1 to "b",
            0xA2 to "c",
            0xA3 to "d",
            0xA4 to "e",
            0xA5 to "f",
            0xA6 to "g",
            0xA7 to "h",
            0xA8 to "i",
            0xA9 to "j",
            0xAA to "k",
            0xAB to "l",
            0xAC to "m",
            0xAD to "n",
            0xAE to "o",
            0xAF to "p",
            0xB0 to "q",
            0xB1 to "r",
            0xB2 to "s",
            0xB3 to "t",
            0xB4 to "u",
            0xB5 to "v",
            0xB6 to "w",
            0xB7 to "x",
            0xB8 to "y",
            0xB9 to "z",
            0xBA to "é",
            0xBB to "'d",
            0xBC to "'l",
            0xBD to "'s",
            0xBE to "'t",
            0xBF to "'v",
            0xE0 to "'",
            0xE1 to "[PK]",
            0xE2 to "[MN]",
            0xE3 to "-",
            0xE4 to "'r",
            0xE5 to "'m",
            0xE6 to "?",
            0xE7 to "!",
            0xE8 to ".",
            0xF0 to "$",
            0xF2 to "[.]",
            0xF4 to ","
        ))
    }

    // Accessor method to get values from the map
    fun lookup(key: UByte): String? {
        return map[key.toInt()]
    }

    fun getLongestTokenLength(): Int {
        return map.values.maxOf { it.length }
    }

    fun getByteFromString(lookup: String): Byte? {
        return map.entries.find { it.value == lookup }?.key?.toByte()
    }
}