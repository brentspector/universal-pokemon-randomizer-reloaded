package romHandlers

import pokemon.Pokemon

// AbstractRomHandler.kt
abstract class AbstractRomHandler : RomHandler {
    override fun randomPokemon(): Pokemon {
        return Pokemon("")
    }
}