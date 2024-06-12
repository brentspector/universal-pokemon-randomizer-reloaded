package pokemon

import logicModules.RandomSource
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class StatBucket(val median: Double, val standardDeviation: Double, val skew: Double) {
    GENERIC(median = 411.5, standardDeviation = 108.5, skew = -0.1),
    LEGENDARY(median = 650.0, standardDeviation = 60.0, skew = 0.5),
    HAS_EVOS(median = 300.0, standardDeviation = 37.0, skew = -0.9),
    NO_EVOS(median = 487.0, standardDeviation = 94.0, skew = -0.2)
}
data class Pokemon(val name: String, var primaryType: Type) {
    val number: Int = 0
    var secondaryType:Type? = null

    var hp: Int = 0
    var attack: Int = 0
    var defense: Int = 0
    var spatk: Int = 0
    var spdef: Int = 0
    var speed: Int = 0
    var special: Int = 0

    var abilities: IntArray = IntArray(3)

    var catchRate: Int = 0
    var expYield: Int = 0

    var guaranteedHeldItem: Int = 0
    var commonHeldItem: Int = 0
    var rareHeldItem: Int = 0
    var darkGrassHeldItem: Int = 0

    var genderRatio: Int = 0

    var frontSpritePointer: Int = 0
    var picDimensions: Int = 0

    // TODO: Make ExpCurve class
    //var growthCurve: ExpCurve? = null

    var evolutionsFrom: MutableList<Evolution> = mutableListOf()
    var evolutionsTo: MutableList<Evolution> = mutableListOf()

    var shuffledStatsOrder: MutableList<Int> = mutableListOf(0, 1, 2, 3, 4, 5)
    var typeChanged: Int = 0

    // A flag to use for things like recursive stats copying.
    // Must not rely on the state of this flag being preserved between calls.
    var temporaryFlag: Boolean = false

    // TODO: Move to global constants
    private val legendaries: MutableList<Int> = mutableListOf(
        144, 145, 146, 150, 151, 243,
        244, 245, 249, 250, 251, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 480, 481,
        482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 638, 639, 640, 641,
        642, 643, 644, 645, 646, 647, 648, 649
    )


    // Allows recomputing isCyclic if the evolutionsFrom changes
    private var _isCyclic: Boolean? = null
    val isCyclic: Boolean
        get() {
            // Either get the calculated value, or compute, store, and return it
            return _isCyclic ?: computeIsCyclic().also { _isCyclic = it }
        }

    fun forceRecomputeIsCyclic() {
        _isCyclic = null
    }

    private fun computeIsCyclic(): Boolean {
        val visited = mutableSetOf<Pokemon>()
        val recStack = mutableSetOf<Pokemon>()

        fun Pokemon.isCyclic(): Boolean {
            if (!visited.add(this)) return true // If already visited, it's cyclic
            recStack.add(this) // Add to recursion stack
            for (ev in evolutionsFrom) {
                if (recStack.contains(ev.to) || ev.to.isCyclic()) {
                    return true // If the Pokemon is in recursion stack or found cyclic
                }
            }
            recStack.remove(this) // Remove from recursion stack if no cycles found
            return false
        }

        return isCyclic()
    }

    // Select a type from the ones on this pokemon
    fun randomOfTypes(random: RandomSource): Type {
        return if (random.nextBoolean()) primaryType else secondaryType ?: primaryType
    }

    fun assignTypeByReference(
        ref: Pokemon,
        typesDiffer: Int,
        defaultFunction: () -> Type
    ) {
        when (typesDiffer) {
            // No difference, just copy the reference
            0 -> {
                primaryType = ref.primaryType
                secondaryType = ref.secondaryType
                typeChanged = ref.typeChanged
            }
            1 -> assignTypePrimaryTypesDiffer(ref, defaultFunction)
            2 -> assignTypeSecondaryTypesDiffer(ref, defaultFunction)
            3 -> assignTypePrimaryMatchesSecondary(ref, defaultFunction)
            4 -> assignTypeSecondaryMatchesPrimary(ref, defaultFunction)
        }
    }

