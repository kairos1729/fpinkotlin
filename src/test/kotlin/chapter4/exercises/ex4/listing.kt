package chapter4.exercises.ex4

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import chapter4.None
import chapter4.Option
import chapter4.Some
import chapter4.exercises.ex3.map2_null
import chapter4.foldRight
import chapter4.map2
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
fun <A> sequence1(xs: List<Option<A>>): Option<List<A>> {
    fun loop(xs: List<Option<A>>, acc: List<A>): Option<List<A>> =
        when (xs) {
            is Nil -> Some(acc.reverse())
            is Cons -> xs.head.flatMap { h -> loop(xs.tail, Cons(h, acc)) }
        }
    return loop(xs, Nil)
}

fun <A> sequence(xs: List<Option<A>>): Option<List<A>> =
    xs.foldRight<Option<A>, Option<List<A>>>(
        Some(Nil)
    ) { oa, ob -> map2(oa, ob) { a, b -> Cons(a, b) } }

fun <A> sequence_null(xs: List<A?>): List<A>? =
    xs.foldRight<A?, List<A>?>(
        Nil
    ) { an, bn -> map2_null(an, bn) { a, b -> Cons(a, b) } }

//end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise4 : WordSpec({

    "sequence" should {
        "turn a list of some options into an option of list" {
            val lo =
                List.of(Some(10), Some(20), Some(30))
            sequence(lo) shouldBe Some(List.of(10, 20, 30))
        }
        "turn a list of options containing none into a none" {
            val lo =
                List.of(Some(10), None, Some(30))
            sequence(lo) shouldBe None
        }
    }

    "sequence_null" should {
        "turn a list of some options into an option of list" {
            val lo =
                List.of(10, 20, 30)
            sequence_null(lo) shouldBe List.of(10, 20, 30)
        }
        "turn a list of options containing none into a none" {
            val lo = List.of(10, null, 30)
            sequence_null(lo) shouldBe null
        }
    }
})
