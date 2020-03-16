package com.absinthe.anywhere_.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.absinthe.anywhere_.adapter.gift.ChatAdapter
import com.absinthe.anywhere_.adapter.gift.InfoNode
import com.absinthe.anywhere_.adapter.gift.LeftChatNode
import com.absinthe.anywhere_.adapter.gift.RightChatNode
import com.absinthe.anywhere_.cloud.GiftStatusCode
import com.absinthe.anywhere_.cloud.interfaces.GiftRequest
import com.absinthe.anywhere_.cloud.model.GiftModel
import com.absinthe.anywhere_.cloud.model.GiftPriceModel
import com.absinthe.anywhere_.model.ChatQueue
import com.absinthe.anywhere_.model.ChatQueue.IChatQueueListener
import com.absinthe.anywhere_.model.GiftChatString
import com.absinthe.anywhere_.utils.AppUtils
import com.absinthe.anywhere_.utils.CipherUtils
import com.absinthe.anywhere_.utils.StorageUtils
import com.absinthe.anywhere_.utils.manager.URLManager
import com.chad.library.adapter.base.entity.node.BaseNode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException

class GiftViewModel(application: Application) : AndroidViewModel(application) {

    val chatQueue: ChatQueue
    val node = MutableLiveData<BaseNode>()
    val thirdTimesPrice = MutableLiveData<Int>()
    val infinityPrice = MutableLiveData<Int>()

    init {
        chatQueue = ChatQueue(object : IChatQueueListener {
            override fun onEnqueue(type: Int) {
                onEnqueueImpl(type)
            }

            override fun onDequeue(head: String?) {}
        })
    }

    fun getCode(code: String?) {
        val retrofit = Retrofit.Builder()
                .baseUrl(URLManager.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val request = retrofit.create(GiftRequest::class.java)
        val gift = request.requestByCode(code, AppUtils.getAndroidId(getApplication()))
        gift?.enqueue(object : Callback<GiftModel?> {
            override fun onResponse(call: Call<GiftModel?>, response: Response<GiftModel?>) {
                val giftModel = response.body()
                if (giftModel != null) {
                    if (giftModel.statusCode == GiftStatusCode.STATUS_SUCCESS) {
                        val data = giftModel.data
                        if (data == null) {
                            Timber.d("data == null")
                            return
                        }
                        if (data.isActive == 0) {
                            chatQueue.clear()
                            chatQueue.offer(GiftChatString.purchaseResponse)
                            val encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()))
                            try {
                                StorageUtils.storageToken(getApplication(), encode)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else if (data.isActive == 1 && data.ssaid == AppUtils.getAndroidId(getApplication())) {
                            chatQueue.offer(GiftChatString.hasPurchasedResponse)
                            val encode = CipherUtils.encrypt(AppUtils.getAndroidId(getApplication()))
                            try {
                                StorageUtils.storageToken(getApplication(), encode)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else if (data.isActive == 1 && data.ssaid != AppUtils.getAndroidId(getApplication())) {
                            chatQueue.offer(GiftChatString.notYourCodeResponse)
                        }
                    } else if (giftModel.statusCode == GiftStatusCode.STATUS_NO_MATCH_DATA) {
                        chatQueue.offer(GiftChatString.notExistCodeResponse)
                    } else {
                        chatQueue.offer(GiftChatString.abnormalResponse)
                    }
                }
            }

            override fun onFailure(call: Call<GiftModel?>, t: Throwable) {
                Timber.d("Failed: %s", t.message)
            }
        })
    }

    val price: Unit
        get() {
            if (thirdTimesPrice.value != null && infinityPrice.value != null) {
                return
            }
            val retrofit = Retrofit.Builder()
                    .baseUrl(URLManager.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val request = retrofit.create(GiftRequest::class.java)
            val price = request.requestPrice()
            price?.enqueue(object : Callback<GiftPriceModel?> {
                override fun onResponse(call: Call<GiftPriceModel?>, response: Response<GiftPriceModel?>) {
                    val priceModel = response.body()
                    if (priceModel != null) {
                        thirdTimesPrice.value = priceModel.thirdTimesGiftPrice
                        infinityPrice.value = priceModel.infinityGiftPrice
                    }
                }

                override fun onFailure(call: Call<GiftPriceModel?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

    fun responseChat() {
        chatQueue.offer(GiftChatString.leisureResponse)
    }

    fun stopOffer() {
        chatQueue.stopOffer()
    }

    fun addChat(msg: String?, type: Int) {
        when (type) {
            ChatAdapter.TYPE_LEFT -> {
                val node = LeftChatNode()
                node.msg = msg
                addNode(node)
            }
            ChatAdapter.TYPE_RIGHT -> {
                val node = RightChatNode()
                node.msg = msg
                addNode(node)
            }
            else -> {
                val node = InfoNode()
                node.msg = msg
                addNode(node)
            }
        }
    }

    private fun addNode(node: BaseNode) {
        Handler(Looper.getMainLooper()).post { this.node.setValue(node) }
    }

    private fun onEnqueueImpl(type: Int) {
        addChat(chatQueue.poll(), type)
    }
}