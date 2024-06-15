package pokemon

data class TypeRelationship(var attacker: Type, var defender: Type, var effectiveness: Effectiveness) {
    enum class Effectiveness {
        ZERO,
        HALF,
        NEUTRAL,
        DOUBLE
    }

    companion object {
        val STRONG_AGAINST_NORMAL: MutableList<Type> = mutableListOf(Type.FIGHTING)
        val RESISTANT_TO_NORMAL: MutableList<Type> = mutableListOf(Type.ROCK, Type.STEEL)
        val IMMUNE_TO_NORMAL: MutableList<Type> = mutableListOf(Type.GHOST)
        val STRONG_AGAINST_FIGHTING: MutableList<Type> =
            mutableListOf(Type.FLYING, Type.PSYCHIC)
        val RESISTANT_TO_FIGHTING: MutableList<Type> =
            mutableListOf(Type.POISON, Type.FLYING, Type.PSYCHIC, Type.BUG)
        val IMMUNE_TO_FIGHTING: MutableList<Type> = mutableListOf(Type.GHOST)
        val STRONG_AGAINST_FLYING: MutableList<Type> =
            mutableListOf(Type.ELECTRIC, Type.ICE, Type.ROCK)
        val RESISTANT_TO_FLYING: MutableList<Type> =
            mutableListOf(Type.ELECTRIC, Type.ROCK, Type.STEEL)
        val STRONG_AGAINST_GRASS: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.ICE, Type.POISON, Type.FLYING, Type.BUG)
        val RESISTANT_TO_GRASS: MutableList<Type> = mutableListOf(
            Type.FIRE, Type.GRASS,
            Type.POISON, Type.FLYING, Type.BUG, Type.DRAGON, Type.STEEL
        )
        val STRONG_AGAINST_WATER: MutableList<Type> =
            mutableListOf(Type.ELECTRIC, Type.GRASS)
        val RESISTANT_TO_WATER: MutableList<Type> =
            mutableListOf(Type.WATER, Type.GRASS, Type.DRAGON)
        val STRONG_AGAINST_FIRE: MutableList<Type> =
            mutableListOf(Type.WATER, Type.GROUND, Type.ROCK)
        val RESISTANT_TO_FIRE: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.WATER, Type.ROCK, Type.DRAGON)
        val STRONG_AGAINST_ROCK: MutableList<Type> = mutableListOf(
            Type.WATER, Type.GRASS,
            Type.FIGHTING, Type.GROUND, Type.STEEL
        )
        val RESISTANT_TO_ROCK: MutableList<Type> =
            mutableListOf(Type.FIGHTING, Type.GROUND, Type.STEEL)
        val STRONG_AGAINST_GROUND: MutableList<Type> =
            mutableListOf(Type.WATER, Type.GRASS, Type.ICE)
        val RESISTANT_TO_GROUND: MutableList<Type> = mutableListOf(Type.GRASS, Type.BUG)
        val IMMUNE_TO_GROUND: MutableList<Type> = mutableListOf(Type.FLYING)
        val STRONG_AGAINST_PSYCHIC: MutableList<Type> =
            mutableListOf(Type.BUG, Type.GHOST, Type.DARK)
        val RESISTANT_TO_PSYCHIC: MutableList<Type> =
            mutableListOf(Type.PSYCHIC, Type.STEEL)
        val IMMUNE_TO_PSYCHIC: MutableList<Type> = mutableListOf(Type.DARK)
        val STRONG_AGAINST_BUG: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.FLYING, Type.ROCK)
        val RESISTANT_TO_BUG: MutableList<Type> = mutableListOf(
            Type.FIRE, Type.FIGHTING,
            Type.POISON, Type.FLYING, Type.GHOST, Type.STEEL
        )
        val STRONG_AGAINST_DRAGON: MutableList<Type> = mutableListOf(Type.ICE, Type.DRAGON)
        val RESISTANT_TO_DRAGON: MutableList<Type> = mutableListOf(Type.STEEL)
        val STRONG_AGAINST_ELECTRIC: MutableList<Type> = mutableListOf(Type.GROUND)
        val RESISTANT_TO_ELECTRIC: MutableList<Type> =
            mutableListOf(Type.ELECTRIC, Type.GRASS, Type.DRAGON)
        val IMMUNE_TO_ELECTRIC: MutableList<Type> = mutableListOf(Type.GROUND)
        val STRONG_AGAINST_GHOST: MutableList<Type> = mutableListOf(Type.GHOST, Type.DARK)
        val RESISTANT_TO_GHOST: MutableList<Type> = mutableListOf(Type.DARK)
        val IMMUNE_TO_GHOST: MutableList<Type> = mutableListOf(Type.NORMAL)
        val STRONG_AGAINST_POISON: MutableList<Type> =
            mutableListOf(Type.GROUND, Type.PSYCHIC)
        val RESISTANT_TO_POISON: MutableList<Type> =
            mutableListOf(Type.POISON, Type.GROUND, Type.ROCK, Type.GHOST)
        val IMMUNE_TO_POISON: MutableList<Type> = mutableListOf(Type.STEEL)
        val STRONG_AGAINST_ICE: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.FIGHTING, Type.ROCK, Type.STEEL)
        val RESISTANT_TO_ICE: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.WATER, Type.ICE, Type.STEEL)
        val STRONG_AGAINST_STEEL: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.FIGHTING, Type.GROUND)
        val RESISTANT_TO_STEEL: MutableList<Type> =
            mutableListOf(Type.FIRE, Type.WATER, Type.ELECTRIC, Type.STEEL)
        val STRONG_AGAINST_DARK: MutableList<Type> = mutableListOf(Type.FIGHTING, Type.BUG)
        val RESISTANT_TO_DARK: MutableList<Type> = mutableListOf(Type.FIGHTING, Type.DARK)
    }
}