    // Zubat (Poison/Flying) -> Butterfree (Bug/Flying)
    private fun assignTypePrimaryTypesDiffer(ref: Pokemon, defaultFunction: () -> Type) {
        // If the primary type changed, change the primary type of this one, otherwise keep the primary type
        primaryType = if (ref.typeChanged == 1) defaultFunction() else primaryType
        // If the secondary type changed, copy the secondary type, otherwise keep the secondary type
        secondaryType = if (ref.typeChanged == 2) ref.secondaryType else secondaryType
        typeChanged = ref.typeChanged
        handlePrimarySecondaryEquality(defaultFunction)
    }

    // Paras (Bug/Grass) -> Butterfree (Bug/Flying)
    private fun assignTypeSecondaryTypesDiffer(ref: Pokemon, defaultFunction: () -> Type) {
        // If the primary type changed, copy the primary type, otherwise keep the primary type
        primaryType = if (ref.typeChanged == 1) ref.primaryType else primaryType
        // If the secondary type changed and this secondary type is not null,
        // change the secondary type of this one, otherwise keep the secondary type
        secondaryType = if (ref.typeChanged == 2 && secondaryType != null) defaultFunction() else secondaryType
        // Copy the ref if possible, otherwise set type changed to 1 when this only has a primary type
        typeChanged = if (secondaryType != null) ref.typeChanged else 1
        handlePrimarySecondaryEquality(defaultFunction)
    }

    // Noibat (Flying/Dragon) -> Butterfree (Bug/Flying)
    private fun assignTypePrimaryMatchesSecondary(ref: Pokemon, defaultFunction: () -> Type) {
        // If the primary type changed, keep the primary type, otherwise change the primary type of this one
        primaryType = if (ref.typeChanged == 1) primaryType else defaultFunction()
        // If the secondary type changed, copy the primary type, otherwise keep the secondary type
        secondaryType = if (ref.typeChanged == 1) ref.primaryType else secondaryType
        // If the primary type changed, then this one's secondary type changed, otherwise the primary changed
        typeChanged = if (ref.typeChanged == 1) 2 else 1
        handlePrimarySecondaryEquality(defaultFunction)
    }

    // Anorith (Rock/Bug) -> Butterfree (Bug/Flying)
    private fun assignTypeSecondaryMatchesPrimary(ref: Pokemon, defaultFunction: () -> Type) {
        // If the secondary type changed, copy the secondary type, otherwise keep this primary type unless the secondary type
        // is null, then copy the primary type to enable some level of change
        primaryType = if (ref.typeChanged == 2) ref.secondaryType!! else if (secondaryType == null) ref.primaryType else primaryType
        // If the primary type changed and our secondary type isn't null,
        // change the secondary type of this one, otherwise keep the secondary type
        secondaryType = if (ref.typeChanged == 1 && secondaryType != null) defaultFunction() else secondaryType
        // If the primary type changed and our secondary type isn't null, then this one's secondary type changed,
        // otherwise the primary changed
        typeChanged = if (ref.typeChanged == 1 && secondaryType != null) 2 else 1
        handlePrimarySecondaryEquality(defaultFunction)
    }

    private fun handlePrimarySecondaryEquality(defaultFunction: () -> Type) {
        while (primaryType === secondaryType) {
            primaryType = if (typeChanged == 1) defaultFunction() else primaryType
            secondaryType = if (typeChanged == 2) defaultFunction() else secondaryType
        }
    }

    fun getRandomWeakness(random: RandomSource, useResistantType: Boolean): Type? {
        return Type.randomWeakness(random, useResistantType, primaryType, this.secondaryType)
    }

