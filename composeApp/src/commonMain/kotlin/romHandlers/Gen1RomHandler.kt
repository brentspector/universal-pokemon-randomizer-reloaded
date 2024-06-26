package romHandlers

import configurations.Gen1RomConfiguration
import configurations.YellowVersionUSA
import models.GBRom
import pokemon.Pokemon
import pokemon.Type
import romHandlers.abstractRomHandlers.AbstractGBRomHandler
import kotlin.experimental.and

class Gen1RomHandler(romConfiguration: Gen1RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {
    private val pokes: MutableList<Pokemon> = mutableListOf()
    // TODO: Get the actual pokedexCount
    private val pokedexCount: Int = 151
    override fun parseRom() {
        loadPokemonStats()
    }
    private fun loadPokemonStats() {
        // Fetch our names
        val pokeNames: MutableList<String> = readPokemonNames()
        // Get base stats
        for (i in 1..pokedexCount) {
            // TODO: Get name and type
            val pk = Pokemon("", Type.NORMAL)
            pk.number = i
            // TODO: Verify YellowVersionUSA covers all children classes, otherwise make isYellow function
            if (i != Gen1RomConfiguration.MEW_INDEX || romConfiguration is YellowVersionUSA) {
                loadBasicPokeStats(
                    pk,romConfiguration.pokemonStatsOffset + (i - 1) * Gen1RomConfiguration.BASE_STATS_ENTRY_SIZE
                )
            }
            // Name?
            pk.name = pokeNames[getPokeNumToRBYTable()[i]]
            pokes.add(pk)
        }

        // Mew override for R/B
        if (romConfiguration is YellowVersionUSA) {
            loadBasicPokeStats(
                pokes[Gen1RomConfiguration.MEW_INDEX],
                romConfiguration.mewStatsOffset
            )
        }

        // Evolutions
        populateEvolutions()
    }

    private fun readPokemonNames(): MutableList<String> {
        println("Reading names")
        return (1..romConfiguration.internalPokemonCount).map {i ->
            readFixedLengthString(romConfiguration.pokemonNamesOffset + (i - 1) * romConfiguration.pokemonNamesLength,
                romConfiguration.pokemonNamesLength)
        }.toMutableList()
    }

    private fun loadBasicPokeStats(pk: Pokemon, i: Int) {
        // TODO: Define
    }

    private fun getPokeNumToRBYTable(): ArrayList<Int> {
        // TODO: Define
        return arrayListOf()
    }

    private fun populateEvolutions() {
        // TODO: Define
    }

    private fun readFixedLengthString(offset: Int, length: Int): String {
        return readString(offset, length, false)
    }

    private fun readString(offset: Int, maxLength: Int, textEngineMode: Boolean): String {
        println("Reading $offset $maxLength")
        return buildString {
            for (c in 0 until maxLength) {
                val currChar: Int = rom.value[offset + c].toUByte().toInt()
                val textChar = romConfiguration.textLookup.lookup(currChar)
                if (textChar != null) {
                    append(textChar)
                    if (textEngineMode && (textChar == "\\r" || textChar == "\\e")) {
                        break
                    }
                } else {
                    if (currChar == Gen1RomConfiguration.STRING_TERMINATOR) {
                        break
                    } else {
                        append("\\x${currChar.toString(16)}")
                    }
                }
            }
        }
    }
}