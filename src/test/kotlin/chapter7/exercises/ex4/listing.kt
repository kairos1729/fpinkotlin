package chapter7.exercises.ex4

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

typealias Par<A> = (ExecutorService) -> Future<A>

class UnitFuture<A>(val a: A): Future<A> {
    override fun cancel(mayInterruptIfRunning: Boolean) = false

    override fun isCancelled() = true

    override fun isDone() = true

    override fun get(): A = a

    override fun get(timeout: Long, unit: TimeUnit): A = a
}

object Pars {

    //tag::init[]
    fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> = { lazyUnit { f(it) } }
    //end::init[]

    fun <A> unit(a: () -> A): Par<A> =
        { _: ExecutorService -> UnitFuture(a()) }

    fun <A> fork(
        a: () -> Par<A>
    ): Par<A> =
        { es: ExecutorService ->
            es.submit(Callable<A> { a()(es).get() })
        }

    fun <A> lazyUnit(a: () -> A): Par<A> =
        fork { unit { a() } }

    fun <A> run(a: Par<A>): A = a(Executors.newCachedThreadPool()).get()
}