    fun isWeakTo(pk: Pokemon): Boolean {
        val primaryImmunity = Type.IMMUNE_TO[pk.primaryType]?.run {
            primaryType in this || secondaryType in this
        } ?: false

        val secondaryImmunity = Type.IMMUNE_TO[pk.secondaryType]?.run {
            primaryType in this || secondaryType in this
        } ?: false

        val primaryWeakness = Type.STRONG_AGAINST[primaryType]?.run {
            (pk.primaryType in this && !primaryImmunity) || (pk.secondaryType in this && !secondaryImmunity)
        } ?: false

        val secondaryWeakness = secondaryType?.let {
            Type.STRONG_AGAINST[it]?.run {
                (pk.primaryType in this && !primaryImmunity) || (pk.secondaryType in this && !secondaryImmunity)
            }
        } ?: false

        return primaryWeakness || secondaryWeakness
    }

    fun evolutionChainSize(): Int {
        return (evolutionsFrom.maxOfOrNull { it.to.evolutionChainSize() } ?: 0) + 1
    }

    fun minimumLevel(): Int {
        return evolutionsTo.maxOfOrNull { evo ->
            if (evo.type.usesLevel()) {
                evo.extraInfo
            } else {
                // TODO: Make this support all EvolutionTypes (MoveLearnt at what level?)
                when (evo.type) {
                    EvolutionType.STONE, EvolutionType.STONE_FEMALE_ONLY, EvolutionType.STONE_MALE_ONLY -> 24
                    EvolutionType.TRADE, EvolutionType.TRADE_ITEM, EvolutionType.TRADE_SPECIAL -> 37
                    else -> 33
                }
            }
        } ?: 1
    }

    fun nearestEvoTarget(level: Int): Int {
        return evolutionsFrom.withIndex().lastOrNull { (index, evo) ->
            val evoMin = if (evo.type.usesLevel()) {
                evo.extraInfo
            } else {
                when (evo.type) {
                    EvolutionType.STONE, EvolutionType.STONE_FEMALE_ONLY, EvolutionType.STONE_MALE_ONLY -> 24
                    EvolutionType.TRADE, EvolutionType.TRADE_ITEM, EvolutionType.TRADE_SPECIAL -> 37
                    else -> 33
                }
            }
            evoMin <= level
        }?.index ?: -1
    }


    fun shuffleStats(random: RandomSource) {
        random.shuffleList(shuffledStatsOrder)
        applyShuffledOrderToStats()
    }

    fun copyShuffledStatsUpEvolution(evolvesFrom: Pokemon) {
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder
        applyShuffledOrderToStats()
    }

    private fun applyShuffledOrderToStats() {
        val stats: MutableList<Int> = mutableListOf(hp, attack, defense, spatk, spdef, speed)

        // Copy in new stats
        hp = stats[shuffledStatsOrder[0]]
        attack = stats[shuffledStatsOrder[1]]
        defense = stats[shuffledStatsOrder[2]]
        spatk = stats[shuffledStatsOrder[3]]
        spdef = stats[shuffledStatsOrder[4]]
        speed = stats[shuffledStatsOrder[5]]

        // make special the average of spatk and spdef
        special = ceil((spatk + spdef) / 2.0).toInt()
    }

