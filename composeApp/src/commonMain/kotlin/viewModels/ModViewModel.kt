package viewModels

interface ModViewModel<T> {
    fun getEnumValues(): Iterable<T>

    fun setState(state: T)
    fun isSelected(state: T): Boolean
}
