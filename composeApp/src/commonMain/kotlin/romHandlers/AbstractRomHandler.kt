package romHandlers

import configurations.RomConfiguration
import pokemon.Pokemon
import pokemon.Type

abstract class AbstractRomHandler(private val romConfiguration: RomConfiguration) : RomHandler {
    private val allPokes: MutableList<Pokemon> = mutableListOf(Pokemon("Default", Type.NORMAL))
    private val starterPokes: MutableList<Pokemon> = mutableListOf()
    override fun getPokemon(): MutableList<Pokemon> {
        return allPokes
    }

    override fun getPokemonByNumber(number: Int): Pokemon {
        return allPokes[number]
    }
    override fun randomPokemon(): Pokemon {
        return Pokemon("", Type.NORMAL)
    }

    override fun randomizeStarters() {
        repeat(romConfiguration.numStarters) {
            starterPokes.add(randomPokemon())
        }
    }

    override fun setStarters(starters: IntArray) {
        starterPokes.addAll(starters.take(romConfiguration.numStarters).map { getPokemonByNumber(it) })
    }
}
