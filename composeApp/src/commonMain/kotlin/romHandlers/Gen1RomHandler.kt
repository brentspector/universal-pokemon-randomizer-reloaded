package romHandlers

import configurations.Gen1RomConfiguration
import configurations.YellowVersionUSA
import models.GBRom
import pokemon.Evolution
import pokemon.EvolutionType
import pokemon.ExpCurve
import pokemon.Pokemon
import romHandlers.abstractRomHandlers.AbstractGBRomHandler
import kotlin.math.max

class Gen1RomHandler(romConfiguration: Gen1RomConfiguration, rom: GBRom)
    : AbstractGBRomHandler(romConfiguration, rom) {
    private val pokeNames: MutableList<String> = mutableListOf("")
    private val pokeNumToRBYTable: MutableMap<Int, Int> = mutableMapOf(0 to 0)
    private val pokeRBYToNumTable: MutableMap<Int, Int> = mutableMapOf(0 to 0)
    private var pokedexCount: Int = 0
    override fun parseRom() {
        loadPokedexOrder()
        readPokemonNames()
        loadPokemonStats()
    }

    private fun loadPokedexOrder() {
        for(i in 1..romConfiguration.internalPokemonCount) {
            val pokedexNum: Int = readUnsignedByte(rom.value[romConfiguration.pokedexOrderOffset + i - 1])
            getPokeRBYToNumTable()[i] = pokedexNum
            if (pokedexNum != 0 && !getPokeNumToRBYTable().containsKey(pokedexNum)) {
                getPokeNumToRBYTable()[pokedexNum] = i
            }
            pokedexCount = max(pokedexCount, pokedexNum)
        }
    }
    private fun loadPokemonStats() {
        // Get base stats
        for (i in 1..pokedexCount) {
            getPokemon().add(Pokemon(i))
            // Change the offset to mewStatsOffset if this is not Yellow version
            val offset = when {
                romConfiguration !is YellowVersionUSA && i == Gen1RomConfiguration.MEW_INDEX -> romConfiguration.mewStatsOffset
                else -> romConfiguration.pokemonStatsOffset + (i - 1) * Gen1RomConfiguration.BASE_STATS_ENTRY_SIZE
            }
            // TODO: Verify YellowVersionUSA covers all children classes, otherwise make isYellow function
            loadBasicPokeStats(getPokemon()[i], offset)
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
        pk.name = pokeNames[getPokeNumToRBYTable()[pk.number]!!]
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

    private fun getPokeNumToRBYTable(): MutableMap<Int, Int> {
        return pokeNumToRBYTable
    }

    private fun getPokeRBYToNumTable(): MutableMap<Int, Int> {
        return pokeRBYToNumTable
    }

    private fun populateEvolutions() {
        getPokemon().forEach { pk ->
            pk.apply {
                evolutionsFrom.clear()
                evolutionsTo.clear()
            }
        }

        for (i in 1..romConfiguration.internalPokemonCount) {
            val pointer = readWord(romConfiguration.pokemonMovesetsTableOffset + (i - 1) * 2)
            var realPointer = calculateOffset(romConfiguration.pokemonMovesetsTableOffset, pointer, Gen1RomConfiguration.BANK_SIZE)

            if (getPokeRBYToNumTable()[i] != 0) {
                val thisPoke = getPokeRBYToNumTable()[i]
                val pk = getPokemon()[thisPoke!!]

                while (rom.value[realPointer].toInt() != 0) {
                    val method = rom.value[realPointer].toInt()
                    val type = EvolutionType.fromIndex(1, method)
                    val otherPoke = getPokeRBYToNumTable()[readUnsignedByte(rom.value[realPointer + 2 + if (type == EvolutionType.STONE) 1 else 0])]
                    val extraInfo = readUnsignedByte(rom.value[realPointer + 1])
                    val evo = Evolution(pk, getPokemon()[otherPoke!!], true, type, extraInfo)

                    if (!pk.evolutionsFrom.contains(evo)) {
                        pk.evolutionsFrom.add(evo)
                        getPokemon()[otherPoke].evolutionsTo.add(evo)
                    }

                    realPointer += if (type == EvolutionType.STONE) 4 else 3
                }

                if (pk.evolutionsFrom.size > 1) {
                    pk.evolutionsFrom.forEach { it.carryStats = false }
                }
            }
        }

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

    private fun readWord(offset: Int): Int {
        return readWord(rom.value, offset)
    }

    private fun readWord(data: ByteArray, offset: Int): Int {
        return readUnsignedByte(data[offset]) + (readUnsignedByte(data[offset+1]) shl 8)
    }
}