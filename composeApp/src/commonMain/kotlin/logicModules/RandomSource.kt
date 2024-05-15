package logicModules

import org.jetbrains.skiko.currentNanoTime
import kotlin.random.Random

object RandomSource {
    private var source: Random = Random.Default
    private var seed: Long = 0
    private var calls = 0

    fun reset() {
        source = Random.Default
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

    fun pickSeed(): Long {
        var value: Long = 0
        val by = currentNanoTime().toString().encodeToByteArray()
        for (i in by.indices) {
            value = value or (by[i].toLong() and 0xffL shl 8 * i)
        }
        return value
    }

    fun callsSinceSeed(): Int {
        return calls
    }

    fun getSeed(): Long {
        return seed
    }

}