package romHandlers

class Gen5RomHandler : AbstractDSRomHandler() {
    companion object {
        fun isLoadable(filename: String): Boolean {
            return filename == "Gen5"
        }
    }
}