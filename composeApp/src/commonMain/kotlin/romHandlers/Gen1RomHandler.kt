package romHandlers

import configurations.Gen1RomConfiguration
import configurations.YellowVersionUSA
import models.GBRom
import pokemon.ExpCurve
import pokemon.Pokemon
import romHandlers.abstractRomHandlers.AbstractGBRomHandler

class Gen1RomHandler(romConfiguration: Gen1RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {
    private val pokes: MutableList<Pokemon> = mutableListOf(Pokemon(0))
    private val pokeNames: MutableList<String> = mutableListOf()
    // TODO: Get the actual pokedexCount
    private val pokedexCount: Int = 151
    override fun parseRom() {
        readPokemonNames()
        loadPokemonStats()
    }
    private fun loadPokemonStats() {
        // Get base stats
        for (i in 1..pokedexCount) {
            pokes.add(Pokemon(i))
            // Change the offset to mewStatsOffset if this is not Yellow version
            val offset = when {
                romConfiguration !is YellowVersionUSA && i == Gen1RomConfiguration.MEW_INDEX -> romConfiguration.mewStatsOffset
                else -> romConfiguration.pokemonStatsOffset + (i - 1) * Gen1RomConfiguration.BASE_STATS_ENTRY_SIZE
            }
            // TODO: Verify YellowVersionUSA covers all children classes, otherwise make isYellow function
            loadBasicPokeStats(pokes[i], offset)
        }

        // Evolutions
        populateEvolutions()
    }

    private fun readPokemonNames() {
        (1..romConfiguration.internalPokemonCount).mapTo(pokeNames) {i ->
            readFixedLengthString(romConfiguration.pokemonNamesOffset + (i - 1) * romConfiguration.pokemonNamesLength,
                romConfiguration.pokemonNamesLength)
        }
    }

    private fun loadBasicPokeStats(pk: Pokemon, offset: Int) {
        pk.name = pokeNames[getPokeNumToRBYTable()[pk.number]]
        pk.hp = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_HP_OFFSET])
        pk.attack = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_ATTACK_OFFSET])
        pk.defense = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_DEFENSE_OFFSET])
        pk.speed = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_SPEED_OFFSET])
        pk.special = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_SPECIAL_OFFSET])
        pk.spatk = pk.special
        pk.spdef = pk.special
        pk.primaryType = romConfiguration.typeTable[readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_PRIMARY_TYPE_OFFSET])]!!
        pk.secondaryType = romConfiguration.typeTable[readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_SECONDARY_TYPE_OFFSET])]
        // Only one type?
        if (pk.secondaryType === pk.primaryType) {
            pk.secondaryType = null
        }

        pk.catchRate = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_CATCH_RATE_OFFSET])
        pk.expYield = readUnsignedByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_EXP_YIELD_OFFSET])
        pk.growthCurve = ExpCurve.fromByte(rom.value[offset + Gen1RomConfiguration.BASE_STATS_GROWTH_CURVE_OFFSET])

        pk.guaranteedHeldItem = -1
        pk.commonHeldItem = -1
        pk.rareHeldItem = -1
        pk.darkGrassHeldItem = -1
    }

    private fun getPokeNumToRBYTable(): List<Int> {
        return (1..256).toList()
    }

    private fun populateEvolutions() {
        // TODO: Define
        pokes.forEach { println(it) }
    }

    private fun readFixedLengthString(offset: Int, length: Int): String {
        return readString(offset, length, false)
    }

    private fun readString(offset: Int, maxLength: Int, textEngineMode: Boolean): String {
        return buildString {
            for (c in 0 until maxLength) {
                val currChar: UByte = rom.value[offset + c].toUByte()
                val textChar = romConfiguration.textLookup.lookup(currChar)
                if (textChar != null) {
                    append(textChar)
                    if (textEngineMode && (textChar == "\\r" || textChar == "\\e")) {
                        break
                    }
                } else {
                    if (currChar == Gen1RomConfiguration.STRING_TERMINATOR.toUByte()) {
                        break
                    } else {
                        append("\\x${currChar.toString(16)}")
                    }
                }
            }
        }
    }

    private fun readUnsignedByte(byte: Byte): Int {
        return byte.toInt() and 0xFF
    }
}