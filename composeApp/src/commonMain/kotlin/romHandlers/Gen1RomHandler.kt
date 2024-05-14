package romHandlers

// Gen4RomHandler.kt
class Gen1RomHandler : AbstractGBCRomHandler() {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return filename == "Gen1"
        }
    }
    override fun parseRom() {
        println("Gen4 Parsing")
    }
}