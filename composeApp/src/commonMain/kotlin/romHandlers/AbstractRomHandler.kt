package romHandlers

// AbstractRomHandler.kt
abstract class AbstractRomHandler : RomHandler {
    override fun parseRom() {
        println("Parsing ROM")
    }

    override fun manipulateRom() {
        println("Manipulating ROM")
    }
}