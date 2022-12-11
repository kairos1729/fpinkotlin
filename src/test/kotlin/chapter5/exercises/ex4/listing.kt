package chapter5.exercises.ex4

import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
fun <A> Stream<A>.forAll(p: (A) -> Boolean): Boolean =
    when (this) {
        is Cons -> if (!p(this.head())) false else this.tail().forAll(p)
        is Empty -> true
    }

fun <A> Stream<A>.forAllFoldR(p: (A) -> Boolean): Boolean =
    foldRight({ true }) { a, b -> p(a) && b() }

//end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise4 : WordSpec({

    "Stream.forAll" should {
        "ensure that all elements match the predicate" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.forAll { it < 6 } shouldBe true
        }
        "stop evaluating if one element does not satisfy the predicate" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.forAll { it != 3 } shouldBe false
        }
    }

    "Stream.forAllFoldR" should {
        "ensure that all elements match the predicate" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.forAllFoldR { it < 6 } shouldBe true
        }
        "stop evaluating if one element does not satisfy the predicate" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.forAllFoldR { it != 3 } shouldBe false
        }
    }
})
