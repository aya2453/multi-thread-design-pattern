/**
 * 2-1:
 * 1:○
 * 2:×
 * 3:○
 * 4:×
 * 5:×
 *
 * 2-2:
 * 置き換えもとのStringとreplace実行後のStringは別インスタンスのため、前者の値が書き換わっているわけではない
 *
 * 2-3:
 * NotSynch : BEGIN!!
 * NotSynch : END!!
 * Elapsed time = 37 ms
 * Synch : BEGIN!!
 * Synch : END!!
 * Elapsed time = 1950 ms
 *
 *2-4:
 * UserInfoのフィールドに持っているStringBufferがmutableで、この参照に対してアクセスされると値が変更されるため
 *
 *2-5:
 * 誤:Immutable.
 * コンストラクタで渡したmutableなPointインスタンスをprivate finalでフィールドに保持し
 * 値を取得するときはそのpointインスタンスからプリミティブなintを返している
 * 外からPointインスタンスに直接変更をくわえられないようになっているしpointインスタンスを返すようなgetterもないためImmutableである
 *
 * ⇨フィールドにpointを持っているがこの参照を外部で持っているインスタンスがあった場合にこの値はセットされた以降も変わる可能性がある
 *
 * 2-6
 * ImmutablePersonでMutablePersonを引数にとっているが、代入の最中に複数スレッドからアクセスされる可能性がある
 *
 *
 */
fun main() {
    trial("NotSynch", 100000000L, NoSynch())
    trial("Synch", 100000000L, Synch())
}

fun trial(msg: String, count: Long, obj: Any) {
    println("$msg : BEGIN!!")
    val startTime = System.currentTimeMillis()
    for (i in 0..count) {
        obj.toString()
    }
    println(
        """
        $msg : END!!
        Elapsed time = ${System.currentTimeMillis() - startTime} ms
        """.trimIndent()
    )

}

class Synch {
    private val name: String = "Synch"

    @Synchronized
    override fun toString(): String {
        return name
    }
}

class NoSynch {
    private val name: String = "NoSynch"

    override fun toString(): String {
        return name
    }
}