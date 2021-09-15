package fp


sealed class Tuple
data class Tuple2<A, B>(val _1: A, val _2: B) : Tuple()
data class Tuple3<A, B, C>(val _1: A, val _2: B, val _3: C) : Tuple()
data class Tuple4<A, B, C, D>(val _1: A, val _2: B, val _3: C, val _4: D) : Tuple()
data class Tuple5<A, B, C, D, E>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E) : Tuple()
data class Tuple6<A, B, C, D, E, F>(
    val _1: A,
    val _2: B,
    val _3: C,
    val _4: D,
    val _5: E,
    val _6: F
) : Tuple()

data class Tuple7<A, B, C, D, E, F, G>(
    val _1: A,
    val _2: B,
    val _3: C,
    val _4: D,
    val _5: E,
    val _6: F,
    val _7: G
) : Tuple()


// extension and utils
fun String.splitToTuple(delimiter: String): TResult<Tuple, IllegalArgumentException> {
    val ls = this.split(delimiter)
    return TResult.tryResult {
        when (ls.size) {
            2 -> Tuple2(ls[0], ls[1])
            3 -> Tuple3(ls[0], ls[1], ls[2])
            4 -> Tuple4(ls[0], ls[1], ls[2], ls[3])
            5 -> Tuple5(ls[0], ls[1], ls[2], ls[3], ls[4])
            6 -> Tuple6(ls[0], ls[1], ls[2], ls[3], ls[4], ls[5])
            7 -> Tuple7(ls[0], ls[1], ls[2], ls[3], ls[4], ls[5], ls[6])
            else -> throw IllegalArgumentException("Only support Tuple2 to Tuple7")
        }
    }
}

infix fun <A, B> A.tuple(b: B): Tuple2<A, B> = Tuple2(this, b)
infix fun <A, B, C> A.tuple(t2: Tuple2<B, C>): Tuple3<A, B, C> = Tuple3(this, t2._1, t2._2)
infix fun <A, B, C, D> A.tuple(t3: Tuple3<B, C, D>): Tuple4<A, B, C, D> =
    Tuple4(this, t3._1, t3._2, t3._3)

infix fun <A, B, C, D, E> A.tuple(t4: Tuple4<B, C, D, E>): Tuple5<A, B, C, D, E> =
    Tuple5(this, t4._1, t4._2, t4._3, t4._4)

infix fun <A, B, C, D, E, F> A.tuple(t5: Tuple5<B, C, D, E, F>): Tuple6<A, B, C, D, E, F> =
    Tuple6(this, t5._1, t5._2, t5._3, t5._4, t5._5)

infix fun <A, B, C, D, E, F, G> A.tuple(t6: Tuple6<B, C, D, E, F, G>): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(this, t6._1, t6._2, t6._3, t6._4, t6._5, t6._6)

infix fun <A, B, C> Tuple2<A, B>.tuple(c: C): Tuple3<A, B, C> = Tuple3(_1, _2, c)
infix fun <A, B, C, D> Tuple2<A, B>.tuple(t2: Tuple2<C, D>): Tuple4<A, B, C, D> =
    Tuple4(_1, _2, t2._1, t2._2)

infix fun <A, B, C, D, E> Tuple2<A, B>.tuple(t3: Tuple3<C, D, E>): Tuple5<A, B, C, D, E> =
    Tuple5(_1, _2, t3._1, t3._2, t3._3)

infix fun <A, B, C, D, E, F> Tuple2<A, B>.tuple(t4: Tuple4<C, D, E, F>): Tuple6<A, B, C, D, E, F> =
    Tuple6(_1, _2, t4._1, t4._2, t4._3, t4._4)

infix fun <A, B, C, D, E, F, G> Tuple2<A, B>.tuple(t5: Tuple5<C, D, E, F, G>): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(_1, _2, t5._1, t5._2, t5._3, t5._4, t5._5)

infix fun <A, B, C, D> Tuple3<A, B, C>.tuple(d: D): Tuple4<A, B, C, D> = Tuple4(_1, _2, _3, d)
infix fun <A, B, C, D, E> Tuple3<A, B, C>.tuple(t2: Tuple2<D, E>): Tuple5<A, B, C, D, E> =
    Tuple5(_1, _2, _3, t2._1, t2._2)

infix fun <A, B, C, D, E, F> Tuple3<A, B, C>.tuple(t3: Tuple3<D, E, F>): Tuple6<A, B, C, D, E, F> =
    Tuple6(_1, _2, _3, t3._1, t3._2, t3._3)

