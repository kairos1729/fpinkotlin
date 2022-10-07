package chapter3.exercises.ex9

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

// tag::init[]
tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
    when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
    }

// end::init[]

fun sum(l: List<Int>): Int {
    tailrec fun loop(xs: List<Int>, acc: Int): Int =
        when (xs) {
            is Nil -> acc
            is Cons -> loop(xs.tail, acc + xs.head)
        }
    return loop(l, 0)
}

fun product(l: List<Int>): Int {
    tailrec fun loop(xs: List<Int>, acc: Int): Int =
        when (xs) {
            is Nil -> acc
            is Cons -> loop(xs.tail, acc * xs.head)
        }
    return loop(l, 1)
}

//TODO: Enable tests by removing `!` prefix
class Exercise9 : WordSpec({
    "list foldLeft" should {
        """apply a function f providing a zero accumulator from tail
            recursive position""" {
            foldLeft(
                List.of(1, 2, 3, 4, 5),
                0,
                { x, y -> x + y }) shouldBe 15
        }
    }
    "sum" should {
        "add" {
            sum(List.of(1, 2, 3, 4)) shouldBe 10
        }
        "add empty" {
            sum(Nil) shouldBe 0
        }
    }
    "product" should {
        "multiply" {
            product(List.of(1, 2, 3, 4)) shouldBe 24
        }
        "multiply empty" {
            product(Nil) shouldBe 1
        }
    }
})
