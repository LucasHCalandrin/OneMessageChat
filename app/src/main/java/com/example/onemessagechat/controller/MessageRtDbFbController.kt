package com.example.onemessagechat.controller

import com.example.onemessagechat.model.Constant.MESSAGE_ARRAY
import com.example.onemessagechat.model.Message
import com.example.onemessagechat.model.MessageDao
import com.example.onemessagechat.model.MessageDaoRtDbFb
import com.example.onemessagechat.view.MainActivity

class MessageRtDbFbController(private val mainActivity: MainActivity) {

    private val msgDaoImpl: MessageDao = MessageDaoRtDbFb()

    fun insertMessage(msg: Message) {
        Thread {
            msgDaoImpl.createMessage(msg)
        }.start()
    }

    interface OnMessageFoundListener {
        fun onMessageFound(msg: Message)
        fun onMessageNotFound()
    }

    fun getMessage(id: String, callback: OnMessageFoundListener) {
        Thread {
            val message = msgDaoImpl.readMessage(id)
            mainActivity.runOnUiThread {
                if (message != null) {
                    callback.onMessageFound(message)
                } else {
                    callback.onMessageNotFound()
                }
            }
        }.start()
    }

    fun getMessages() {
        Thread {
            val returnList = msgDaoImpl.readAllMessages()

            mainActivity.updateMessageListHandler.apply {
                sendMessage(android.os.Message().apply {
                    data.putParcelableArray(
                        MESSAGE_ARRAY,
                        returnList.toTypedArray()
                    )
                })
            }

        }.start()
    }

    fun editMessage(msg: Message){
        Thread {
            msgDaoImpl.updateMessages(msg)
        }.start()
    }
}