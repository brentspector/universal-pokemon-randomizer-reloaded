package pokemon

import exceptions.RandomizationException
import logicModules.RandomSource

enum class Type(isHackOnly: Boolean = false) {
    NORMAL, FIGHTING, FLYING, GRASS, WATER, FIRE, ROCK, GROUND, PSYCHIC, BUG, DRAGON, ELECTRIC, GHOST, POISON, ICE, STEEL, DARK, GAS(
        true), FAIRY(true), WOOD(true), ABNORMAL(
        true), WIND(true), SOUND(true), LIGHT(true), TRI(true), HACK(true);

    companion object {
        private var shuffledList: MutableList<Type> = mutableListOf()

        val STRONG_AGAINST: MutableMap<Type, MutableList<Type>> = mutableMapOf(
            BUG to TypeRelationship.STRONG_AGAINST_BUG,
            DARK to TypeRelationship.STRONG_AGAINST_DARK,
            DRAGON to TypeRelationship.STRONG_AGAINST_DRAGON,
            ELECTRIC to TypeRelationship.STRONG_AGAINST_ELECTRIC,
            FIGHTING to TypeRelationship.STRONG_AGAINST_FIGHTING,
            FIRE to TypeRelationship.STRONG_AGAINST_FIRE,
            FLYING to TypeRelationship.STRONG_AGAINST_FLYING,
            GHOST to TypeRelationship.STRONG_AGAINST_GHOST,
            GRASS to TypeRelationship.STRONG_AGAINST_GRASS,
            GROUND to TypeRelationship.STRONG_AGAINST_GROUND,
            ICE to TypeRelationship.STRONG_AGAINST_ICE,
            NORMAL to TypeRelationship.STRONG_AGAINST_NORMAL,
            POISON to TypeRelationship.STRONG_AGAINST_POISON,
            PSYCHIC to TypeRelationship.STRONG_AGAINST_PSYCHIC,
            ROCK to TypeRelationship.STRONG_AGAINST_ROCK,
            STEEL to TypeRelationship.STRONG_AGAINST_STEEL,
            WATER to TypeRelationship.STRONG_AGAINST_WATER
        )

        val RESISTANT_TO: MutableMap<Type, MutableList<Type>> = mutableMapOf(
            BUG to TypeRelationship.RESISTANT_TO_BUG,
            DARK to TypeRelationship.RESISTANT_TO_DARK,
            DRAGON to TypeRelationship.RESISTANT_TO_DRAGON,
            ELECTRIC to TypeRelationship.RESISTANT_TO_ELECTRIC,
            FIGHTING to TypeRelationship.RESISTANT_TO_FIGHTING,
            FIRE to TypeRelationship.RESISTANT_TO_FIRE,
            FLYING to TypeRelationship.RESISTANT_TO_FLYING,
            GHOST to TypeRelationship.RESISTANT_TO_GHOST,
            GRASS to TypeRelationship.RESISTANT_TO_GRASS,
            GROUND to TypeRelationship.RESISTANT_TO_GROUND,
            ICE to TypeRelationship.RESISTANT_TO_ICE,
            NORMAL to TypeRelationship.RESISTANT_TO_NORMAL,
            POISON to TypeRelationship.RESISTANT_TO_POISON,
            PSYCHIC to TypeRelationship.RESISTANT_TO_PSYCHIC,
            ROCK to TypeRelationship.RESISTANT_TO_ROCK,
            STEEL to TypeRelationship.RESISTANT_TO_STEEL,
            WATER to TypeRelationship.RESISTANT_TO_WATER
        )

        val IMMUNE_TO: MutableMap<Type, MutableList<Type>> = mutableMapOf(
            ELECTRIC to TypeRelationship.IMMUNE_TO_ELECTRIC,
            FIGHTING to TypeRelationship.IMMUNE_TO_FIGHTING,
            GHOST to TypeRelationship.IMMUNE_TO_GHOST,
            GROUND to TypeRelationship.IMMUNE_TO_GROUND,
            NORMAL to TypeRelationship.IMMUNE_TO_NORMAL,
            POISON to TypeRelationship.IMMUNE_TO_POISON,
            PSYCHIC to TypeRelationship.IMMUNE_TO_PSYCHIC
        )

        fun randomType(random: RandomSource): Type {
            return random.randomOfList(entries.toMutableList())
        }

        fun getTypes(size: Int): List<Type> {
            return entries.take(size)
        }

        fun setShuffledList(list: List<Type>) {
            shuffledList = list.toMutableList()
        }

        fun getShuffledList(): List<Type> {
            return shuffledList
        }

        fun randomStrength(
            random: RandomSource,
            useResistantType: Boolean,
            vararg checkTypes: Type?
        ): Type? {
            // Safety check since varargs allow zero arguments
            if (checkTypes.isEmpty()) {
                throw RandomizationException("Must provide at least 1 type to obtain a strength")
            }

            return if (useResistantType) {
                getStrengthFromList(random, getCombinedResistanceMap(), *checkTypes)
            } else {
                getStrengthFromList(random, STRONG_AGAINST, *checkTypes)
            }
        }

        fun getStrengthFromList(
            random: RandomSource,
            checkMap: Map<Type, List<Type>>,
            vararg checkTypes: Type?
        ): Type? {
            val randomTypes = entries.toMutableList()
            random.shuffleList(randomTypes)
            var backupType: Type? = null

            for (checkType in randomTypes) {
                // If the type doesn't have a value in the checkMap, go to next iteration
                if (checkType !in checkMap) continue

                // If the checkMap list contains all of the types provided in the checkTypes, return the type
                if (checkMap[checkType]!!.containsAll(checkTypes.toSet())) {
                    return checkType
                }
                // If there is at least 1 matching type in the check types, set the backup to it
                else if (checkMap[checkType]!!.intersect(checkTypes.toSet()).isNotEmpty()) {
                    backupType = checkType
                }
            }

            // Returns the backup type, or null if no match was found (for example, Normal-type)
            return backupType
        }

        fun randomWeakness(
            random: RandomSource,
            useResistantType: Boolean,
            vararg checkTypes: Type?
        ): Type? {
            // Safety check since varargs allow zero arguments
            if (checkTypes.isEmpty()) {
                throw RandomizationException("Must provide at least 1 type to obtain a weakness")
            }

            return if (useResistantType) {
                getWeaknessFromList(random, getCombinedResistanceMap(), *checkTypes)
            } else {
                getWeaknessFromList(random, STRONG_AGAINST, *checkTypes)
            }
        }

        private fun getWeaknessFromList(
            random: RandomSource,
            checkMap: Map<Type, List<Type>>,
            vararg checkTypes: Type?
        ): Type? {
            val pickList = checkTypes
                // Filter out null types from checkTypes
                .filterNotNull()
                // Filter out null lists from checkMap
                .mapNotNull { checkMap[it] }
                // Starting with a null Set that is replaced with the first Set from the map (list.toSet),
                // accumulate a Set of all types from checkMap that are shared (acc.intersect)
                .fold(null as Set<Type>?) { acc, list ->
                    acc?.intersect(list.toSet()) ?: list.toSet()
                }

            return when {
                // No weaknesses at all in checkMap
                pickList == null -> null
                // Random weakness for any of the types given
                // Can occur when no weaknesses are shared, such as Ghost/Dark
                pickList.isEmpty() -> {
                    var randomType: Type?
                    var resistantList: List<Type>?
                    do {
                        randomType = checkTypes[random.nextInt(checkTypes.size)]
                        resistantList = checkMap[randomType]
                    } while (resistantList.isNullOrEmpty()) // Keep reselecting randomType until resistantList is not null or empty
                    random.randomOfList(resistantList.toMutableList())
                }
                // Return a type from the pickList
                else -> random.randomOfList(pickList.toMutableList())
            }

        }

        fun getWeaknesses(checkType: Type, maxNum: Int): List<Type> {
            // Return an empty list if we're not getting any types
            if (maxNum <= 0) {
                return emptyList()
            }
            // Return an empty list if STRONG_AGAINST is null
            val checkList = STRONG_AGAINST[checkType] ?: return emptyList()
            // Get a sublist up to the maxNum, or as many as we can
            return checkList.subList(0, minOf(maxNum, checkList.size))
        }

        fun typesToInt(types: List<Type>?): Int {
            if (types == null || types.size > Int.SIZE_BITS) {
                // No can do
                return 0
            }
            var initial = 0
            var state = 1
            for (t in entries) {
                initial = initial or (if (types.contains(t)) state else 0)
                state *= 2
            }
            return initial
        }

        fun intToTypes(types: Int): List<Type>? {
            if (types == 0) {
                return null
            }
            val typesList = mutableListOf<Type>()
            var state = 1
            entries.forEach { entry ->
                if ((types and state) > 0) {
                    typesList.add(entry)
                }
                state *= 2
            }

            return typesList
        }

        fun getCombinedResistanceMap(): Map<Type, List<Type>> {
            return RESISTANT_TO.mapValues { (type, resistantTo) ->
                resistantTo + (IMMUNE_TO[type] ?: emptyList())
            }
        }

        /**
         * Update the STRONG_AGAINST map such that STRONG_AGAINST_<defender> includes attacker
         *
         * @param attacker - Type of the attacker
         * @param defender - Type of the defender
         */
        fun updateStrongAgainst(attacker: Type, defender: Type) {
            STRONG_AGAINST.getOrPut(defender) { mutableListOf() }.add(attacker)
        }

        /**
         * Update the RESISTANT_TO map such that RESISTANT_TO_<attacker> includes defender
         *
         * @param attacker - Type of the attacker
         * @param defender - Type of the defender
         */
        fun updateResistantTo(attacker: Type, defender: Type) {
            RESISTANT_TO.getOrPut(attacker) { mutableListOf() }.add(defender)
        }

        /**
         * Update the IMMUNE_TO map such that IMMUNE_TO_<attacker> includes defender
         *
         * @param attacker - Type of the attacker
         * @param defender - Type of the defender
         */
        fun updateImmuneTo(attacker: Type, defender: Type) {
            IMMUNE_TO.getOrPut(attacker) { mutableListOf() }.add(defender)
        }
    }
}