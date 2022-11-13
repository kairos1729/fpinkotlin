package chapter5.exercises.ex2

import chapter3.List
import chapter3.Nil
import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import chapter5.toList
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise2 : WordSpec({

    //tag::take[]

    // Do we need to make this tail recursive, as it's a lazy method?
    fun <A> Stream<A>.take(n: Int): Stream<A> = when (n) {
        0 -> Empty
        else -> when (this) {
            is Empty -> this
            is Cons -> Cons(head) { tail().take(n - 1) }
        }
    }
    //end::take[]

    //tag::drop[]
    tailrec fun <A> Stream<A>.drop(n: Int): Stream<A> = when (n) {
        0 -> this
        else -> when (this) {
            is Empty -> this
            is Cons -> tail().drop(n - 1)
        }
    }

    //end::drop[]

    "Stream.take(n)" should {
        "return the first n elements of a stream" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.take(3).toList() shouldBe List.of(1, 2, 3)
        }

        "return all the elements if the stream is exhausted" {
            val s = Stream.of(1, 2, 3)
            s.take(5).toList() shouldBe List.of(1, 2, 3)
        }

        "return an empty stream if the stream is empty" {
            val s = Stream.empty<Int>()
            s.take(3).toList() shouldBe Nil
        }
    }

    "Stream.drop(n)" should {
        "return the remaining elements of a stream" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.drop(3).toList() shouldBe List.of(4, 5)
        }

        "return empty if the stream is exhausted" {
            val s = Stream.of(1, 2, 3)
            s.drop(5).toList() shouldBe Nil
        }

        "return empty if the stream is empty" {
            val s = Stream.empty<Int>()
            s.drop(5).toList() shouldBe Nil
        }
    }
})
