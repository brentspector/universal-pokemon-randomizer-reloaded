package viewModels

interface ModViewModel<T> {
    fun getState(): T
    fun setState(state: T)
    fun getEnumValues(): Iterable<T>
    fun isSelected(state: T): Boolean
}
