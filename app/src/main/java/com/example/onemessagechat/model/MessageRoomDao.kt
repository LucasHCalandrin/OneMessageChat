package com.example.onemessagechat.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MessageRoomDao {

    companion object {
        const val MESSAGE_DATABASE_FILE = "messages_room"
        private const val MESSAGES_TABLE = "messages"
        private const val ID_COLUMN = "id"
        private const val MESSAGE_COLUMN = "message"

    }

    @Insert
    fun createMessage(message : Message)

    @Query("SELECT *FROM $MESSAGES_TABLE WHERE $ID_COLUMN = :id")
    fun readMessage(id: Int): Message?

    @Query("SELECT * FROM $MESSAGES_TABLE ORDER BY $MESSAGE_COLUMN")
    fun readAllMessages(): MutableList<Message>

    @Update
    fun updateMessages(message: Message): Int

}