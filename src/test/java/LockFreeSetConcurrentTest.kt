import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.junit.jupiter.api.Test

@Param(name = "value", gen = IntGen::class)
class LockFreeSetConcurrentTest {
    private val mySet = LockFreeSetImpl<Int>();

    @Operation
    fun add(@Param(name = "value") value: Int): Boolean {
        return mySet.add(value)
    }

    @Operation
    fun remove(@Param(name = "value") value: Int): Boolean {
        return mySet.remove(value)
    }

    @Operation
    fun contains(@Param(name = "value") value: Int): Boolean {
        return mySet.contains(value)
    }

    @Operation
    fun empty(): Boolean {
        return mySet.empty()
    }

    @Test
    fun concurrentTest() = ModelCheckingOptions().check(this::class)
}