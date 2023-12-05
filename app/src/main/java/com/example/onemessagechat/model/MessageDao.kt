package com.example.onemessagechat.model

interface MessageDao {
    fun createMessage(message: Message): Int
    fun readMessage(id: Int): Message?
    fun readAllMessages(): MutableList<Message>
    fun updateMessages(message: Message): Int
}