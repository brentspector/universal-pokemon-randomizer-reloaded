package pokemon

data class Evolution(var from: Pokemon, var to: Pokemon, var carryStats: Int,
                         var type: EvolutionType, var extraInfo: Int) {

    val typesDiffer = when {
        // 1 == 1
        from.primaryType != to.primaryType -> 1
        // 2 == 2
        from.secondaryType != to.secondaryType -> 2
        // 1 == 2
        from.primaryType == to.secondaryType && from.secondaryType != to.primaryType -> 3
        // 2 == 1
        from.secondaryType == to.primaryType && from.primaryType != to.secondaryType -> 4
        // 1 == 2 && 2 == 1 (Grass/Bug vs Bug/Grass)
        from.primaryType == to.secondaryType && from.secondaryType == to.primaryType -> 0
        else -> -0 // Default case, if none of the conditions match
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Evolution) return false
        return from === other.from && to === other.to && type === other.type
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
