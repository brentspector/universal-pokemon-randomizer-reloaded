package romHandlers

// Gen4RomHandler.kt
class Gen4RomHandler : AbstractDSRomHandler() {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return filename == "Gen4"
        }
    }

    override fun parseRom() {
        println("Gen4 Parsing")
    }
}