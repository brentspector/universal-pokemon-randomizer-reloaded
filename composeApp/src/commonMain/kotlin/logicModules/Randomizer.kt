package logicModules

import models.Rom
import romHandlers.abstractRomHandlers.AbstractRomHandler
import viewModels.StarterPokemonMod
import viewModels.StarterPokemonViewModel


class Randomizer(private val romHandler: AbstractRomHandler) {
    fun randomize(filename: String): Int {
        val seed: Long = RandomSource.pickSeed()
        return randomize(filename, seed)
    }

    fun randomize(filename: String, seed: Long): Int {
        RandomSource.seed(seed)
        when (StarterPokemonViewModel.getState()) {
            StarterPokemonMod.UNCHANGED -> {}
            StarterPokemonMod.RANDOM -> { romHandler.randomizeStarters() }
            StarterPokemonMod.CUSTOM -> { romHandler.setStarters(intArrayOf(0, 0, 0)) }
        }
        return 0
    }

    fun parseRom() {
        romHandler.parseRom()
    }
    fun saveROM(): Rom {
        return romHandler.saveROM()
    }

    override fun toString(): String {
        return romHandler::class.simpleName ?: "Basic Randomizer"
    }
}