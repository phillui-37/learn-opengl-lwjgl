package fp

sealed class TMaybe<T>: Iterable<TMaybe<T>> {
    abstract val isSome: Boolean
    val isNone
        get() = !isSome

    companion object {
        fun <T> pure(t: T) = TSome(t)
        inline fun <reified T> empty() = TNone<T>()
        inline fun <reified T> T?.iMaybe(): TMaybe<T> = this.tryNotNull(::pure) ?: TNone()
        fun <T> T?.maybe(): TMaybe<T> = this.tryNotNull(::pure) ?: TNone()
    }

    inline fun <U> fmap(fn: (T) -> U): TMaybe<U> = when (this) {
        is TSome -> TSome(fn(value))
        is TNone -> TNone()
    }
    inline fun <U> fmapOr(default: U, fn: (T) -> U): TMaybe<U> = TSome(when (this) {
        is TSome -> fn(value)
        is TNone -> default
    })
    inline fun <U> fmapOrElse(thenFn: (T) -> U, elseFn: () -> U): TMaybe<U> = TSome(when (this) {
        is TSome -> thenFn(value)
        is TNone -> elseFn()
    })
    inline fun <U> flatMap(fn: (T) -> TMaybe<U>): TMaybe<U> = when (this) {
        is TSome -> fn(value)
        is TNone -> TNone()
    }
    inline fun filter(predicate: (T) -> Boolean): TMaybe<T> = when (this) {
        is TSome -> if (predicate(value)) this else TNone()
        is TNone -> this
    }

    infix fun and(other: TMaybe<T>): TMaybe<T> = when (this) {
        is TSome -> other
        is TNone -> this
    }
    inline fun <U> andThen(fn: (T) -> TMaybe<U>): TMaybe<U> = flatMap(fn)
    infix fun or(other: TMaybe<T>): TMaybe<T> = when (this) {
        is TSome -> this
        is TNone -> other
    }
    inline fun orElse(fn: () -> TMaybe<T>): TMaybe<T> = when (this) {
        is TSome -> this
        is TNone -> fn()
    }
    infix fun xor(other: TMaybe<T>): TMaybe<T> = when (this) {
        is TSome -> if (other is TSome) TNone() else this
        is TNone -> if (other is TNone) this else other
    }


    infix fun contains(value: T): Boolean = when (this) {
        is TSome -> value == this.value
        is TNone -> false
    }

    fun get(): T = when (this) {
        is TSome -> value
        is TNone -> throw RuntimeException("cannot get value from None")
    }

    infix fun getOr(value: T): T = when (this) {
        is TSome -> this.value
        is TNone -> value
    }

    inline fun getOrElse(fn: () -> T): T = when (this) {
        is TSome -> value
        is TNone -> fn()
    }

    infix fun <U> zip(other: TMaybe<U>): TMaybe<Tuple2<T, U>> = when (this) {
        is TNone -> TNone()
        is TSome -> when (other) {
            is TSome -> TSome(Tuple2(this.value, other.value))
            is TNone -> TNone()
        }
    }
    fun getOrNull(): T? = when (this) {
        is TSome -> value
        is TNone -> null
    }

    fun <U, V> zipWith(other: TMaybe<U>, fn: (T, U) -> V): TMaybe<V> = this.zip(other).fmap { x -> fn(x._1, x._2) }
    fun const(value: T): TMaybe<T> = TSome(value)

    // some impure functions
    inline fun consume(fn: (T) -> Unit): TMaybe<T> = this.also {
        when (it) {
            is TSome -> fn(it.value)
            is TNone -> {}
        }
    }

    inline fun consumeNone(fn: () -> Unit): TMaybe<T> = this.also {
        when (it) {
            is TSome -> {}
            is TNone -> fn()
        }
    }

    inline fun consumeAsNever(fn: (T) -> Unit) {
        when (this) {
            is TSome -> fn(value)
            is TNone -> {}
        }
    }

    fun toNullable(): T? = when(this) {
        is TSome<T> -> value
        is TNone<T> -> null
    }

}

data class TSome<T>(val value: T): TMaybe<T>() {
    override val isSome = true
    override fun toString(): String {
        return "Some($value)"
    }

    override fun iterator(): Iterator<TMaybe<T>> {
        var finished = false

        return object : Iterator<TMaybe<T>> {
            override fun hasNext() = !finished

            override fun next(): TMaybe<T> {
                return when (finished) {
                    true -> {
                        finished = true
                        this@TSome
                    }
                    else -> TNone()
                }
            }

        }
    }
}

class TNone<T>: TMaybe<T>() {
    override val isSome = false
    override fun equals(other: Any?): Boolean {
        return other is TNone<*>
    }

    override fun toString(): String {
        return "None"
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun iterator(): Iterator<TMaybe<T>> {
        return object : Iterator<TMaybe<T>> {
            override fun hasNext() = false
            override fun next() = this@TNone
        }
    }
}

fun <T> TMaybe<TMaybe<T>>.flatten(): TMaybe<T> {
    return when (this) {
        is TSome -> this.value
        is TNone -> TNone()
    }
}
