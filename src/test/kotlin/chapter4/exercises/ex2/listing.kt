package chapter4.exercises.ex2

import chapter3.List
import chapter4.None
import chapter4.Option
import chapter4.Some
import chapter4.exercises.ex1.getOrElse_null
import chapter4.getOrElse
import chapter4.isEmpty
import chapter4.map
import chapter4.size
import chapter4.sum
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlin.math.pow

fun mean(xs: List<Double>): Option<Double> =
    if (xs.isEmpty()) None
    else Some(xs.sum() / xs.size())

//tag::init[]
fun variance(xs: List<Double>): Option<Double> =
    mean(xs).flatMap { m ->
        mean(xs.map { x -> (x - m).pow(2) })
    }

// With nullables
fun mean_null(xs: List<Double>): Double? =
    if (xs.isEmpty()) null else xs.sum() / xs.size()

fun variance_null(xs: List<Double>): Double? =
    mean_null(xs)?.let { mean_null(xs.map { x -> (x - it).pow(2) }) }

fun variance_null2(xs: List<Double>): Double? =
    mean_null(xs)?.let { m ->
        mean_null(xs.map { x -> (x - m).pow(2) })
    }


//end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise2 : WordSpec({

    "variance" should {
        "determine the variance of a list of numbers" {
            val ls =
                List.of(1.0, 1.1, 1.0, 3.0, 0.9, 0.4)
            variance(ls).getOrElse { 0.0 } shouldBe
                (0.675).plusOrMinus(0.005)
        }
    }

    "variance_null" should {
        "determine the variance of a list of numbers" {
            val ls =
                List.of(1.0, 1.1, 1.0, 3.0, 0.9, 0.4)
            variance_null(ls).getOrElse_null { 0.0 } shouldBe
                (0.675).plusOrMinus(0.005)
        }
    }

    "variance_null2" should {
        "determine the variance of a list of numbers" {
            val ls =
                List.of(1.0, 1.1, 1.0, 3.0, 0.9, 0.4)
            variance_null2(ls).getOrElse_null { 0.0 } shouldBe
                (0.675).plusOrMinus(0.005)
        }
    }
})
