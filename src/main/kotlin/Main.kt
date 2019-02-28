import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Exchanger

/**
 * 5-1:
 * 1:○
 * 2:×
 * 3:×
 * 4:○
 * 5:×
 * 6:○
 *
 * 5-2:
 * テーブルクラスをインスタンスが別.前者のスレッドがbufferいっぱいにputするだけで、
 * 後者のスレッドのキューにはなにもputされてないため
 *
 * 5-3:
 * 複数のEaterスレッドから同時にアクセスされる可能性があるため
 *
 * 5-4:
 *
 * 5-6:
 * interruptを使う
 *
 * 5-7:
 * ?
 * ⇨ 実行メソッドがInterruptedExceptionをthrowするようにして、キャンセルしてもいいところにThread.interruptedがtrueだった場合は
 * 当該例外を投げるような処理を書く
 *
 * 5-8:
 * notifyはそのインスタンスのウェイトセットにある任意の１つのスレッド起こすため
 * この処理に関係ないスレッドがwakeされた場合はデットロックになるため
 */
fun main() {
//    val exchanger = Exchanger<CharArray>()
//    val buffer1 = CharArray(10)
//    val buffer2 = CharArray(10)
//    ProducerThread(exchanger, buffer1, 314159).start()
//    ConsumerThread(exchanger, buffer2, 265358).start()
    val table = Table(3)
    val list = arrayListOf(
        MakerThread("MakerThread-1", table, 31415),
        MakerThread("MakerThread-2", table, 91267),
        MakerThread("MakerThread-3", table, 23049),
        EaterThread("EaterThread-1", table, 12383),
        EaterThread("EaterThread-2", table, 30494),
        EaterThread("EaterThread-3", table, 30494)
    )

    list.forEach {
        it.start()
    }
    Thread.sleep(10000)
    list.forEach {
        it.interrupt()
    }


}

class MakerThread(
    name: String,
    private val table: Table,
    seed: Long
) : Thread(name) {
    private val random: Random = Random(
        seed
    )

    override fun run() {
        while (true) {
            try {
                Thread.sleep(random.nextInt(1000).toLong())
                val cake = "[Cake No. ${nextId()} by ${name}"
                table.put(cake)
            } catch (e: InterruptedException) {
                println("InterruptedException!!")
                break
            }
        }
    }

    companion object {
        @JvmField
        var id: Int = 0

        @Synchronized
        @JvmStatic
        fun nextId(): Int = id++
    }
}

class EaterThread(
    name: String,
    private val table: Table,
    seed: Long
) : Thread(name) {
    private val random: Random = Random(
        seed
    )

    override fun run() {
        while (true) {
            try {
                table.take()
                Thread.sleep(random.nextInt(1000).toLong())
            } catch (e: InterruptedException) {
                println("Interruped Exception!!")
                break
            }
        }
    }

    companion object {
        @JvmField
        var id: Int = 0

        @Synchronized
        @JvmStatic
        fun nextId(): Int = id++
    }
}


class Table(count: Int) {
    private val buffer = Collections.synchronizedList(mutableListOf("", "", "", "", ""))
    private var tail: Int = 0
    private var head: Int = 0
    private var count: Int = 0

    @Synchronized
    fun put(cake: String) {
        println("${Thread.currentThread().name} puts $cake")
        if (count >= buffer.size) {
            return
        }
        buffer[tail] = cake
        tail = (tail + 1) % buffer.size
        count++
    }

    @Synchronized
    fun take(): String {
        if (count <= 0) {
            return ""
        }
        val cake = buffer[head]
        head = (head + 1) % buffer.size
        count--
        println("${Thread.currentThread().name} takes $cake")
        return cake
    }

    @Synchronized
    fun clear() {
        buffer.clear()
        println("$buffer")
    }
}


class Table2(count: Int) : ArrayBlockingQueue<String>(count) {


    override fun put(cake: String) {
        println("${Thread.currentThread().name} puts $cake")
        super.put(cake)
    }


    override fun take(): String =
        super.take().apply {
            println("${Thread.currentThread().name} takes $this")
        }
}


class ProducerThread(
    private val exchanger: Exchanger<CharArray>,
    private var buffer: CharArray,
    seed: Long
) : Thread("ProducerThread") {
    private var index: Int = 0
    private val random: Random = Random(seed)

    override fun run() {
        try {
            while (true) {
                for (i in 0 until buffer.size) {
                    buffer[i] = nextChar()
                    println("${Thread.currentThread().name} : ${buffer[i]}")
                }
                println("${Thread.currentThread().name} : BEFORE exchange")
                buffer = exchanger.exchange(buffer)
                println("${Thread.currentThread().name} : AFTER exchange")
            }
        } catch (e: InterruptedException) {

        }
    }

    private fun nextChar(): Char {
        val c = 'A' + index % 26
        index++
        Thread.sleep(random.nextInt(1000).toLong())
        return c
    }
}

class ConsumerThread(
    private val exchanger: Exchanger<CharArray>,
    private var buffer: CharArray,
    seed: Long
) : Thread("ConsumerThread") {
    private val random: Random = Random(seed)

    override fun run() {
        try {
            while (true) {
                println("${Thread.currentThread().name} : BEFORE exchange")
                buffer = exchanger.exchange(buffer)
                println("${Thread.currentThread().name} : AFTER exchange")
                buffer.forEach {
                    println("${Thread.currentThread().name} : -> $it")
                    Thread.sleep(random.nextInt(1000).toLong())
                }
            }
        } catch (e: InterruptedException) {

        }
    }
}

class Host {
    companion object {
        @JvmStatic
        fun execute(count: Int) {

        }
    }
}