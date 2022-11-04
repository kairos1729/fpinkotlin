package chapter4.exercises.ex3

import chapter4.None
import chapter4.Option
import chapter4.Some
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//tag::init[]
fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A, B) -> C): Option<C> =
    a.flatMap { sa -> b.map { sb -> f(sa, sb) } }

fun <A, B, C> map2_null(an: A?, bn: B?, f: (A, B) -> C): C? =
    an?.let { a -> bn?.let { b -> f(a, b) } }
//end::init[]

//TODO: Enable tests by removing `!` prefix
class Exercise3 : WordSpec({

    "map2" should {

        val a = Some(5)
        val b = Some(20)
        val none = Option.empty<Int>()
        val an = 5
        val bn = 20
        val nulld: Int? = null

        "combine two option values using a binary function" {
            map2(a, b) { aa, bb ->
                aa * bb
            } shouldBe Some(100)
        }

        "return none if either option is not defined" {
            map2(a, none) { aa, bb ->
                aa * bb
            } shouldBe None

            map2(none, b) { aa, bb ->
                aa * bb
            } shouldBe None
        }

        "combine two nullable values using a binary function" {
            map2_null(an, bn) { aa, bb ->
                aa * bb
            } shouldBe 100
        }

        "return null if either nullable is null" {
            map2_null(an, nulld) { aa, bb ->
                aa * bb
            } shouldBe null

            map2_null(nulld, bn) { aa, bb ->
                aa * bb
            } shouldBe null
        }
    }
})
