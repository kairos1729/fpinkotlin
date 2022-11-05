package chapter4.exercises.ex5

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

//TODO: Enable tests by removing `!` prefix
class Exercise5 : WordSpec({

    //tag::traverse[]
    fun <A, B> traverse(
        xa: List<A>,
        f: (A) -> Option<B>
    ): Option<List<B>> =
        xa.foldRight<A, Option<List<B>>>(
            Some(Nil)
        ) { a, ob ->
            map2(f(a), ob) { b, xb -> Cons(b, xb) }
        }

    //end::traverse[]

    fun <A> sequence(xs: List<Option<A>>): Option<List<A>> =
        traverse(xs) { it }

    fun <A> catches(a: () -> A): Option<A> =
        try {
            Some(a())
        } catch (e: Throwable) {
            None
        }

    // using nullables
    fun <A, B> traverse_null(xa: List<A>, f: (A) -> B?): List<B>? =
        xa.foldRight<A, List<B>?>(
            Nil
        ) { a, xbn ->
            map2_null(f(a), xbn) { b, xb -> Cons(b, xb) }
        }

    fun <A> sequence_null(xs: List<A?>): List<A>? =
        traverse_null(xs) { it }

    fun <A> catches_null(a: () -> A): A? =
        try {
            a()
        } catch (e: Throwable) {
            null
        }

    "traverse" should {
        """return some option of a transformed list if all
            transformations succeed""" {
            val xa = List.of(1, 2, 3, 4, 5)
            traverse(xa) { a: Int ->
                catches { a.toString() }
            } shouldBe Some(
                List.of("1", "2", "3", "4", "5")
            )
        }

        "return a none option if any transformations fail" {
            val xa = List.of("1", "2", "x", "4")
            traverse(xa) { a ->
                catches { a.toInt() }
            } shouldBe None
        }
    }

    "sequence" should {
        "turn a list of some options into an option of list" {
            val lo =
                List.of(Some(10), Some(20), Some(30))
            sequence(lo) shouldBe Some(List.of(10, 20, 30))
        }

        "turn a list of options containing a none into a none" {
            val lo =
                List.of(Some(10), None, Some(30))
            sequence(lo) shouldBe None
        }
    }

    "traverse_null" should {
        """return some option of a transformed list if all
            transformations succeed""" {
            val xa = List.of(1, 2, 3, 4, 5)
            traverse_null(xa) { a: Int ->
                catches_null { a.toString() }
            } shouldBe List.of("1", "2", "3", "4", "5")
        }

        "return a none option if any transformations fail" {
            val xa = List.of("1", "2", "x", "4")
            traverse_null(xa) { a ->
                catches_null { a.toInt() }
            } shouldBe null
        }
    }

    "sequence_null" should {
        "turn a list of some options into an option of list" {
            val lo =
                List.of(10, 20, 30)
            sequence_null(lo) shouldBe List.of(10, 20, 30)
        }

        "turn a list of options containing a none into a none" {
            val lo =
                List.of(10, null, 30)
            sequence_null(lo) shouldBe null
        }
    }
})
