package chapter6.exercises.ex3

// import chapter6.solutions.ex2.double
// import chapter6.solutions.ex5.doubleR
import chapter6.RNG
import chapter6.solutions.ex2.double
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise3 : WordSpec({

    //tag::init[]
    fun intDouble(rng: RNG): Pair<Pair<Int, Double>, RNG> {
        val (i, r1) = rng.nextInt()
        val (d, r2) = double(r1)
        return (i to d) to r2
    }


    fun doubleInt(rng: RNG): Pair<Pair<Double, Int>, RNG> {
        val (id, r1) = intDouble(rng)
        val (i, d) = id
        return (d to i) to r1
    }

    fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
        val (n1, r1) = double(rng)
        val (n2, r2) = double(r1)
        val (n3, r3) = double(r2)
        return (Triple(n1, n2, n3)) to r3
    }
    //end::init[]

    "intDouble" should {

        val doubleBelowOne =
            Int.MAX_VALUE.toDouble() / (Int.MAX_VALUE.toDouble() + 1)

        val unusedRng = object : RNG {
            override fun nextInt(): Pair<Int, RNG> = TODO()
        }

        val rng3 = object : RNG {
            override fun nextInt(): Pair<Int, RNG> =
                Int.MAX_VALUE to unusedRng
        }

        val rng2 = object : RNG {
            override fun nextInt(): Pair<Int, RNG> =
                Int.MAX_VALUE to rng3
        }

        val rng = object : RNG {
            override fun nextInt(): Pair<Int, RNG> =
                Int.MAX_VALUE to rng2
        }

        "generate a pair of int and double" {
            val (id, _) = intDouble(rng)
            val (i, d) = id
            i shouldBe Int.MAX_VALUE
            d shouldBe doubleBelowOne
        }

        "generate a pair of double and int" {
            val (di, _) = doubleInt(rng)
            val (d, i) = di
            d shouldBe doubleBelowOne
            i shouldBe Int.MAX_VALUE
        }

        "generate a triple of double, double, double" {
            val (ddd, _) = double3(rng)
            val (d1, d2, d3) = ddd
            d1 shouldBe doubleBelowOne
            d2 shouldBe doubleBelowOne
            d3 shouldBe doubleBelowOne
        }
    }
})