    fun randomizeStatsWithinBST(random: RandomSource) {
        // TODO: Fix so that it's based on anything with Wonder Guard
        // Stat value being manually assigned
        val reservedStats: Int = 50
        // Number of stats reservedStats is being distributed to
        val reservedTraits: Int = 5
        if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            val bst = bst() - reservedStats - 1

            // Make weightings
            val atkW = random.nextDouble()
            val defW = random.nextDouble()
            val spaW = random.nextDouble()
            val spdW = random.nextDouble()
            val speW = random.nextDouble()
            val totW = atkW + defW + spaW + spdW + speW
            hp = 1
            attack = max(1.0, (atkW / totW * bst)).toInt() + reservedStats/reservedTraits
            defense = max(1.0, (defW / totW * bst)).toInt() + reservedStats/reservedTraits
            spatk = max(1.0, (spaW / totW * bst)).toInt() + reservedStats/reservedTraits
            spdef = max(1.0, (spdW / totW * bst)).toInt() + reservedStats/reservedTraits
            speed = max(1.0, (speW / totW * bst)).toInt() + reservedStats/reservedTraits

            // Fix up special too
            special = ceil((spatk + spdef) / 2.0).toInt()
        } else {
            var bst = bst() - reservedStats
            val minimumHp = 35

            // Make weightings
            val hpW = random.nextDouble()
            val atkW = random.nextDouble()
            val defW = random.nextDouble()
            val spaW = random.nextDouble()
            val spdW = random.nextDouble()
            val speW = random.nextDouble()
            val totW = hpW + atkW + defW + spaW + spdW + speW

            // TODO: Revisit and ensure the randomized stats match the total
            // Handle HP specially to avoid skewing
            val suggestedHp: Int = (hpW / totW * bst).roundToInt()
            hp = max(suggestedHp, minimumHp)
            // If the suggestedHP is lower than minimumHP, remove the difference
            // from the remaining bst stat pool
            if (suggestedHp < minimumHp) {
                bst -= (minimumHp - suggestedHp)
            }

            // Handle the rest normally
            attack = max(1.0, (atkW / totW * bst)).toInt() + reservedStats/reservedTraits
            defense = max(1.0, (defW / totW * bst)).toInt() + reservedStats/reservedTraits
            spatk = max(1.0, (spaW / totW * bst)).toInt() + reservedStats/reservedTraits
            spdef = max(1.0, (spdW / totW * bst)).toInt() + reservedStats/reservedTraits
            speed = max(1.0, (speW / totW * bst)).toInt() + reservedStats/reservedTraits

            // Fix up special too
            special = ceil((spatk + spdef) / 2.0).toInt()
        }