infix fun <A, B, C, D, E, F, G> Tuple3<A, B, C>.tuple(t4: Tuple4<D, E, F, G>): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(_1, _2, _3, t4._1, t4._2, t4._3, t4._4)

infix fun <A, B, C, D, E> Tuple4<A, B, C, D>.tuple(e: E): Tuple5<A, B, C, D, E> =
    Tuple5(_1, _2, _3, _4, e)

infix fun <A, B, C, D, E, F> Tuple4<A, B, C, D>.tuple(t2: Tuple2<E, F>): Tuple6<A, B, C, D, E, F> =
    Tuple6(_1, _2, _3, _4, t2._1, t2._2)

infix fun <A, B, C, D, E, F, G> Tuple4<A, B, C, D>.tuple(t3: Tuple3<E, F, G>): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(_1, _2, _3, _4, t3._1, t3._2, t3._3)

infix fun <A, B, C, D, E, F> Tuple5<A, B, C, D, E>.tuple(f: F): Tuple6<A, B, C, D, E, F> =
    Tuple6(_1, _2, _3, _4, _5, f)

infix fun <A, B, C, D, E, F, G> Tuple5<A, B, C, D, E>.tuple(t2: Tuple2<F, G>): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(_1, _2, _3, _4, _5, t2._1, t2._2)

infix fun <A, B, C, D, E, F, G> Tuple6<A, B, C, D, E, F>.tuple(g: G): Tuple7<A, B, C, D, E, F, G> =
    Tuple7(_1, _2, _3, _4, _5, _6, g)

fun <K, V> MutableMap<K, V>.putAll(tuple2s: Iterable<Tuple2<K, V>>): MutableMap<K, V> =
    this.apply { putAll(tuple2s.toMap()) }

fun <K, V> MutableMap<K, V>.putAll(tuple2s: Sequence<Tuple2<K, V>>): MutableMap<K, V> =
    this.apply { putAll(tuple2s.toMap()) }

fun <K, V> MutableMap<K, V>.putAll(tuple2s: Array<out Tuple2<K, V>>): MutableMap<K, V> =
    this.apply { putAll(tuple2s.toMap()) }

fun <K, V> mapOf(tuple2: Tuple2<K, V>): Map<K, V> =
    LinkedHashMap<K, V>().apply { put(tuple2._1, tuple2._2) }

fun <K, V> mapOf(vararg tuple2: Tuple2<K, V>): Map<K, V> =
    LinkedHashMap<K, V>().apply { putAll(tuple2) }

operator fun <K, V> Map<out K, V>.plus(tuple2: Tuple2<K, V>): Map<K, V> =
    if (this.isEmpty()) mapOf(tuple2) else LinkedHashMap(this).apply { put(tuple2._1, tuple2._2) }

fun <K, V> Iterable<Tuple2<K, V>>.toMap(): Map<K, V> = LinkedHashMap<K, V>().apply {
    this@toMap.forEach { put(it._1, it._2) }
}

fun <K, V> Sequence<Tuple2<K, V>>.toMap(): Map<K, V> = LinkedHashMap<K, V>().apply {
    this@toMap.forEach { put(it._1, it._2) }
}

fun <K, V> Array<out Tuple2<K, V>>.toMap(): Map<K, V> = LinkedHashMap<K, V>().apply {
    this@toMap.forEach { put(it._1, it._2) }
}

typealias STTuple2<A> = Tuple2<A,A>
typealias STTuple3<A> = Tuple3<A,A,A>
typealias STTuple4<A> = Tuple4<A,A,A,A>
typealias STTuple5<A> = Tuple5<A,A,A,A,A>
typealias STTuple6<A> = Tuple6<A,A,A,A,A,A>
typealias STTuple7<A> = Tuple7<A,A,A,A,A,A,A>

fun <A> STTuple2<A>.toList(): List<A> = listOf(_1, _2)
fun <A> STTuple3<A>.toList(): List<A> = listOf(_1, _2, _3)
fun <A> STTuple4<A>.toList(): List<A> = listOf(_1, _2, _3, _4)
fun <A> STTuple5<A>.toList(): List<A> = listOf(_1, _2, _3, _4, _5)
fun <A> STTuple6<A>.toList(): List<A> = listOf(_1, _2, _3, _4, _5, _6)
fun <A> STTuple7<A>.toList(): List<A> = listOf(_1, _2, _3, _4, _5, _6, _7)
