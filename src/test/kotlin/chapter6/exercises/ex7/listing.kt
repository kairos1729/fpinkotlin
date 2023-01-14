package chapter6.exercises.ex7

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import chapter3.foldRight
import chapter6.RNG
import chapter6.Rand
import chapter6.rng1
import chapter6.solutions.ex6.map2
import chapter6.unit
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise7 : WordSpec({

    //tag::init[]
    fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
        when (fs) {
            is Cons -> map2(fs.head, sequence(fs.tail)) { a, bs ->
                Cons(a, bs)
            }

            is Nil -> unit(Nil)
        }
    //end::init[]

    //tag::init2[]
    fun <A> sequence2(fs: List<Rand<A>>): Rand<List<A>> =
        foldRight(fs, unit<List<A>>(Nil)) { ra, rl ->
            map2(ra, rl) { a, l ->
                Cons(a, l)
            }
        }

    //end::init2[]
    val intR: Rand<Int> = { rng -> rng.nextInt() }

    fun <A> repeatCount(count: Int, item: A): List<A> =
        when (count) {
            0 -> Nil
            else -> Cons(item, repeatCount(count - 1, item))
        }

    fun ints2(count: Int, rng: RNG): Pair<List<Int>, RNG> =
        sequence(repeatCount(count, intR))(rng)

    data class SimpleRNG(val seed: Long) : RNG {
        override fun nextInt(): Pair<Int, RNG> {
            val newSeed =
                (seed * 0x5DEECE66DL + 0xBL) and
                    0xFFFFFFFFFFFFL // <1>
            val nextRNG = SimpleRNG(newSeed) // <2>
            val n = (newSeed ushr 16).toInt() // <3>
            return n to nextRNG // <4>
        }
    }

    "sequence" should {

        "combine the results of many actions using recursion" {

            val combined: Rand<List<Int>> =
                sequence(
                    List.of(
                        unit(1),
                        unit(2),
                        unit(3),
                        unit(4)
                    )
                )

            combined(rng1).first shouldBe
                List.of(1, 2, 3, 4)
        }

        """combine the results of many actions using
            foldRight and map2""" {

            val combined2: Rand<List<Int>> =
                sequence2(
                    List.of(
                        unit(1),
                        unit(2),
                        unit(3),
                        unit(4)
                    )
                )

            combined2(rng1).first shouldBe
                List.of(1, 2, 3, 4)
        }
    }

    "ints" should {
        "generate a list of ints of a specified length" {
            ints2(4, rng1).first shouldBe
                List.of(1, 1, 1, 1)
        }

        "generate a list of random ints of a specified length" {

            ints2(5, SimpleRNG(1L)) shouldBe (List.of(
                384748,
                -1151252339,
                -549383847,
                1612966641,
                -883454042
            ) to SimpleRNG(223576932655868))
        }
    }
})
