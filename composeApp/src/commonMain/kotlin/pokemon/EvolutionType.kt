package pokemon

import logicModules.RandomSource

const val UNUSED_METHOD = -1
enum class EvolutionMethod(private val types: List<EvolutionType>) {
    HAPPINESS(listOf(EvolutionType.HAPPINESS, EvolutionType.HAPPINESS_DAY, EvolutionType.HAPPINESS_NIGHT)),
    UNCONTROLLED(listOf(EvolutionType.LEVEL, EvolutionType.LEVEL_FEMALE_ONLY, EvolutionType.LEVEL_MALE_ONLY, EvolutionType.LEVEL_HIGH_PV, EvolutionType.LEVEL_LOW_PV)),
    BRANCHLEVEL(listOf(EvolutionType.LEVEL_FEMALE_ONLY, EvolutionType.LEVEL_MALE_ONLY, EvolutionType.LEVEL_HIGH_PV, EvolutionType.LEVEL_LOW_PV)),
    TRADE(listOf(EvolutionType.TRADE, EvolutionType.TRADE_ITEM)),
    STONE(listOf(EvolutionType.STONE, EvolutionType.STONE_FEMALE_ONLY, EvolutionType.STONE_MALE_ONLY)),
    ITEM(listOf(EvolutionType.LEVEL_ITEM_DAY, EvolutionType.LEVEL_ITEM_NIGHT, EvolutionType.TRADE_ITEM)),
    PARTY(listOf(EvolutionType.LEVEL_WITH_OTHER)),
    // BANNED: Doesn't work for anything but Nincada -> Shedninja
    // And Karrablast -> Shelmet
    // And MEGA_EVOLVE is too complicated
    BANNED(listOf(EvolutionType.LEVEL_CREATE_EXTRA, EvolutionType.LEVEL_IS_EXTRA, EvolutionType.TRADE_SPECIAL, EvolutionType.MEGA_EVOLVE)),
    // DIFFERENT: Methods do not follow standard data structure - skip requirements and
    // just copy the data
    DIFFERENT(listOf(EvolutionType.MEGA_EVOLVE));
    fun containsType(type: EvolutionType): Boolean {
        return type in types
    }

    fun containsAnyType(typeList: List<EvolutionType>): Boolean {
        return types.intersect(typeList.toSet()).isNotEmpty()
    }
}

enum class EvolutionType(vararg val generations: Int) {

