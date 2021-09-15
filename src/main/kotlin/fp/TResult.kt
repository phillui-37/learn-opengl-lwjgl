package fp

sealed class TResult<T, E>: Iterable<TResult<T,E>> {
    abstract val isOk: Boolean
    val isErr
        get() = !isOk

    companion object {
        fun <T, E> pure(t: T): TResult<T, E> = TOk(t)

        inline fun <reified T, reified E: Exception> tryResult(getter: () -> T): TResult<T, E> {
            return try {
                TOk(getter())
            } catch (e: Exception) {
                if (e is E)
                    TErr(e)
                else
                    throw e
            }
        }

        inline fun <reified T, E> defaultOk(): TOk<T, E> = when (T::class.java) {
            String::class.java -> TOk("")
            Int::class.java -> TOk(0)
            Float::class.java -> TOk(0f)
            Double::class.java -> TOk(0.0)
            Unit::class.java -> TOk<Unit, E>(Unit)
            else -> throw NotImplementedError("This Type has no default ok value")
        } as TOk<T, E>
    }

    inline fun <U> fmap(fn: (T) -> U): TResult<U, E> = when (this) {
        is TOk -> TOk(fn(value))
        is TErr -> TErr(err)
    }
    inline fun <U> fmapOr(default: U, fn: (T) -> U): TResult<U, E> = TOk(when(this) {
        is TOk -> fn(value)
        is TErr -> default
    })
    inline fun <U> fmapOrElse(thenFn: (T) -> U, elseFn: (E) -> U): TResult<U, E> = TOk(when (this) {
        is TOk -> thenFn(value)
        is TErr -> elseFn(err)
    })
    inline fun <F> fmapErr(fn: (E) -> F): TResult<T, F> = when (this) {
        is TOk -> TOk(value)
        is TErr -> TErr(fn(err))
    }
    inline fun <U> flatMap(fn: (T) -> TResult<U, E>): TResult<U, E> = when (this) {
        is TOk -> fn(value)
        is TErr -> TErr(err)
    }

    infix fun <U> and(other: TResult<U, E>): TResult<U, E> = when (this) {
        is TOk -> other
        is TErr -> TErr(err)
    }
    fun <U, Fn> andThen(fn: Fn): TResult<U, E> where Fn: (T) -> TResult<U, E> = flatMap(fn)
    infix fun <F> or(other: TResult<T, F>): TResult<T, F> = when (this) {
        is TOk -> TOk(value)
        is TErr -> other
    }
    fun <F, Fn> orElse(fn: Fn): TResult<T, F> where Fn: (E) -> TResult<T, F> = when (this) {
        is TOk -> TOk(value)
        is TErr -> fn(err)
    }

    infix fun contains(value: T): Boolean = when (this) {
        is TOk -> value == this.value
        is TErr -> false
    }
    infix fun containsErr(err: E): Boolean = when (this) {
        is TOk -> false
        is TErr -> err == this.err
    }
    fun get(exc: Exception = RuntimeException("cannot get value from Err")): T = when (this) {
        is TOk -> value
        is TErr -> throw exc
    }
    fun getErr(exc: Exception = RuntimeException("cannot get err from Ok")): E = when (this) {
        is TOk -> throw exc
        is TErr -> err
    }
    infix fun getOr(value: T): T = when (this) {
        is TOk -> this.value
        is TErr -> value
    }
    inline fun getOrElse(fn: () -> T): T = when (this) {
        is TOk -> value
        is TErr -> fn()
    }

    // impure
    fun consume(fn: (T) -> Unit) = apply {
        when (this) {
            is TOk -> fn(this.value)
            is TErr -> {
            }
        }
    }

    fun consumeErr(fn: (E) -> Unit) = apply {
        when (this) {
            is TOk -> {
            }
            is TErr -> fn(this.err)
        }
    }
}

data class TOk<T, E>(val value: T): TResult<T, E>() {
    override val isOk = true

    override fun iterator(): Iterator<TResult<T, E>> {
        var finished = false
        val stopIteration = TErr<T, E>(Any() as E)

        return object : Iterator<TResult<T, E>> {
            override fun hasNext() = finished

            override fun next(): TResult<T, E> {
                if (!finished) {
                    finished = true
                    return this@TOk
                }
                return stopIteration
            }
        }
    }
}

data class TErr<T, E>(val err: E): TResult<T, E>() {
    override val isOk = false

    override fun iterator(): Iterator<TResult<T, E>> {
        return object : Iterator<TResult<T,E>> {
            override fun hasNext() = false
            override fun next(): TResult<T, E> = this@TErr
        }
    }
}

fun <T, E> TResult<TResult<T,E>,E>.flatten(): TResult<T,E> {
    return when (this) {
        is TOk -> this.value
        is TErr -> TErr(err)
    }
}
