/**
 * 問題1 1-1
 * 1:○
 * 2:×⇨startメソッドがrunメソッドを呼び出す
 * 3:×⇨runメソッドのみ
 * 4:○
 * 5:○
 * 6:×⇨現在のスレッドのみ
 * 7:×⇨実行は停止しないし、synchronizedが付いてないメソッドだったらアクセスできる
 * 8:×⇨ウェイトセットにはいるのはwait
 * 誤9:○⇨そのインスタンスのロックを取っている必要があるから　（そのいんすたんすのロックをとる方法はsyncronizedブロックでもそっから呼び出されるメソッド内でもよい）
 * 10:○
 *
 * 謝問題1 1-2
 * コンカレント（並行処理）だから　⇨クラスライブラリの中で適切にスレッドの排他制御が行われているから
 *
 * 問題1 1-3
 * スレッドの起動はThread#startで初めて行われるためrun呼び出してるだけだとシングルスレッド
 *
 * 問題1 1-5
 * Threadインスタンスと実行中のスレッドは別だから
 *
 * 問題1 1-6
 * 1:○
 * 2:○
 * 3:○
 * 4:×
 * 5:×
 * 6:○
 * 7:○
 * 8:○
 * 9:×
 * 10:×
 * 11:×
 * 12×?
 *
 */
fun main() {
    //問題1 1-4
    val bank = Bank(500)
    val runnable = {
        println(Thread.currentThread().name)
        for (i in 0..100000) {
            if (bank.withdraw(500)) {
                bank.deposit(500)
            }
        }
    }
    for (i in 0..4) {
        Thread(runnable).start()
    }
}

class Bank(private var money: Int) {
    fun withdraw(m: Int): Boolean {
        if (money >= m) {
            money -= m
            check()
            return true
        } else {
            return false
        }
    }

    fun deposit(m: Int) {
        money += m
    }

    private fun check() {
        if (money < 0) {
            println(Thread.currentThread().name + "預金残高がマイナスです! money= $money")
        }
    }
}