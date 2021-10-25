package fp

import kotlin.reflect.KProperty0

inline fun <reified T> id(t: T): T = t

// ref https://stackoverflow.com/a/33158859
fun is_(clz: Class<*>, obj: Any): Boolean = clz.isAssignableFrom(obj.javaClass)
fun is_(clz: Class<*>): (Any) -> Boolean = curry2(::is_)(clz)
fun not(b: Boolean) = !b
fun eq(a: Any, b: Any) = a::class.java == b::class.java && a == b
fun eq(a: Any): (Any) -> Boolean = curry2(::eq)(a)
fun <T, U, R> flip(f: (T, U) -> R) = { u: U, t: T -> f(t, u) }
fun <T, U, R> flip(f: (T, U) -> R, u: U): (T) -> R = { t: T -> f(t, u) }


// function extension
inline infix fun <A, B, C> ((A) -> B).pipe(crossinline g: (B) -> C): (A) -> C =
    { a: A -> g(this(a)) }

inline infix fun <A, B, C> KProperty0<(A) -> B>.pipe(crossinline g: (B) -> C): (A) -> C =
    this.get() pipe g

inline infix fun <A, B, C> ((B) -> C).compose(crossinline g: (A) -> B): (A) -> C =
    { a: A -> this(g(a)) }

inline infix fun <A, B, C> KProperty0<(B) -> C>.compose(crossinline g: (A) -> B): (A) -> C =
    this.get() compose g

fun <A, B> ((A) -> B).consume(): (A) -> Unit = { a: A ->
    this(a)
    Unit
}

fun <A, B> KProperty0<(A) -> B>.consume(): (A) -> Unit = this.get().consume()

//null operation
inline infix fun <A> A?.notNull(cb: (A) -> Unit) {
    if (this != null) cb(this)
}

inline infix fun String?.notNullOrEmpty(cb: (String) -> Unit) {
    if (!isNullOrEmpty()) cb(this)
}

inline infix fun <A, B> A?.tryNotNull(cb: (A) -> B): B? {
    return this?.let(cb)
}

inline infix fun <A> A?.nullElse(cb: () -> A): A = when {
    this == null -> cb()
    else -> this
}

infix fun <A> A?.nullElse(default: A): A = when {
    this == null -> default
    else -> this
}


// compare
infix fun <A> Comparable<A>.gt(other: A) = this > other
infix fun <A> Comparable<A>.ge(other: A) = this >= other
infix fun <A> Comparable<A>.le(other: A) = this <= other
infix fun <A> Comparable<A>.lt(other: A) = this < other

// curry for function partial application
inline fun <reified A, reified B, reified C> curry2(crossinline fn: (A, B) -> C) =
    { a: A -> { b: B -> fn(a, b) } }

inline fun <reified A, reified B, reified C> curry2(crossinline fn: (A, B) -> C, a: A) = curry2(fn)(a)

inline fun <reified A, reified B, reified C, reified D> curry3(crossinline fn: (A, B, C) -> D) =
    { a: A -> curry2 { b: B, c: C -> fn(a, b, c) } }
inline fun <reified A, reified B, reified C, reified D> curry3(crossinline fn: (A, B, C) -> D, a: A) = curry3(fn)(a)
inline fun <reified A, reified B, reified C, reified D> curry3(crossinline fn: (A, B, C) -> D, a: A, b: B) = curry3(fn)(a)(b)

inline fun <reified A, reified B, reified C, reified D, reified E> curry4(crossinline fn: (A, B, C, D) -> E) =
    { a: A -> curry3 { b: B, c: C, d: D -> fn(a, b, c, d) } }
inline fun <reified A, reified B, reified C, reified D, reified E> curry4(crossinline fn: (A, B, C, D) -> E, a: A) = curry4(fn)(a)
inline fun <reified A, reified B, reified C, reified D, reified E> curry4(crossinline fn: (A, B, C, D) -> E, a: A, b: B) = curry4(fn)(a)(b)
inline fun <reified A, reified B, reified C, reified D, reified E> curry4(crossinline fn: (A, B, C, D) -> E, a: A, b: B, c: C) = curry4(fn)(a)(b)(c)

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> curry5(crossinline fn: (A, B, C, D, E) -> F) =
    { a: A -> curry4 { b: B, c: C, d: D, e: E -> fn(a, b, c, d, e) } }
inline fun <reified A, reified B, reified C, reified D, reified E, reified F> curry5(crossinline fn: (A, B, C, D, E) -> F,a:A) = curry5(fn)(a)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F> curry5(crossinline fn: (A, B, C, D, E) -> F,a:A,b:B) = curry5(fn)(a)(b)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F> curry5(crossinline fn: (A, B, C, D, E) -> F,a:A,b:B,c:C) = curry5(fn)(a)(b)(c)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F> curry5(crossinline fn: (A, B, C, D, E) -> F,a:A,b:B,c:C,d:D) = curry5(fn)(a)(b)(c)(d)

inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G
) = { a: A -> curry5 { b: B, c: C, d: D, e: E, f: F -> fn(a, b, c, d, e, f) } }
inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G,
    a: A
) = curry6(fn)(a)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G,
    a: A,
    b:B,
) = curry6(fn)(a)(b)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G,
    a: A,
    b:B,
    c:C,
) = curry6(fn)(a)(b)(c)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G,
    a: A,
    b:B,
    c:C,
    d:D,
) = curry6(fn)(a)(b)(c)(d)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> curry6(
    crossinline fn: (A, B, C, D, E, F) -> G,
    a: A,
    b:B,
    c:C,
    d:D,
    e:E,
) = curry6(fn)(a)(b)(c)(d)(e)

inline fun <reified A, reified B, reified C> uncurry2(crossinline fn: (A) -> (B) -> C) =
    { a: A, b: B -> fn(a)(b) }

inline fun <reified A, reified B, reified C, reified D> uncurry3(crossinline fn: (A) -> (B) -> (C) -> D) =
    { a: A, b: B, c: C -> fn(a)(b)(c) }

inline fun <reified A, reified B, reified C, reified D, reified E> uncurry4(crossinline fn: (A) -> (B) -> (C) -> (D) -> E) =
    { a: A, b: B, c: C, d: D -> fn(a)(b)(c)(d) }

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> uncurry5(crossinline fn: (A) -> (B) -> (C) -> (D) -> (E) -> F) =
    { a: A, b: B, c: C, d: D, e: E -> fn(a)(b)(c)(d)(e) }

inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> uncurry6(
    crossinline fn: (A) -> (B) -> (C) -> (D) -> (E) -> (F) -> G
) = { a: A, b: B, c: C, d: D, e: E, f: F -> fn(a)(b)(c)(d)(e)(f) }
