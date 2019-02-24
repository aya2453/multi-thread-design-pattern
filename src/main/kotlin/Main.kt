import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * 3-1:
 * 1:○
 * 2:×
 * 3:○
 * 4:○
 * 5:×
 * 6:×
 * 7:×
 *
 * 3-2:
 * 誤:動作しない⇨動作する
 * notifyAllを呼び出すと、ウェイトセットに入っていたスレッドが復帰してまたロックを取得する
 * そして、そのあとに呼ばれたqueue.offerによって現在実行中のスレッドもLinkedBlockingQueueにアクセスしようとするので
 * InterruptedExceptionが投げられる⇨notifyAllを呼び出氏の時点でthisがロックをとっているので、ウェイトセットから起こされたスレッドがロックをとる
 * ことはないので問題はない
 *
 * 3-3:
 *
 * 3-4:
 * 1:nullじゃ無くなるまで待ち続けないとqueue.removeで例外が発生する
 * 2:queue.peekしてからremoveするまでの間に別スレッドがqueueを操作する可能性がある
 * 誤3:例外が発生した時にもwhileが走り続ける⇨他のスレッドがinterruptメソッドを読んだ時に、まだガード条件を満たしてなくてもwhileを抜けて
 * queue.removeを読んでしまう可能性がある
 * 4:waitはwaitを読んだインスタンスのウェイトセットにスレッドを待機させるが、sleepはカレントスレッドを指定時間中断するだけ
 * ロックを持ち続けるので、他のスレッドが入ってくることができない
 *
 * 3-5:
 * getRequestでガード条件が常にtrueを返すため。初期メッセージを設定してあげる
 *
 *
 */
fun main() {
    val requestQueue1 = RequestQueue()
    val requestQueue2 = RequestQueue()
    TalkThread(requestQueue1, requestQueue2, "Alice").start()
    TalkThread(requestQueue2, requestQueue1, "Bobby").start()
//    val requestQueue = RequestQueue()
//    ClientThread(requestQueue, "Alice", 3141592L).start()
//    ServerThread(requestQueue, "Bobby", 6535897L).start()

}

data class Request(val name: String) {
    override fun toString(): String {
        return "[ Request $name ]"
    }
}

class RequestQueue {
    private val queue = LinkedBlockingQueue<Request>()

    init {
        queue.add(Request("first"))
    }

    fun getRequest(): Request {
        return queue.take()
    }

    fun putRequest(request: Request) {
        queue.put(request)
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
            Request("No.$i").let {
                println("${Thread.currentThread().name} requests $it")
                requestQueue.putRequest(it)
            }
            try {
                Thread.sleep(random.nextInt(1000).toLong())
            } catch (e: InterruptedException) {

            }
        }
    }
}

class ServerThread(
    private val requestQueue: RequestQueue,
    nameS: String,
    seed: Long
) : Thread(nameS) {
    private val random: Random = Random(seed)

    override fun run() {
        for (i in 0..10000) {
            println("${Thread.currentThread().name} requests ${requestQueue.getRequest()}")
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
            println("${Thread.currentThread().name} gets $request1")
            Request(request1.name + "!").let {
                println("${Thread.currentThread().name} puts $it")
                output.putRequest(it)
            }
        }
        println("{ ${Thread.currentThread().name} :END }")
    }
}