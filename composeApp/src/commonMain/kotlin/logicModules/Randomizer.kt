package logicModules

import romHandlers.RomHandler


class Randomizer(romHandler: RomHandler) {

    fun randomize(filename: String): Int {
        val seed: Long = RandomSource.pickSeed()
        return randomize(filename, seed)
    }

    fun randomize(filename: String, seed: Long): Int {
        return 0
    }
}