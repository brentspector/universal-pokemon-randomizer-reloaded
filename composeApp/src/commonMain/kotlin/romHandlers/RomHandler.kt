package romHandlers

import pokemon.Pokemon

interface RomHandler {
    fun getPokemon(): MutableList<Pokemon>
    fun getPokemonByNumber(number: Int): Pokemon
    fun randomPokemon(): Pokemon

    fun randomizeStarters()

    fun setStarters(starters: IntArray)
    fun loadRom()
}
