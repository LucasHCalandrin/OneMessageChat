package com.example.onemessagechat.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1)
abstract class MessageRoomDaoDatabase: RoomDatabase() {
    abstract fun getContactRoomDao(): MessageRoomDao
}