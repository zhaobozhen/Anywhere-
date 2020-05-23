package com.absinthe.anywhere_.model.manager

import com.absinthe.anywhere_.adapter.gift.ChatAdapter
import com.absinthe.anywhere_.constants.GiftChatString
import timber.log.Timber
import java.util.*

class ChatQueue(private val mListener: IChatQueueListener) : LinkedList<String>() {

    private var offerThread: Thread? = null
    private var interrupt = false

    fun offer(strings: Array<String>) {
        offerThread = Thread(Runnable {
            try {
                for (str in strings) {
                    if (interrupt) {
                        break
                    }
                    val prefix = str.substring(0, 3)
                    Timber.d(prefix)
                    when (prefix) {
                        GiftChatString.L -> offer(str.substring(3), ChatAdapter.TYPE_LEFT)
                        GiftChatString.R -> offer(str.substring(3), ChatAdapter.TYPE_RIGHT)
                        GiftChatString.I -> offer(str.substring(3), ChatAdapter.TYPE_INFO)
                    }
                    Thread.sleep(2000)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        })
        offerThread!!.start()
    }

    override fun poll(): String? {
        val head = super.poll()!!
        mListener.onDequeue(head)
        Timber.d("Chat Dequeue: %s", head)
        return head
    }

    fun stopOffer() {
        interrupt = true
        offerThread!!.interrupt()
    }

    private fun offer(s: String?, type: Int): Boolean {
        val result = super.offer(s)
        Timber.d("Chat Enqueue: %s", s)
        mListener.onEnqueue(type)
        return result
    }

    interface IChatQueueListener {
        fun onEnqueue(type: Int)
        fun onDequeue(head: String?)
    }

}