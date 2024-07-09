package configurations

import models.GBRom
import models.Rom
import pokemon.Type
import romHandlers.Gen2RomHandler
import romHandlers.abstractRomHandlers.AbstractRomHandler

open class SilverVersionEnglish : RomConfiguration {
    override val crcInHeader: Int
        get() = TODO("Not yet implemented")
    override val nonJapanese: Int
        get() = TODO("Not yet implemented")
    override val version: Int
        get() = TODO("Not yet implemented")
    override val romName: String
        get() = TODO("Not yet implemented")
    override val numStarters: Int = 3
    override val pokemonNamesOffset: Int
        get() = TODO("Not yet implemented")
    override val pokemonNamesLength: Int
        get() = TODO("Not yet implemented")
    override val pokedexOrderOffset: Int
        get() = TODO("Not yet implemented")
    override val internalPokemonCount: Int
        get() = TODO("Not yet implemented")
    override val pokemonStatsOffset: Int
        get() = TODO("Not yet implemented")
    override val mewStatsOffset: Int
        get() = TODO("Not yet implemented")

    override val textLookup: TextLookup
        get() = TODO("Not yet implemented")

    override val typeTable: MutableMap<Int, Type>
        get() = TODO("Not yet implemented")


    override fun isLoadable(rom: Rom): Boolean {
        return true
    }
    override fun create(rom: Rom): AbstractRomHandler {
        return Gen2RomHandler(this, rom as GBRom)
    }
}

class GoldVersionEnglish : SilverVersionEnglish()

class CrystalVersionEnglish : SilverVersionEnglish()