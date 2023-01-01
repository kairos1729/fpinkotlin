package chapter6.exercises.ex4

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import chapter4.None
import chapter4.Some
import chapter4.map
import chapter5.exercises.ex11.unfold
import chapter5.toList
import chapter6.RNG
import chapter6.rng1
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise4 : WordSpec({

    //tag::init[]
    fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> {
        tailrec fun go(
            count: Int,
            p: Pair<List<Int>, RNG>
        ): Pair<List<Int>, RNG> =
            when (count) {
                0 -> p
                else -> {
                    val (l, rng1) = p
                    val (i, rng2) = rng1.nextInt()
                    go(count - 1, Cons(i, l) to rng2)
                }
            }
        return go(count, List.empty<Int>() to rng)
    }

    fun intsUnfold(count: Int, rng: RNG): Pair<List<Int>, RNG> {
        val x = unfold(
            count to rng
        ) { (c, r) ->
            if (c == 0)
                None
            else {
                val (i, rn) = r.nextInt()
                Some((i to rn) to ((c - 1) to rn))
            }
        }.toList().reverse()


        return when (x) {
            is Nil -> Nil to rng
            is Cons -> {
                val boo = x.map { it.first } to x.head.second
                boo
            }
        }
    }

    //end::init[]
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

    "ints" should {
        "generate a list of ints of a specified length" {

            ints(5, rng1) shouldBe (List.of(1, 1, 1, 1, 1) to rng1)
        }

        "generate a list of random ints of a specified length" {

            ints(5, SimpleRNG(1L)) shouldBe (List.of(
                -883454042,
                1612966641,
                -549383847,
                -1151252339,
                384748
            ) to SimpleRNG(223576932655868))
        }
    }

    "intsUnfold" should {
        "generate a list of ints of a specified length" {

            intsUnfold(5, rng1) shouldBe (List.of(1, 1, 1, 1, 1) to rng1)
        }

        "generate a list of random ints of a specified length" {

            intsUnfold(5, SimpleRNG(1L)) shouldBe (List.of(
                -883454042,
                1612966641,
                -549383847,
                -1151252339,
                384748
            ) to SimpleRNG(223576932655868))
        }
    }
})
