package romHandlers

import pokemon.Pokemon

interface RomHandler {
    fun randomPokemon(): Pokemon

    fun randomizeStarters()
}