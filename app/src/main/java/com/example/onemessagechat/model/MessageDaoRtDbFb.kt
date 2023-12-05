package com.example.onemessagechat.model

import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class MessageDaoRtDbFb: MessageDao {
    companion object {
        private const val MESSAGE_LIST = "messageList"
    }

    private val messageRefDb = Firebase.database
        .getReference(MESSAGE_LIST)

    private val messageList: MutableList<Message> = mutableListOf()

    init {
        messageRefDb.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message: Message? = snapshot.getValue<Message>()

                message?.also { newMessage ->
                    if (!messageList.any{ it.id == newMessage.id }){
                        messageList.add(newMessage)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message: Message? = snapshot.getValue<Message>()

                message?.also { editedMessage ->
                    messageList.indexOfFirst { editedMessage.id == it.id }.also {
                        messageList[it] = editedMessage
                    }

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message: Message? = snapshot.getValue<Message>()

                message?.also {
                    messageList.remove(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })

        messageRefDb.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgMap = snapshot.getValue<Map<String, Message>>()

                messageList.clear()
                msgMap?.values?.also {
                    messageList.addAll(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })
    }

    override fun createMessage(msg: Message): Int {
        createOrUpdateMessage(msg)
        return 1
    }

    override fun readMessage(id: String): Message? {
        val index = messageList.indexOfFirst { it.id == id }
        return if (index != -1) {
            messageList[index]
        } else {
            null
        }
    }


    override fun readAllMessages(): MutableList<Message> = messageList

    override fun updateMessages(msg: Message): Int {
        createOrUpdateMessage(msg)
        return 1
    }

    private fun createOrUpdateMessage(msg: Message) =
        messageRefDb.child(msg.id.toString()).setValue(msg)
}