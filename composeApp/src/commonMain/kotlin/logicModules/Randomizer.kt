package logicModules

import romHandlers.RomHandler


class Randomizer(private val romHandler: RomHandler) {
    fun randomize(filename: String): Int {
        val seed: Long = RandomSource.pickSeed()
        return randomize(filename, seed)
    }

    fun randomize(filename: String, seed: Long): Int {
        return 0
    }

    override fun toString(): String {
        return romHandler::class.simpleName ?: "Basic Randomizer"
    }
}