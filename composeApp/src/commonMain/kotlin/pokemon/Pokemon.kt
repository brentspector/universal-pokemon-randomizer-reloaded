package pokemon

import logicModules.RandomSource
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class Pokemon(val name: String, var primaryType: Type) {
    private val GENERAL_MEDIAN = 411.5
    private val GENERAL_SD = 108.5
    private val GENERAL_SKEW = -0.1
    private val EVO1_2EVOS_MEDIAN = 300.0
    private val EVO1_2EVOS_SD = 37.0
    private val EVO1_2EVOS_SKEW = -0.9
    private val PK_2EVOS_DIFF_MEDIAN = 100.0
    private val PK_2EVOS_DIFF_SD = 44.0
    private val PK_2EVOS_DIFF_SKEW = 0.7
    private val EVO1_1EVO_MEDIAN = 310.0
    private val EVO1_1EVO_SD = 44.0
    private val EVO1_1EVO_SKEW = -0.6
    private val PK_1EVO_DIFF_MEDIAN = 162.5
    private val PK_1EVO_DIFF_SD = 36.0
    private val PK_1EVO_DIFF_SKEW = 0.5
    private val NO_EVO_MEDIAN = 487.0
    private val NO_EVO_SD = 94.0
    private val NO_EVO_SKEW = -0.2
    private val MAX_EVO_MEDIAN = 490.0
    private val MAX_EVO_SD = 43.5
    private val MAX_EVO_SKEW = -0.3

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

    // TODO: Make Evolution class
    //var evolutionsFrom: List<Evolution> = java.util.ArrayList<Evolution>()
    //var evolutionsTo: List<Evolution> = java.util.ArrayList<Evolution>()

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
    // TODO: Make Evolution class
//    fun evolutionChainSize(): Int {
//        var length = 0
//        for (ev in evolutionsFrom) {
//            val temp: Int = ev.to.evolutionChainSize()
//            if (temp > length) {
//                length = temp
//            }
//        }
//        return length + 1
//    }
    // TODO: Make Evolution class
//    fun isCyclic(visited: MutableSet<Pokemon?>, recStack: MutableSet<Pokemon?>): Boolean {
//        if (!visited.contains(this)) {
//            visited.add(this)
//            recStack.add(this)
//            for (ev in evolutionsFrom) {
//                if (!visited.contains(ev.to) && ev.to.isCyclic(visited, recStack)) {
//                    return true
//                } else if (recStack.contains(ev.to)) {
//                    return true
//                }
//            }
//        }
//        recStack.remove(this)
//        return false
//    }
    // TODO: Make Evolution class
//    fun minimumLevel(): Int {
//        var min = 1
//        for (evo in evolutionsTo) {
//            var evoMin = 1
//            evoMin = if (evo.type.usesLevel()) {
//                evo.extraInfo
//            } else {
//                // TODO: Make this better (move MoveLearnt to Pokemon, etc.).
//                when (evo.type) {
//                    MeasureUnit.STONE, STONE_FEMALE_ONLY, STONE_MALE_ONLY -> 24
//                    TRADE, TRADE_ITEM, TRADE_SPECIAL -> 37
//                    else -> 33
//                }
//            }
//            if (evoMin > min) {
//                min = evoMin
//            }
//        }
//        return min
//    }
    // TODO: Make Evolution class
//    fun nearestEvoTarget(level: Int): Int {
//        var target = -1
//        var evoMin = -1
//        for (i in evolutionsFrom.indices) {
//            evoMin = if (evolutionsFrom[i].type.usesLevel()) {
//                evolutionsFrom[i].extraInfo
//            } else {
//                when (evolutionsFrom[i].type) {
//                    MeasureUnit.STONE, STONE_FEMALE_ONLY, STONE_MALE_ONLY -> 24
//                    TRADE, TRADE_ITEM, TRADE_SPECIAL -> 37
//                    else -> 33
//                }
//            }
//
//            // Target represents the evolution index
//            target = if (evoMin <= level) i else target
//        }
//        return target
//    }

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
        if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            val bst = bst() - 51

            // Make weightings
            val atkW = random.nextDouble()
            val defW = random.nextDouble()
            val spaW = random.nextDouble()
            val spdW = random.nextDouble()
            val speW = random.nextDouble()
            val totW = atkW + defW + spaW + spdW + speW
            hp = 1
            attack = max(1.0, (atkW / totW * bst)).toInt() + 10
            defense = max(1.0, (defW / totW * bst)).toInt() + 10
            spatk = max(1.0, (spaW / totW * bst)).toInt() + 10
            spdef = max(1.0, (spdW / totW * bst)).toInt() + 10
            speed = max(1.0, (speW / totW * bst)).toInt() + 10

            // Fix up special too
            special = ceil((spatk + spdef) / 2.0).toInt()
        } else {
            // Minimum 10 everything not including HP
            var bst = bst() - 50
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
            attack = max(1.0, (atkW / totW * bst)).toInt() + 10
            defense = max(1.0, (defW / totW * bst)).toInt() + 10
            spatk = max(1.0, (spaW / totW * bst)).toInt() + 10
            spdef = max(1.0, (spdW / totW * bst)).toInt() + 10
            speed = max(1.0, (speW / totW * bst)).toInt() + 10

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
    // TODO: Make Evolution class, add nextGaussian to RandomSource
//    fun randomizeStatsNoRestrictions(random: RandomSource, evolutionSanity: Boolean) {
//        val weightSd = 0.16
//        // TODO: Revisit shedninja
//        if (number == 292) {
//            // Shedinja is horribly broken unless we restrict him to 1HP.
//            val bst: Int
//            bst = if (evolutionSanity) {
//                (PK_1EVO_DIFF_MEDIAN
//                        + skewedGaussian(random.nextGaussian(), PK_1EVO_DIFF_SKEW) * PK_1EVO_DIFF_SD
//                        - 51).toInt()
//            } else {
//                (GENERAL_MEDIAN
//                        + skewedGaussian(
//                    random.nextGaussian(),
//                    GENERAL_SKEW
//                ) * GENERAL_SD - 51).toInt()
//            }
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
//            // Minimum 10 everything not including HP
//            var bst: Int
//            if (evolutionSanity) {
//                if (evolutionsFrom.size > 0) {
//                    var pk2Evos = false
//                    for (ev in evolutionsFrom) {
//                        // If any of the targets here evolve, the original
//                        // Pokemon has 2+ stages.
//                        if (ev.to.evolutionsFrom.size() > 0) {
//                            pk2Evos = true
//                            break
//                        }
//                    }
//                    bst = if (pk2Evos) {
//                        // First evo of 3 stages
//                        (EVO1_2EVOS_MEDIAN
//                                + skewedGaussian(random.nextGaussian(), EVO1_2EVOS_SKEW)
//                                * EVO1_2EVOS_SD
//                                - 50).toInt()
//                    } else {
//                        // First evo of 2 stages
//                        (EVO1_1EVO_MEDIAN
//                                + skewedGaussian(random.nextGaussian(), EVO1_1EVO_SKEW)
//                                * EVO1_1EVO_SD
//                                - 50).toInt()
//                    }
//                } else {
//                    bst = if (evolutionsTo.size > 0) {
//                        // Last evo, doesn't carry stats
//                        (MAX_EVO_MEDIAN
//                                + skewedGaussian(random.nextGaussian(), MAX_EVO_SKEW) * MAX_EVO_SD
//                                - 50).toInt()
//                    } else {
//                        // No evolutions, no pre-evolutions
//                        (NO_EVO_MEDIAN
//                                + skewedGaussian(random.nextGaussian(), NO_EVO_SKEW) * NO_EVO_SD
//                                - 50).toInt()
//                    }
//                }
//            } else {
//                // No 'Follow evolutions'
//                bst = (GENERAL_MEDIAN
//                        + skewedGaussian(
//                    random.nextGaussian(),
//                    GENERAL_SKEW
//                ) * GENERAL_SD - 50).toInt()
//            }
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
    // TODO: Create Evolution class, nextGaussian
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

    private fun skewedGaussian(gaussian: Double, skew: Double): Double {
        val skewedCdf = ((1 - exp(-1.7 * gaussian * skew))
                / (2 * (1 + exp(-1.7 * gaussian * skew))) + 0.5)
        return 2 * gaussian * skewedCdf
    }
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