    LEVEL(1, 1, 4, 4, 4),
    STONE(2, 2, 7, 7, 8),
    TRADE(3, 3, 5, 5, 5),
    TRADE_ITEM(UNUSED_METHOD, 3, 6, 6, 6),
    HAPPINESS(UNUSED_METHOD, 4, 1, 1, 1),
    HAPPINESS_DAY(UNUSED_METHOD, 4, 2, 2, 2),
    HAPPINESS_NIGHT(UNUSED_METHOD, 4, 3, 3, 3),
    LEVEL_ATTACK_HIGHER(UNUSED_METHOD, 5, 8, 8, 9),
    LEVEL_DEFENSE_HIGHER(UNUSED_METHOD, 5, 10, 10, 11),
    LEVEL_ATK_DEF_SAME(UNUSED_METHOD, 5, 9, 9, 10),
    LEVEL_LOW_PV(UNUSED_METHOD, UNUSED_METHOD, 11, 11, 12),
    LEVEL_HIGH_PV(UNUSED_METHOD, UNUSED_METHOD, 12, 12, 13),
    LEVEL_CREATE_EXTRA(UNUSED_METHOD, UNUSED_METHOD, 13, 13, 14),
    LEVEL_IS_EXTRA(UNUSED_METHOD, UNUSED_METHOD, 14, 14, 15),
    LEVEL_HIGH_BEAUTY(UNUSED_METHOD, UNUSED_METHOD, 15, 15, 16),
    STONE_MALE_ONLY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 16, 17),
    STONE_FEMALE_ONLY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 17, 18),
    LEVEL_ITEM_DAY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 18, 19),
    LEVEL_ITEM_NIGHT(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 19, 20),
    LEVEL_WITH_MOVE(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 20, 21),
    LEVEL_WITH_OTHER(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 21, 22),
    LEVEL_MALE_ONLY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 22, 23),
    LEVEL_FEMALE_ONLY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 23, 24),
    LEVEL_ELECTRIFIED_AREA(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 24, 25),
    LEVEL_MOSS_ROCK(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 25, 26),
    LEVEL_ICY_ROCK(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 26, 27),
    TRADE_SPECIAL(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, 7),
    FAIRY_AFFECTION(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_WITH_DARK(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_UPSIDE_DOWN(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_RAIN(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_DAY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_NIGHT(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    MEGA_EVOLVE(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_FEMALE_ESPURR(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_GAME(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_DAY_GAME(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_NIGHT_GAME(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_SNOWY(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_DUSK(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    LEVEL_NIGHT_ULTRA(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    STONE_ULTRA(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD),
    NONE(UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD, UNUSED_METHOD);

    companion object {
        private val methodsAvailable: MutableList<Int> = mutableListOf()
        private val reverseIndexes: MutableMap<Int, MutableMap<Int, EvolutionType>> = entries.flatMap {
            // A lot of boilerplate. reverseIndexes is just a map of generation to (value, EvolutionType)
            it.generations.mapIndexed{ generation, value -> generation to mutableMapOf(value to it) }
        }.toMap().toMutableMap()

        fun modifyEvolutionTypes(
            generation: Int,
            evolutionMethods: MutableMap<Int, EvolutionType>
        ) {
            evolutionMethods.forEach { (key, method) ->
                if (method != NONE) {
                    method.generations[generation - 1] = key
                    reverseIndexes[generation-1]?.set(key, method)
                    methodsAvailable.add(key)
                } else {
                    reverseIndexes[generation-1]?.get(key)?.let {
                        it.generations[generation-1] = UNUSED_METHOD
                        reverseIndexes[generation-1]?.set(key, NONE)
                    }
                }
            }
        }

        fun fromIndex(generation: Int, index: Int): EvolutionType {
            return reverseIndexes[generation - 1]?.get(index) ?: NONE
        }

        fun randomFromGeneration(random: RandomSource, generation: Int): EvolutionType {
            val available = reverseIndexes[generation-1]
            var et = available!!.values.let { random.randomOfList(it.toMutableList()) }
            if (generation == 2) {
                // Since Gen 2 has a special pointer value for the version of happiness,
                // all indices are set at 4. We select a specific version by getting a
                // number from 0-2 and adding 4 to match the index of that type
                if (et === HAPPINESS) {
                    et = entries[random.nextInt(3) + 4]
                } else if (et === LEVEL_ATTACK_HIGHER) {
                    et = entries[random.nextInt(3) + 7]
                } else if (et === TRADE) {
                    et = entries[random.nextInt(2) + 2]
                }
            }
            return et
        }

        fun generationCount(generation: Int): MutableList<Int> {
            return if (methodsAvailable.size > 0) {
                methodsAvailable
            } else {
                (1..(reverseIndexes[generation-1]?.size ?: 0)).toList().toMutableList()
            }
        }
    }

    fun toIndex(generation: Int): Int {
        return generations[generation - 1]
    }

    fun usesLevel(): Boolean {
        return this === LEVEL || this === LEVEL_ATTACK_HIGHER || this === LEVEL_DEFENSE_HIGHER || this === LEVEL_ATK_DEF_SAME || this === LEVEL_LOW_PV || this === LEVEL_HIGH_PV || this === LEVEL_CREATE_EXTRA || this === LEVEL_IS_EXTRA || this === LEVEL_MALE_ONLY || this === LEVEL_FEMALE_ONLY
    }

    fun isInGeneration(generation: Int): Boolean {
        return generations[generation - 1] > UNUSED_METHOD
    }
}