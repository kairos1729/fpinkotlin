package chapter5.exercises.ex14

import chapter5.Stream
import chapter5.exercises.ex13.takeWhile
import chapter5.exercises.ex13.zipAll
import chapter5.solutions.ex4.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise14 : WordSpec({

    //tag::startswith[]
    fun <A> Stream<A>.startsWith(that: Stream<A>): Boolean =
        this.zipAll(that)
            .takeWhile { !it.second.isEmpty() }
            .forAll { it.first == it.second }

    //end::startswith[]

    "Stream.startsWith" should {
        "detect if one stream is a prefix of another" {
            Stream.of(1, 2, 3).startsWith(
                Stream.of(1, 2)
            ) shouldBe true
        }
        "detect if one stream is not a prefix of another" {
            Stream.of(1, 2, 3).startsWith(
                Stream.of(2, 3)
            ) shouldBe false
        }
        "detect if one stream is not a prefix of another if prefix is longer than stream" {
            Stream.of(1, 2).startsWith(
                Stream.of(1, 2, 3)
            ) shouldBe false
        }
    }
})
