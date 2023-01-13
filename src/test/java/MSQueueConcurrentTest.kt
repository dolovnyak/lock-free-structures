import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

@Param(name = "value", gen = IntGen::class)
class MSQueueConcurrentTest {
    private val msQueue = MichaelScottQueueImpl<Int>();

    @Operation
    fun push(@Param(name = "value") value: Int) {
        return msQueue.push(value)
    }

    @Operation
    fun pop(): Int? {
        return msQueue.pop()
    }

    @Operation
    fun empty(): Boolean {
        return msQueue.empty()
    }

    @Test
    fun concurrentTest() = ModelCheckingOptions().check(this::class)

//    @Test
//    fun concurrentTest() {
//        val setUp = StressOptions()
//            .iterations(1000).threads(3)
//            .invocationsPerIteration(1)
//            .logLevel(LoggingLevel.INFO)
//        LinChecker.check(MSQueueConcurrentTest::class.java, setUp)
//    }
}