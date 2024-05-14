package romHandlers

interface RomHandler {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return false
        }
    }
    fun parseRom()
    fun manipulateRom()
}