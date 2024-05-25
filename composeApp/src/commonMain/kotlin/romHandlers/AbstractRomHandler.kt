package romHandlers

import configurations.RomConfiguration
import pokemon.Pokemon

abstract class AbstractRomHandler(private val romConfiguration: RomConfiguration) : RomHandler {
    private val allPokes: MutableList<Pokemon> = mutableListOf()
    private val starterPokes: MutableList<Pokemon> = mutableListOf()
    override fun randomPokemon(): Pokemon {
        return Pokemon("")
    }

    override fun randomizeStarters() {
        repeat(romConfiguration.numStarters) {
            starterPokes.add(randomPokemon())
        }
    }
}