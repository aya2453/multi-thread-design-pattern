import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * 4-1:
 * 1:×
 * 2:×
 * 3:×
 * 4:○
 * 5:○
 *
 *
 * 4-2:
 *
 * 4-3:
 * ChangerThreadからchangeメソッドが呼ばれたとき
 *
 * 4-5:
 * ドキュメントのif the thread was already started.
 * より、同じスレッドインスタンスに対して複数回startを呼ぶと当該例外がはかれる
 * １回出力されるのは初回startで立ち上げたスレッドが正常に処理されたため
 *
 *
 */
fun main() {
    val thread = TestThread()
    while (true) {
        thread.start()
    }
//    val requestQueue1 = RequestQueue()
//    val requestQueue2 = RequestQueue()
//    TalkThread(requestQueue1, requestQueue2, "Alice").start()
//    TalkThread(requestQueue2, requestQueue1, "Bobby").start()
//    val data = Data("data.txt", "empty")
//    ChangerThread("ChangerThread", data).start()
//    SaverThread("SaverThread", data).start()
}

class Data(
    private val fileName: String,
    private var content: String
) {
    private var changed: Boolean = false

    @Synchronized
    fun change(newContent: String) {
        content = newContent
        changed = true
    }

    @Synchronized
    fun save() {
        if (!changed) {
            println("${Thread.currentThread().name} balks!, content = $content")
            return
        }
//        Thread.sleep(1000)
        doSave()
        changed = false
    }

    fun doSave() {
        println("${Thread.currentThread().name} calls doSave, content = $content")
        FileWriter(fileName).let {
            it.write(content)
            it.close()
        }
    }
}

class SaverThread(
    name: String,
    private val data: Data
) : Thread(name) {
    override fun run() {
        try {
            while (true) {
                data.save()
                Thread.sleep(1000)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

class ChangerThread(
    name: String,
    private val data: Data
) : Thread(name) {
    private val random: Random = Random()
    override fun run() {
        try {
            var i = 1
            while (true) {
                data.change("No.$i")
                Thread.sleep(random.nextInt(1000).toLong())
                data.save()
                i++
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}


data class Request(val name: String) {
    override fun toString(): String {
        return "[ Request $name ]"
    }
}

class RequestQueue {
    private val queue = LinkedBlockingQueue<Request>()


    fun getRequest(): Request? {
        val request: Request? = null
        try {
            return queue.poll(5L, TimeUnit.SECONDS)
        } catch (e: IllegalStateException) {
            return request
        }
    }

    fun putRequest(request: Request): Boolean {
        return queue.offer(request, 5L, TimeUnit.SECONDS)
    }
}

class ClientThread(
    private val requestQueue: RequestQueue,
    nameS: String,
    seed: Long
) : Thread(nameS) {
    private val random: Random = Random(seed)

    override fun run() {
        for (i in 0..10000) {
            val request = Request("No.$i")
            println("${Thread.currentThread().name} requests $request")
            if (requestQueue.putRequest(request)) {
                println("${Thread.currentThread().name} succeed in adding to queue $request")
            } else {
                println("${Thread.currentThread().name} failed to add to queue $request")
                break
            }
            try {
                Thread.sleep(random.nextInt(1000).toLong())
            } catch (e: InterruptedException) {

            }
        }
    }
}

class TalkThread(
    private val input: RequestQueue,
    private val output: RequestQueue,
    name: String
) : Thread(name) {

    override fun run() {
        println("{ ${Thread.currentThread().name} :BEGIN }")
        for (i in 0..20) {
            val request1 = input.getRequest()
            if (request1 == null) {
                println("Timeout!!")
                break
            }
            println("${Thread.currentThread().name} gets $request1")
            Request(request1.name + "!").let {
                println("${Thread.currentThread().name} puts $it")
                output.putRequest(it)
            }
        }
        println("{ ${Thread.currentThread().name} :END }")
    }
}

class TestThread : Thread() {
    override fun run() {
        println("BEGIN")
        for (i in 0..50) {
            println(".")
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {

            }
        }
        println("END")
    }
}