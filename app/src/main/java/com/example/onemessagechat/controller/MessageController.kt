package com.example.onemessagechat.controller

import android.os.Message
import androidx.room.Room
import com.example.onemessagechat.model.Constant.MESSAGE_ARRAY
import com.example.onemessagechat.model.MessageRoomDao
import com.example.onemessagechat.model.MessageRoomDao.Companion.MESSAGE_DATABASE_FILE
import com.example.onemessagechat.model.MessageRoomDaoDatabase
import com.example.onemessagechat.view.MainActivity

class MessageController(private val mainActivity: MainActivity) {

    private val messageDaoImpl: MessageRoomDao by lazy {
        Room.databaseBuilder(
            mainActivity,
            MessageRoomDaoDatabase::class.java,
            MESSAGE_DATABASE_FILE
        ).build().getContactRoomDao()
    }


    fun insertMessage(message: com.example.onemessagechat.model.Message) {
        Thread {
            messageDaoImpl.createMessage(message)
            getAllMessages()
        }.start()
    }

    fun getMessage(id: Int) = messageDaoImpl.readMessage(id)

    fun getAllMessages() {
        Thread {
            val returnList = messageDaoImpl.readAllMessages()

            mainActivity.updateMessageListHandler.apply {
                sendMessage(Message().apply {
                    data.putParcelableArray(
                        MESSAGE_ARRAY,
                        returnList.toTypedArray()
                    )
                })
            }

        }.start()
    }

    fun editMessage(message: com.example.onemessagechat.model.Message) {
        Thread {
            messageDaoImpl.updateMessages(message)
            getAllMessages()
        }.start()
    }
}