package chapter3.exercises.ex14

import chapter3.List
import chapter3.Nil
import chapter3.exercises.ex13.append
import chapter3.foldLeft
import chapter3.foldRight
import chapter3.reverse
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
fun <A> concat(lla: List<List<A>>): List<A> =
    foldRight(lla, Nil, { a: List<A>, acc: List<A> -> append(a, acc) })

fun <A> concat2(lla: List<List<A>>): List<A> =
    foldLeft(reverse(lla), Nil, { acc: List<A>, a: List<A> -> append(a, acc) })

// end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise14 : WordSpec({
    "list concat" should {
        "concatenate a list of lists into a single list" {
            concat(
                List.of(
                    List.of(1, 2, 3),
                    List.of(4, 5, 6)
                )
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)

            concat2(
                List.of(
                    List.of(1, 2, 3),
                    List.of(4, 5, 6)
                )
            ) shouldBe List.of(1, 2, 3, 4, 5, 6)
        }
    }
})
