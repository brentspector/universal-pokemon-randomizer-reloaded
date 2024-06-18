package romHandlers

import configurations.Gen1RomConfiguration
import configurations.YellowVersionUSA
import pokemon.Pokemon
import pokemon.Type
import kotlin.experimental.and


class Gen1RomHandler(private val romConfiguration: Gen1RomConfiguration) : AbstractGBCRomHandler(romConfiguration) {
    private val pokes: MutableList<Pokemon> = mutableListOf()
    // TODO: Get the actual pokedexCount
    private val pokedexCount: Int = 151
    override fun loadRom() {
        loadPokemonStats()
    }
    private fun loadPokemonStats() {
        // Fetch our names
        val pokeNames: MutableList<String> = readPokemonNames()
        //TODO fix pokeStatsOffset
        val pokeStatsOffset = 0
        // Get base stats
        for (i in 1..pokedexCount) {
            val pk = Pokemon("", Type.NORMAL)
            pk.number = i
            // TODO: Verify YellowVersionUSA covers all children classes, otherwise make isYellow function
            if (i != Gen1RomConfiguration.MEW_INDEX || romConfiguration !is YellowVersionUSA) {
                loadBasicPokeStats(
                    pk,pokeStatsOffset + (i - 1) * Gen1RomConfiguration.BASE_STATS_ENTRY_SIZE
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
        val rom = ByteArray(0)
        val tb = mutableListOf("", null)
        return buildString {
            for (c in 0 until maxLength) {
                val currChar: Int = (rom[offset + c] and 0xFF.toByte()).toInt()
                if (tb[currChar] != null) {
                    append(tb[currChar])
                    if (textEngineMode && (tb[currChar] == "\\r" || tb[currChar] == "\\e")) {
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