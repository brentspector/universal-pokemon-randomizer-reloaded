package romHandlers

import pokemon.Pokemon

abstract class AbstractRomHandler : RomHandler {
    private val allPokes: List<Pokemon> = ArrayList()
    private lateinit var starterPokes: List<Pokemon>
    override fun randomPokemon(): Pokemon {
        return Pokemon("")
    }

    override fun randomizeStarters(numStarters: Int) {
        starterPokes = mutableListOf(randomPokemon(), randomPokemon())
        println(starterPokes)
    }
}