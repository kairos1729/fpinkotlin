package chapter2.exercises.ex1

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlinx.collections.immutable.persistentMapOf
import kotlin.system.measureTimeMillis

//TODO: Enable tests by removing `!` prefix
class Exercise1 : WordSpec({
    //tag::init[]
    fun fib(i: Int): Int {
        tailrec fun go(n: Int, fn: Int, fn1: Int): Int =
            when {
                n >= i -> fn
                else -> go(n + 1, fn1, fn + fn1)
            }
        return go(0, 0, 1)
    }

    fun fib_non_tail_recursive(i: Int): Int =
        when (i) {
            0 -> 0
            1 -> 1
            else ->
                fib_non_tail_recursive(i - 1) +
                    fib_non_tail_recursive(i - 2)
        }

    fun verifyFib(name: String, functionToTest: (Int) -> Int) {
        val millis = measureTimeMillis {
            persistentMapOf(
                1 to 1,
                2 to 1,
                3 to 2,
                4 to 3,
                5 to 5,
                6 to 8,
                7 to 13,
                8 to 21,
                40 to 102334155
            ).forEach { (n, num) ->
                functionToTest(n) shouldBe num
            }
        }
        println("$name: $millis ms")
    }

    //end::init[]

    "fib" should {
        "return the nth fibonacci number" {
            verifyFib("fib", ::fib)
        }
    }
    "fib_non_tail_recursive" should {
        "return the nth fibonacci number" {
            verifyFib("fib_non_tail_recursive", ::fib_non_tail_recursive)
        }
    }
})

