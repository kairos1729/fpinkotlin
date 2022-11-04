package chapter4.exercises.ex1

import chapter4.None
import chapter4.Option
import chapter4.Some
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
    when (this) {
        is None -> None
        is Some -> Some(f(get))
    }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
        is None -> None
        is Some -> f(get)
    }

fun <A> Option<A>.getOrElse(default: () -> A): A =
    when (this) {
        is None -> default()
        is Some -> get
    }

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> =
    when (this) {
        is None -> ob()
        is Some -> this
    }

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> =
    when (this) {
        is None -> None
        is Some -> if (f(get)) this else None
    }

//end::init[]

//tag::alternate[]
fun <A, B> Option<A>.flatMap_2(f: (A) -> Option<B>): Option<B> =
    map(f).getOrElse { None }

fun <A> Option<A>.orElse_2(ob: () -> Option<A>): Option<A> =
    this.map { a -> Some(a) }.getOrElse { ob() }

fun <A> Option<A>.filter_2(f: (A) -> Boolean): Option<A> =
    flatMap { a -> if (f(a)) Some(a) else None }
//end::alternate[]

// What's so wrong about built-in optionals?
fun <A, B> A?.map_3(f: (A) -> B): B? = this?.let { f(it) }
fun <A, B> A?.flatMap_3(f: (A) -> B?) = map_3(f)
fun <A> A?.getOrElse_3(default: () -> A): A = this ?: default()
fun <A> A?.orElse_3(ob: () -> A?): A? = getOrElse_3(ob)
fun <A> A?.filter_3(f: (A) -> Boolean): A? =
    this?.let { if (f(it)) it else null }

//TODO: Enable tests by removing `!` prefix
class Exercise1 : WordSpec({

    val none = Option.empty<Int>()

    val some = Some(10)

    val someBuiltin: Int = 10
    val noneBuiltin: Int? = null

    "option map" should {
        "transform an option of some value" {
            some.map { it * 2 } shouldBe Some(20)
            someBuiltin.map_3 { it * 2 } shouldBe 20
        }
        "pass over an option of none" {
            none.map { it * 10 } shouldBe None
            noneBuiltin.map_3 { it * 10 } shouldBe null
        }
    }

    "option flatMap" should {
        """apply a function yielding an option to an
            option of some value""" {
            some.flatMap { a ->
                Some(a.toString())
            } shouldBe Some("10")

            some.flatMap_2 { a ->
                Some(a.toString())
            } shouldBe Some("10")

            someBuiltin.flatMap_3 { a ->
                a.toString()
            } shouldBe "10"
        }
        "pass over an option of none" {
            none.flatMap { a ->
                Some(a.toString())
            } shouldBe None

            none.flatMap_2 { a ->
                Some(a.toString())
            } shouldBe None

            noneBuiltin.flatMap_3 { a ->
                a.toString()
            } shouldBe null
        }
    }

    "option getOrElse" should {
        "extract the value of some option" {
            some.getOrElse { 0 } shouldBe 10
        }
        "extract the value of some nullable" {
            someBuiltin.getOrElse_3 { 0 } shouldBe 10
        }
        "return a default value if the option is none" {
            none.getOrElse { 10 } shouldBe 10
        }
        "return a default value if the nullable is null" {
            noneBuiltin.getOrElse_3 { 10 } shouldBe 10
        }
    }

    "option orElse" should {
        "return the option if the option is some" {
            some.orElse { Some(20) } shouldBe some
            some.orElse_2 { Some(20) } shouldBe some
            someBuiltin.orElse_3 { Some(20) } shouldBe someBuiltin
        }
        "return a default option if the option is none" {
            none.orElse { Some(20) } shouldBe Some(20)
            none.orElse_2 { Some(20) } shouldBe Some(20)
            noneBuiltin.orElse_3 { 20 } shouldBe 20
        }
    }

    "option filter" should {
        "return some option if the predicate is met" {
            some.filter { it > 0 } shouldBe some
            some.filter_2 { it > 0 } shouldBe some
            someBuiltin.filter_3 { it > 0 } shouldBe someBuiltin
        }
        "return a none option if the predicate is not met" {
            some.filter { it < 0 } shouldBe None
            some.filter_2 { it < 0 } shouldBe None
            someBuiltin.filter_3 { it < 0 } shouldBe null
        }
    }
})
