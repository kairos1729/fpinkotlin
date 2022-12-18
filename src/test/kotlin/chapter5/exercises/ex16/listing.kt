package chapter5.exercises.ex16

import chapter3.List
import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import chapter5.toList
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

//TODO: Enable tests by removing `!` prefix
class Exercise16 : WordSpec({

    //tag::scanright[]
    // This differes from the real solution. I guess I haven't made my solution efficient...
    fun <A, B> Stream<A>.scanRight(z: B, f: (A, () -> B) -> B): Stream<B> =
        when (this) {
            is Cons -> Stream.cons({
                this.foldRight({ z }) { a, b ->
                    f(a, b)
                }
            }, { this.tail().scanRight(z, f) })

            is Empty -> Stream.cons({ z }, { Stream.empty() })
        }

    //end::scanright[]

    "Stream.scanRight" should {
        "behave like foldRight" {
            Stream.of(1, 2, 3)
                .scanRight(0, { a, b ->
                    a + b()
                }).toList() shouldBe List.of(6, 5, 3, 0)
        }
    }
})
