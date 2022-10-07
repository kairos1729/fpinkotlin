package chapter3.exercises.ex5

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

// tag::init[]
fun <A> init(l: List<A>): List<A> {
    tailrec fun loop(acc: List<A>, xs: Cons<A>): List<A> {
        return when (val tail = xs.tail) {
            is Nil -> acc.reverse()
            is Cons -> loop(Cons(xs.head, acc), tail)
        }
    }
    return when (l) {
        is Nil -> throw IllegalStateException()
        is Cons -> loop(Nil, l)
    }
}

fun <A> init_non_tail_recursive(l: List<A>): List<A> =
    when (l) {
        is Cons ->
            if (l.tail == Nil) Nil
            else Cons(l.head, init_non_tail_recursive(l.tail))
        is Nil ->
            throw IllegalStateException("Cannot init Nil list")
    }


// Avoid stack overflows when making the Cons list
fun <A> makeList(aa: kotlin.collections.List<A>): List<A> {
    var result: List<A> = Nil
    for (i in aa.size - 1 downTo 0) {
        result = Cons(aa[i], result)
    }
    return result
}
// end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise5 : WordSpec({

    "list init" should {
        "return all but the last element" {
            init(List.of(1, 2, 3, 4, 5)) shouldBe
                List.of(1, 2, 3, 4)
        }

        "return Nil if only one element exists" {
            init(List.of(1)) shouldBe Nil
        }

        "should not throw a stack overflow error" {
            shouldNotThrow<StackOverflowError> {
                init(makeList((1..100000).toList()))
            }
        }

        "non-tail-recursive method will cause a stack overflow" {
            shouldThrow<StackOverflowError> {
                init_non_tail_recursive(makeList((1..100000).toList()))
            }
        }

        "throw an exception if no elements exist" {
            shouldThrow<IllegalStateException> {
                init(List.empty<Int>())
            }
        }
    }
})
