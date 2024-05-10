package logicModules

import romHandlers.RomHandler


class Randomizer {
    private lateinit var romHandler: RomHandler

    fun Randomizer(romHandler: RomHandler) {
        this.romHandler = romHandler
    }

    fun randomize(filename: String): Int {
        val seed: Long = RandomSource.pickSeed()
        return randomize(filename, seed)
    }

    fun randomize(filename: String, seed: Long): Int {
        return 0
    }
}