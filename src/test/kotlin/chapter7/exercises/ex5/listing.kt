package chapter7.exercises.ex5

import chapter7.sec1.splitAt
import chapter7.solutions.ex3.TimedMap2Future
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

typealias Par<A> = (ExecutorService) -> Future<A>

object Pars {

    fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> =
        { a: A ->
            lazyUnit { f(a) }
        }

    fun <A> unit(a: A): Par<A> =
        { es: ExecutorService -> CompletableFuture.completedFuture(a) }

    fun <A> fork(
        a: () -> Par<A>
    ): Par<A> =
        { es: ExecutorService ->
            es.submit(Callable<A> { a()(es).get() })
        }

    fun <A> lazyUnit(a: () -> A): Par<A> =
        fork { unit(a()) }

    fun <A, B, C> map2(a: Par<A>, b: Par<B>, f: (A, B) -> C): Par<C> =
        { es: ExecutorService -> TimedMap2Future(a(es), b(es), f) }

    //tag::init1[]
    // Sequential
    fun <A> sequence1(ps: List<Par<A>>): Par<List<A>> =
        ps.foldRight(
            lazyUnit { emptyList<A>() }
        ) { pa, pb -> map2(pa, pb) { a, b -> listOf(a) + b } }

    // NLogN
    fun <A> sequence(ps: List<Par<A>>): Par<List<A>> =
        if (ps.size <= 1) {
            ps.firstOrNull()?.let {
                map2(it, unit(Unit)) { a, _ -> listOf(a)}
            } ?: unit(emptyList())
        } else {
            val (l, r) = ps.splitAt(ps.size / 2)
            map2(sequence(l), sequence(r)){ ls, rs -> ls + rs }
        }


//end::init1[]
}