        // TODO: Revisit and see if this can be done without requiring reroll
        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random)
        }
    }

    fun copyRandomizedStatsUpEvolution(evolvesFrom: Pokemon, random: RandomSource) {
        val maximumStat = 255
        val cutHpGrowth = 0.1
        val absoluteHpMax = maximumStat / (1 - cutHpGrowth )
        val growthRatio = 1 + cutHpGrowth
        val ourBST = bst().toDouble()
        val theirBST = evolvesFrom.bst().toDouble()
        val bstRatio = ourBST / theirBST

        // Lower HP growth to allow other stats a chance to grow (except when growth is
        // already under that growth)
        hp = min(absoluteHpMax, max(1.0, (evolvesFrom.hp * bstRatio))).toInt()
        val hpDiff = if (growthRatio < bstRatio) (hp * cutHpGrowth).toInt() else 0
        hp -= hpDiff

        // Convert HPDiff into series of ints
        val hpInt = hpDiff / 5
        val hpRem = hpDiff % 5
        val hpArray = intArrayOf(hpInt, hpInt, hpInt, hpInt, hpInt)

        // Add remainder to random spots in hpArray
        repeat (hpRem) {
            hpArray[abs(random.nextInt() % 5)]++
        }

        // TODO: Revisit and do not exceed 255, and redistribute the bst if it exceeds 255
        // Add HPDiff to remaining stats
        attack = min(max(1.0, evolvesFrom.attack * bstRatio).toInt(), maximumStat) + hpArray[0]
        defense = min(max(1.0, evolvesFrom.defense * bstRatio).toInt(), maximumStat) + hpArray[1]
        speed = min(max(1.0, evolvesFrom.speed * bstRatio).toInt(), maximumStat) + hpArray[2]
        spatk = min(max(1.0, evolvesFrom.spatk * bstRatio).toInt(), maximumStat) + hpArray[3]
        spdef = min(max(1.0, evolvesFrom.spdef * bstRatio).toInt(), maximumStat) + hpArray[4]
        special = ceil((spatk + spdef) / 2.0).toInt()
    }

    private fun skewedGaussian(random: RandomSource, statBucket: StatBucket): Double {
        val gaussian: Double = random.nextDouble()
        val centering = 0.5
        val exponentialFunction: Double = exp(gaussian * statBucket.skew)
        val skewedCdf = (1 - exponentialFunction) / (2 * (1 + exponentialFunction)) + centering
        val deviation = gaussian * skewedCdf * statBucket.standardDeviation
        return statBucket.median + deviation
    }

    private fun distributeBstRandomly(random: RandomSource, bst: Int, statMinMax: Pair<Int, Int>, hpMinMax: Pair<Int, Int>): List<Int> {
        val stats = IntArray(6)

        // Track remaining bsts, reserving the min for stats as a worst-case scenario
        // We know HP is one of the stats, so we only use statMinMax for the remaining stats
        val remainingBsts = bst - hpMinMax.first - (statMinMax.first * (stats.size - 1))

        // Generate random break points as percent weights for all but 1 stat
        val breakpoints = DoubleArray(stats.size-1) { random.nextDouble() }.sorted()

        // First breakpoint is HP. Handle separately to avoid consuming too few BSTs
        var thisStat = (breakpoints[0] * remainingBsts).toInt()
        stats[0] = min(max(thisStat, hpMinMax.first), hpMinMax.second)

        for (i in 1 until stats.size-1) {
            // thisStat is the gap between this breakpoint and the previous
            thisStat = ((breakpoints[i] - breakpoints[i-1]) * remainingBsts).toInt()
            // Try to keep stat as stat, but increase to min or lower to max if outside min-max range
            stats[i] = min(max(thisStat, statMinMax.first), statMinMax.second)
        }

        // The last stat is the remaining bst
        stats[5] = bst - stats.sum()
        stats[5] = min(max(stats[5], statMinMax.first), statMinMax.second)

        // Adjust stats to ensure they add up to the total bst
        // Ignore HP to avoid skewing
        var overUnder = stats.sum() - bst
        while (overUnder != 0) {
            for (i in 1 until 6) {
                // Too many stats were used
                if (overUnder > 0) {
                    var delta = max((random.nextDouble() * overUnder).toInt(), 1)
                    // Either subtract the stats suggested, or the maximum amount available without exceeding min
                    delta = min(delta, stats[i] - statMinMax.first)
                    stats[i] -= delta
                    overUnder -= delta
                }
                // Too few stats were used
                else if (overUnder < 0) {
                    var delta = min((random.nextDouble() * overUnder).toInt(), -1)
                    // Either add the stats suggested, or the maximum amount available without exceeding max
                    delta = min(delta, stats[i] - statMinMax.second)
                    stats[i] -= delta
                    overUnder -= delta
                }
                // We're at 0, end the loop
                else break
            }
        }

        return stats.toList()
    }
