package romHandlers.abstractRomHandlers

import configurations.RomConfiguration
import pokemon.Pokemon

abstract class AbstractRomHandler(private val romConfiguration: RomConfiguration) {
    private val allPokes: MutableList<Pokemon> = mutableListOf(Pokemon("Default"))
    private val starterPokes: MutableList<Pokemon> = mutableListOf()
    fun getPokemon(): MutableList<Pokemon> {
        return allPokes
    }

    fun getPokemonByNumber(number: Int): Pokemon {
        return allPokes[number]
    }
    fun randomPokemon(): Pokemon {
        return Pokemon("")
    }

    fun randomizeStarters() {
        repeat(romConfiguration.numStarters) {
            starterPokes.add(randomPokemon())
        }
    }

    fun setStarters(starters: IntArray) {
        starterPokes.addAll(starters.take(romConfiguration.numStarters).map { getPokemonByNumber(it) })
    }

    abstract fun saveROM(): Any
}
