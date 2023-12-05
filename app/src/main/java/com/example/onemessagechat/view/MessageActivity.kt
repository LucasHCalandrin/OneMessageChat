package com.example.onemessagechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import com.example.onemessagechat.databinding.ActivityMessageBinding
import com.example.onemessagechat.model.Constant.VIEW_MESSAGE
import java.util.Random

class MessageActivity : AppCompatActivity() {

    private val amb: ActivityMessageBinding by lazy {
        ActivityMessageBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Details"

        val receivedMessage = intent.getParcelableExtra<com.example.onemessagechat.model.Message>(EXTRA_MESSAGE)
        receivedMessage?.let {_receivedMessage ->
            val viewMessage: Boolean = intent.getBooleanExtra(VIEW_MESSAGE, false)
            with(amb) {
                if (viewMessage) {
                    messageEt.isEnabled = false
                    saveBt.visibility = View.GONE
                }

            }

        }

        with(amb) {
            val messageId = receivedMessage?.id ?: generateId()
            Log.d("messageId", messageId.toString())
            idTv.text = messageId.toString()

            saveBt.setOnClickListener {

                val message = com.example.onemessagechat.model.Message(
                    id = messageId,
                    message = messageEt.text.toString()
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_MESSAGE, message)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun generateId() = Random(System.currentTimeMillis()).nextInt()

}