import java.util.*
import java.util.concurrent.Semaphore


/**
 * 問題1-1:nameとaddressの値を代入する間にスレッド待機の処理をいれる
 * 問題1-2:複数スレッドから操作される場所をpassメソッドに集約することで意図しない値が代入されることを防ぐため
 * 問題1-3:counter/name/addressが代入される前にtoStringが呼ばれると前の値が入った状態で出力される可能性があるため
 * 問題1-4:
 * 1:○　
 * 2:○
 * 3:○
 * 誤4:○⇨× 他のメソッドからフィールドにアクセスしてたとしたら安全であるとは言い切れない
 * 5:×
 * 誤問題1-5:intはプリミティブで呼ばれるたびにinc/decしているだけなので安全
 * ⇨+=は値を調べる.代入するという２つの処理を行なっているためその間に別スレッドの処理が割り込むと値が異なる
 * 問題1-6:
 */
fun main() {
    println("Testing EaterThread, hit CTRL+C to exit")
    val spoon = Tool("Spoon")
    val fork = Tool("Fork")
    EaterThread(fork, spoon, "Alice").start()
    EaterThread(fork, spoon, "Bobby").start()
//    val gate = Gate()
//    mapOf(
//        "Alice" to "Alaska",
//        "Bobby" to "Brazil",
//        "Chris" to "Canada"
//    ).forEach { name, address ->
//        UserThread2(gate, name, address).start()
//    }

//    // 補講2
//    BoundedResource(3).let {
//        for (i in 0..10) {
//            UserThread(it).start();
//        }
//    }
}

class EaterThread(
    private val leftHand: Tool,
    private val rightHand: Tool,
    private val eaterName: String
) : Thread() {
    override fun run() {
        while (true) {
            eat()
        }
    }

    private fun eat() {
        synchronized(leftHand) {
            println("$eaterName takes up $leftHand (left)")
            synchronized(rightHand) {
                println("$eaterName takes up $rightHand (right)")
                println("$eaterName is eating now, yum yum!")
                println("$eaterName puts down righthand (right)")
            }
            println("$eaterName puts down lefthand (left)")
        }
    }
}


class Tool(private val name: String) {
    override fun toString(): String {
        return name
    }
}

class UserThread2(
    private val gate: Gate,
    private val myName: String,
    private val myAddress: String
) : Thread() {
    override fun run() {
        println("$myName BEGIN")
        while (true) {
            gate.pass(myName, myAddress)
        }
    }
}

class Gate {

    private var counter: Int = 0
    private var name: String = "Nobody"
    private var address: String = "Nowhere"

    @Synchronized
    fun pass(name: String, address: String) {
        this.counter++
        this.name = name
        Thread.sleep(2000L)
        this.address = address
        check()
    }

    @Synchronized
    override fun toString(): String {
        return "No.* $counter : $name, $address"
    }

    private fun check() {
        if (name.first() != address.first()) {
            println("*******BROKEN******* " + toString())
        }
    }
}

class BoundedResource(
    private val permits: Int
) {
    private val semaphore: Semaphore = Semaphore(permits)
    private val random: Random = Random(314159)

    fun use() {
        semaphore.acquire();
        try {
            doUse()
        } finally {
            semaphore.release()
        }
    }

    fun doUse() {
        println(Thread.currentThread().name + ": " + "BEGIN: used= " + (permits - semaphore.availablePermits()))
        Thread.sleep(random.nextInt(500).toLong())
        println((Thread.currentThread().name + ": " + "END: used= " + (permits - semaphore.availablePermits())))
    }
}

class UserThread(
    private val resource: BoundedResource,
    private val random: Random = Random(26535)
) : Thread() {

    override fun run() {
        try {
            while (true) {
                resource.use()
                Thread.sleep(random.nextInt(3000).toLong())
            }
        } catch (e: InterruptedException) {

        }
    }
}