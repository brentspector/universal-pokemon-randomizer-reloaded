package romHandlers

// Gen4RomHandler.kt
class Gen2RomHandler : AbstractGBCRomHandler() {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return filename == "Gen2"
        }
    }

    override fun parseRom() {
        println("Gen4 Parsing")
    }
}