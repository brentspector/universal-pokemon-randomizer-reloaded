package romHandlers

// Gen4RomHandler.kt
class Gen3RomHandler : AbstractGBRomHandler() {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return filename == "Gen3"
        }
    }

    override fun parseRom() {
        println("Gen4 Parsing")
    }
}