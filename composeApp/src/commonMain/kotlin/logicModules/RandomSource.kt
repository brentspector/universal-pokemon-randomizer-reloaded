package logicModules

import kotlin.random.Random

object RandomSource {
    private var source: Random = Random.Default
    private var seed: Long = 0
    private var calls = 0

    fun reset() {
        source = Random(pickSeed())
        calls = 0
    }

    fun seed(seedValue: Long) {
        source = Random(seedValue)
        seed = seedValue
        calls = 0
    }

    fun random(): Double {
        calls++
        return source.nextDouble()
    }

    fun nextInt(size: Int): Int {
        calls++
        return source.nextInt(size)
    }

    fun nextBytes(bytes: ByteArray) {
        calls++
        source.nextBytes(bytes)
    }

    fun nextInt(): Int {
        calls++
        return source.nextInt()
    }

    fun nextLong(): Long {
        calls++
        return source.nextLong()
    }

    fun nextBoolean(): Boolean {
        calls++
        return source.nextBoolean()
    }

    fun nextFloat(): Float {
        calls++
        return source.nextFloat()
    }

    fun nextDouble(): Double {
        calls++
        return source.nextDouble()
    }

    fun <T> shuffleList(list: MutableList<T>) {
        calls++
        list.shuffle(source)
    }

    fun <T> randomOfList(list: MutableList<T>): T {
        calls++
        return list.random(source)
    }

    fun pickSeed(): Long {
        return Random.Default.nextLong()
    }

    fun callsSinceSeed(): Int {
        return calls
    }

    fun getSeed(): Long {
        return seed
    }

}