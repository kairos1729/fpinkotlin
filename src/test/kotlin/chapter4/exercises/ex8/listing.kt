package chapter4.exercises.ex7

import chapter3.Cons
import chapter3.List
import chapter3.Nil
import chapter3.append
import chapter4.Either
import chapter4.exercises.ex6.flatMap
import chapter4.exercises.ex6.map
import chapter4.foldRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

sealed class Eithers<out E, out A>

data class Lefts<out E>(val values: List<E>) : Eithers<E, Nothing>()

data class Rights<out A>(val value: A) : Eithers<Nothing, A>()
//end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise8 : WordSpec({

    fun <E, A, B> Eithers<E, A>.map(f: (A) -> B): Eithers<E, B> =
        when (this) {
            is Lefts -> this
            is Rights -> Rights(f(value))
        }

    fun <E, A, B> Eithers<E, A>.flatMap(f: (A) -> Eithers<E, B>): Eithers<E, B> =
        when (this) {
            is Lefts -> this
            is Rights -> f(value)
        }

    fun <E, A> Eithers<E, A>.orElse(f: () -> Eithers<E, A>): Eithers<E, A> =
        when (this) {
            is Lefts -> f()
            is Rights -> this
        }

    fun <E, A, B, C> map2(
        ae: Either<E, A>,
        be: Either<E, B>,
        f: (A, B) -> C
    ): Either<E, C> =
        ae.flatMap { a -> be.map { b -> f(a, b) } }

    fun <E, A, B, C> map2s(
        ae: Eithers<E, A>,
        be: Eithers<E, B>,
        f: (A, B) -> C
    ): Eithers<E, C> =
        when (ae) {
            is Rights ->
                when (be) {
                    is Rights -> Rights(f(ae.value, be.value))
                    is Lefts -> be
                }
            is Lefts ->
                when (be) {
                    is Rights -> ae
                    is Lefts -> Lefts(append( ae.values, be.values))
                }
        }


    //tag::init[]
    fun <E, A, B> traverse(
        xs: List<A>,
        f: (A) -> Eithers<E, B>
    ): Eithers<E, List<B>> =
        xs.foldRight<A, Eithers<E, List<B>>>(
            Rights(Nil)
        ) { a, ebs ->
            map2s(f(a), ebs) { b, bs -> Cons(b, bs) }
        }

    fun <E, A> sequence(es: List<Eithers<E, A>>): Eithers<E, List<A>> =
        traverse(es) { it }
    //end::init[]

    fun <A> catches(a: () -> A): Eithers<String, A> =
        try {
            Rights(a())
        } catch (e: Throwable) {
            Lefts(Cons(e.message.orEmpty(), Nil))
        }

    "traverse" should {
        """return a right either of a transformed list if all
            transformations succeed""" {
            val xa = List.of("1", "2", "3", "4", "5")

            traverse(xa) { a ->
                catches { Integer.parseInt(a) }
            } shouldBe Rights(List.of(1, 2, 3, 4, 5))
        }

        "return a left either if any transformations fail" {
            val xa = List.of("1", "2", "x", "y", "5")

            traverse(xa) { a ->
                catches { Integer.parseInt(a) }
            } shouldBe Lefts(
                Cons("""For input string: "x"""", Cons("""For input string: "y"""", Nil))
            )
        }
    }
    "sequence" should {
        "turn a list of right eithers into a right either of list" {
            val xe: List<Eithers<String, Int>> =
                List.of(Rights(1), Rights(2), Rights(3))

            sequence(xe) shouldBe Rights(List.of(1, 2, 3))
        }

        """convert a list containing any left eithers into a
            left either""" {
            val xe: List<Eithers<String, Int>> =
                List.of(Rights(1), Lefts(Cons("boom", Nil)), Lefts(Cons("gate", Nil)), Rights(3))

            sequence(xe) shouldBe Lefts(Cons("boom", Cons("gate", Nil)))
        }
    }
})
