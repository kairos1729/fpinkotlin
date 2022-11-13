package chapter5.exercises.ex1

import chapter3.List
import chapter3.Nil
import chapter5.Cons
import chapter5.Empty
import chapter5.Stream
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import chapter3.Cons as ConsL

//TODO: Enable tests by removing `!` prefix
class Exercise1 : WordSpec({
    //tag::init[]
    fun <A> Stream<A>.toListNonTail(): List<A> =
        when (this) {
            is Empty -> Nil
            is Cons -> ConsL(head(), tail().toListNonTail())
        }

    tailrec fun <A, B> Stream<A>.foldLeft(
        z: () -> B,
        f: (() -> B, () -> A) -> () -> B
    ): B =
        when (this) {
            is Empty -> z()
            is Cons -> tail().foldLeft(
                f(z, head), f
            )
        }

    fun <A> Stream<A>.toList(): List<A> =
        this.foldLeft<A, List<A>>({ Nil }) { bs, h -> { ConsL(h(), bs()) } }
            .reverse()

    //end::init[]

    "Stream.toList" should {
        "force the stream into an evaluated list" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.toListNonTail() shouldBe List.of(1, 2, 3, 4, 5)
        }

        "force the stream into an evaluated list tail recursive" {
            val s = Stream.of(1, 2, 3, 4, 5)
            s.toList() shouldBe List.of(1, 2, 3, 4, 5)
        }
    }
})
