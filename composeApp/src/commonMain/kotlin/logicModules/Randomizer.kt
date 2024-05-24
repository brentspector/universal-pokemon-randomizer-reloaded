package logicModules

import romHandlers.RomHandler
import viewModels.StarterPokemonMod
import viewModels.StarterPokemonViewModel


class Randomizer(private val romHandler: RomHandler) {
    fun randomize(filename: String): Int {
        val seed: Long = RandomSource.pickSeed()
        return randomize(filename, seed)
    }

    fun randomize(filename: String, seed: Long): Int {
        RandomSource.seed(seed)
        when (StarterPokemonViewModel.getState()) {
            StarterPokemonMod.UNCHANGED -> {}
            StarterPokemonMod.RANDOM -> { romHandler.randomizeStarters() }
            StarterPokemonMod.CUSTOM -> { println("CUSTOM IS TODO") }
        }
        return 0
    }

    override fun toString(): String {
        return romHandler::class.simpleName ?: "Basic Randomizer"
    }
}