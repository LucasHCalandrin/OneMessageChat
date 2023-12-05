package com.example.onemessagechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.onemessagechat.R
import com.example.onemessagechat.adapter.MessageAdapter
import com.example.onemessagechat.controller.MessageRtDbFbController
import com.example.onemessagechat.databinding.ActivityMainBinding
import com.example.onemessagechat.model.Constant.MESSAGE_ARRAY
import com.example.onemessagechat.model.Constant.VIEW_MESSAGE
import com.example.onemessagechat.model.Message
import java.lang.NumberFormatException


class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val messageList: MutableList<Message> = mutableListOf()
    private val ableMessage: MutableList<Message> = mutableListOf()

    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter(
            this,
            ableMessage
        )
    }

    private val messageController: MessageRtDbFbController by lazy {
         MessageRtDbFbController(this)
    }

    companion object {
        const val GET_MESSAGE = 1
        const val GET_MESSAGE_INTERVAL = 3000L
    }

    val updateMessageListHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            msg.data.getParcelableArray(MESSAGE_ARRAY)?.also { messageArray ->

                messageList.clear()
                messageArray.forEach {
                    messageList.add(it as com.example.onemessagechat.model.Message)
                }
                messageAdapter.notifyDataSetChanged()
            }

        }
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        amb.messageLv.adapter = messageAdapter

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val message =
                    result.data?.getParcelableExtra<com.example.onemessagechat.model.Message>(
                        EXTRA_MESSAGE
                    )
                message?.let { _message ->
                    if (messageList.any { it.id == message.id }) {
                        messageController.editMessage(_message)
                    } else {
                        messageController.insertMessage(_message)
                    }
                }
            }
        }

        amb.messageLv.setOnItemClickListener { parent, view, position, id ->
            val message = ableMessage[position]
            val viewMessageIntent = Intent(this, MessageActivity::class.java)
                .putExtra(EXTRA_MESSAGE, message)
                .putExtra(VIEW_MESSAGE, true)

            startActivity(viewMessageIntent)
        }

        registerForContextMenu(amb.messageLv)
        updateMessageListHandler.apply {
            sendMessageDelayed(
                obtainMessage().apply { what = GET_MESSAGE },
                GET_MESSAGE_INTERVAL
            )
        }
        amb.enterBt.setOnClickListener {
            val messageId = amb.codeEt.text.toString().trim()

            if (messageId.isNotEmpty()) {
                try {
                    messageController.getMessage(messageId, object : MessageRtDbFbController.OnMessageFoundListener {
                        override fun onMessageFound(msg: Message) {
                            ableMessage.add(msg)
                            Log.d("Lista: ", ableMessage.toString())
                            messageAdapter.notifyDataSetChanged()
                        }

                        override fun onMessageNotFound() {
                            Toast.makeText(
                                this@MainActivity,
                                "Chat Not Found", Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Wrong Chat Code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Code", Toast.LENGTH_SHORT).show()
            }
        }
        ableMessage
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.enterMessageMi -> {
                carl.launch(Intent(this,EnterActivity::class.java))
                true
            }
            R.id.createMessageMi -> {
                carl.launch(Intent(this,MessageActivity::class.java))
                true
            }
            else -> true
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when (item.itemId){
            R.id.editMessageMi -> {
                val messageToEdit = ableMessage[position]
                val editMessageIntent = Intent(this, MessageActivity::class.java)
                editMessageIntent.putExtra(EXTRA_MESSAGE, messageToEdit)
                carl.launch(editMessageIntent)
                true
            }
            else -> {true}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(amb.messageLv)
    }
}