//    fun randomizeStatsNoRestrictions(random: RandomSource, evolutionSanity: Boolean) {
//        // Stat value being manually assigned
//        val reservedStats: Int = 50
//        // Number of stats reservedStats is being distributed to
//        val reservedTraits: Int = 5
//        // Total possible percent (100) divided by stats available (6)
//        val weightSd = 100/6
//        // TODO: Revisit shedninja
//        if (number == 292) {
//            // Shedinja is horribly broken unless we restrict him to 1HP.
//            val reserveHP = 1
//            val statBucket = if (evolutionSanity) StatBucket.NO_EVOS else StatBucket.GENERIC
//            val bst = (skewedGaussian(random, statBucket) - reservedStats / reservedTraits - reserveHP).toInt()
//
//            // Make weightings
//            val atkW = max(0.0, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val defW = max(0.0, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val spaW = max(0.0, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val spdW = max(0.0, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val speW = max(0.0, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val totW = atkW + defW + spaW + spdW + speW
//            hp = 1
//            attack = max(1.0, java.lang.Math.round(atkW / totW * bst).toDouble()).toInt() + 10
//            defense = max(1.0, java.lang.Math.round(defW / totW * bst).toDouble()).toInt() + 10
//            spatk = max(1.0, java.lang.Math.round(spaW / totW * bst).toDouble()).toInt() + 10
//            spdef = max(1.0, java.lang.Math.round(spdW / totW * bst).toDouble()).toInt() + 10
//            speed = max(1.0, java.lang.Math.round(speW / totW * bst).toDouble()).toInt() + 10
//
//            // Fix up special too
//            special = ceil(((spatk + spdef) / 2.0f).toDouble()).toInt()
//        } else {
//            val skewedValue = when {
//                evolutionSanity && evolutionsFrom.isNotEmpty() -> skewedGaussian(random, StatBucket.HAS_EVOS)
//                evolutionSanity -> skewedGaussian(random, StatBucket.NO_EVOS)
//                else -> skewedGaussian(random, StatBucket.GENERIC)
//            }
//            val bst = (skewedValue - reservedStats / reservedTraits).toInt()
//
//            // Make weightings
//            val hpW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val atkW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val defW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val spaW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val spdW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val speW = max(0.01, min(1.0, random.nextGaussian() * weightSd + 0.5))
//            val totW = hpW + atkW + defW + spaW + spdW + speW
//
//            // Handle HP specially to avoid skewing
//            val suggestedHP: Float = java.lang.Math.round(hpW / totW * bst).toFloat()
//            hp = if (suggestedHP < 35) 35 else suggestedHP.toInt()
//            // Remove any added stats from the remaining bst
//            bst = (bst - if (suggestedHP < 35) 35 - suggestedHP else 0).toInt()
//
//            // Handle the rest normally
//            attack = max(1.0, java.lang.Math.round(atkW / totW * bst).toDouble()).toInt() + 10
//            defense = max(1.0, java.lang.Math.round(defW / totW * bst).toDouble()).toInt() + 10
//            spatk = max(1.0, java.lang.Math.round(spaW / totW * bst).toDouble()).toInt() + 10
//            spdef = max(1.0, java.lang.Math.round(spdW / totW * bst).toDouble()).toInt() + 10
//            speed = max(1.0, java.lang.Math.round(speW / totW * bst).toDouble()).toInt() + 10
//
//            // Fix up special too
//            special = ceil(((spatk + spdef) / 2.0f).toDouble()).toInt()
//        }
//
//        // Check for something we can't store
//        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
//            // re roll
//            randomizeStatsNoRestrictions(random, evolutionSanity)
//        }
//    }
//    fun copyRandomizedStatsNoRestrictionsUpEvolution(
//        evolvesFrom: Pokemon,
//        random: RandomSource
//    ) {
//        val theirBST = evolvesFrom.bst().toDouble()
//        var ourBST: Double
//        var bstRatio: Double
//        do {
//            ourBST = if (evolutionsFrom.size > 0 || evolutionsTo[0].from.evolutionsTo.size() > 0) {
//                // 3 stages
//                (theirBST + PK_2EVOS_DIFF_MEDIAN
//                        + (skewedGaussian(random.nextGaussian(), PK_2EVOS_DIFF_SKEW)
//                        * PK_2EVOS_DIFF_SD))
//            } else {
//                // 2 stages
//                (theirBST + PK_1EVO_DIFF_MEDIAN
//                        + (skewedGaussian(random.nextGaussian(), PK_1EVO_DIFF_SKEW)
//                        * PK_1EVO_DIFF_SD))
//            }
//            bstRatio = ourBST / theirBST
//        } while (bstRatio < 1)
//        hp =
//            min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.hp * bstRatio).toDouble()))
//                .toInt()
//        attack =
//            min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.attack * bstRatio).toDouble()))
//                .toInt()
//        defense =
//            min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.defense * bstRatio).toDouble()))
//                .toInt()
//        speed = min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.speed * bstRatio).toDouble()))
//            .toInt()
//        spatk = min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.spatk * bstRatio).toDouble()))
//            .toInt()
//        spdef = min(255.0, max(1.0, java.lang.Math.round(evolvesFrom.spdef * bstRatio).toDouble()))
//            .toInt()
//        special = ceil(((spatk + spdef) / 2.0f).toDouble()).toInt()
//    }

    // TODO: Make nextGaussian
