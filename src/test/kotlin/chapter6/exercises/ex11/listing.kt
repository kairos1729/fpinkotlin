package chapter6.exercises.ex11

// import arrow.core.Id
// import arrow.core.extensions.id.monad.monad
// import arrow.mtl.StateApi
// import arrow.mtl.extensions.fx
import arrow.core.Tuple2
import arrow.mtl.State
import arrow.mtl.run
import arrow.mtl.runS
import arrow.mtl.stateTraverse
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlinx.collections.immutable.persistentListOf

//tag::init1[]
sealed class Input

object Coin : Input()
object Turn : Input()

data class Machine(
    val locked: Boolean,
    val candies: Int,
    val coins: Int
)
//end::init1[]

//TODO: Enable tests by removing `!` prefix
class Exercise11 : WordSpec({

    // My go - not using comprehensions so it doesn't look very imperative
    fun performInput(m: Machine, i: Input): Machine =
        when (i) {
            is Coin -> if (m.locked && m.candies > 0) m.copy(
                locked = false,
                coins = m.coins + 1
            ) else m

            is Turn -> if (!m.locked) m.copy(
                locked = true,
                candies = m.candies - 1
            ) else m
        }

    fun traverseInputs(inputs: List<Input>): State<Machine, List<Tuple2<Int, Int>>> =
        inputs.stateTraverse { input: Input ->
            State { m: Machine ->
                val nm = performInput(m, input)
                Tuple2(nm, Tuple2(nm.coins, nm.candies))

            }
        }

    fun simulateMachine(inputs: List<Input>): State<Machine, Tuple2<Int, Int>> =
        State { m ->
            val (m2, ts) = traverseInputs(inputs).run(m)
            Tuple2(m2, ts.last())
        }

    // Solution uses a for comprehension (maybe read arrow docco first...)
    // Actually since arrow 1.0 there is no State monad, so the solution is
    // effectively out of date, don't bother to follow it.


    "simulateMachine" should {
        "allow the purchase of a single candy" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "allow the redemption of a single candy" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "allow purchase and redemption of a candy" {
            val actions = persistentListOf(Coin, Turn)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }

    "inserting a coin into a locked machine" should {
        "unlock the machine if there is some candy" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 1, coins = 0)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "inserting a coin into an unlocked machine" should {
        "do nothing" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after =
                Machine(locked = false, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "turning the knob on an unlocked machine" should {
        "cause it to dispense candy and lock" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = false, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 0, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "turning the knob on a locked machine" should {
        "do nothing" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = true, candies = 1, coins = 1)
            val after = Machine(locked = true, candies = 1, coins = 1)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
    "a machine that is out of candy" should {
        "ignore the turn of a knob" {
            val actions = persistentListOf(Turn)
            val before =
                Machine(locked = true, candies = 0, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 0)
            simulateMachine(actions).runS(before) shouldBe after
        }
        "ignore the insertion of a coin" {
            val actions = persistentListOf(Coin)
            val before =
                Machine(locked = true, candies = 0, coins = 0)
            val after = Machine(locked = true, candies = 0, coins = 0)
            simulateMachine(actions).runS(before) shouldBe after
        }
    }
})