//    fun copyCompletelyRandomizedStatsUpEvolution(
//        evolvesFrom: Pokemon, random: RandomSource,
//        meanBST: Double
//    ) {
//        val theirBST = evolvesFrom.bst().toDouble()
//        val ratio = meanBST / theirBST
//        val mean: Double
//        val stdDev: Double
//
//        // mean < 201 e.g. Caterpie, Weedle, Metapod, Kakuna, Magikarp
//        if (ratio > 1.7) {
//            mean = 0.5
//            stdDev = 0.3 // Average multiplier = 1.5x (301), max multiplier = 2.7x (540)
//        } else if (ratio > 1.4) {
//            mean = 0.5
//            stdDev = 0.2 // Average multiplier = 1.5x, (366) max multiplier = 2.3x (561)
//        } else if (ratio > 1.0) {
//            mean = 0.25
//            stdDev = 0.1 // Average multiplier = 1.25x (427), max multiplier = 1.65x (564)
//        } else if (ratio > 0.8) {
//            mean = 0.0
//            stdDev = 0.1 // Average multiplier = 1.0x (427), max multiplier = 1.4x (597)
//        } else if (ratio > 0.7) {
//            mean = -0.1
//            stdDev = 0.1 // Average multiplier = 0.9x (488), max multiplier = 1.3x (634) [clamped
//            // to 1.0x min]
//        } else {
//            mean = -0.2
//            stdDev = 0.1 // Average multiplier = 0.8x (~500), max multiplier = 1.2x (760 = max roll
//            // of above group max rolled again)
//        }
//        val multiplier = max(1.05 + mean + random.nextGaussian() * stdDev, 1.05)
//
//        // Allow each stat to vary by +- 5% so stats vary a little between them
//        hp = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.hp * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        attack = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.attack * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        defense = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.defense * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        speed = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.speed * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        spatk = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.spatk * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        spdef = min(
//            255.0, max(
//                1.0,
//                java.lang.Math.round(evolvesFrom.spdef * multiplier * (0.95 + random.nextDouble() / 10))
//                    .toDouble()
//            )
//        )
//            .toInt()
//        special = ceil(((spatk + spdef) / 2.0f).toDouble()).toInt()
//    }

    fun isLegendary(): Boolean {
        return legendaries.contains(number)
    }

    fun isBigPoke(isGen1: Boolean): Boolean {
        val bigBst = when {
            isGen1 -> gen1Bst() > 490
            else -> bst() > 590
        }
        val bigStat = when {
            isGen1 -> (listOf(hp, attack, defense, speed, special).maxOrNull() ?: 0) > 190
            else -> (listOf(hp, attack, defense, speed, spatk, spdef).maxOrNull() ?: 0) > 190
        }
        return bigBst || bigStat
    }

    fun bst(): Int {
        return hp + attack + defense + spatk + spdef + speed
    }

    fun gen1Bst(): Int {
        return hp + attack + defense + special + speed
    }

    // TODO: Revisit shedninja
    fun bstForPowerLevels(): Int {
        // Take into account Shedinja's purposefully nerfed HP
        return if (number == 292) {
            (attack + defense + spatk + spdef + speed) * 6 / 5
        } else {
            hp + attack + defense + spatk + spdef + speed
        }
    }
}
