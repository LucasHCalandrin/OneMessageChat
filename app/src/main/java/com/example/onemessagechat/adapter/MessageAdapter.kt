package com.example.onemessagechat.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.onemessagechat.R
import com.example.onemessagechat.databinding.TileMessageBinding
import com.example.onemessagechat.model.Message

class MessageAdapter(
    context: Context, private val messageList: MutableList<Message>):
    ArrayAdapter<Message>(context, R.layout.tile_message, messageList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = messageList[position]
        var messageTileView = convertView
        var tmb: TileMessageBinding?= null

            if(messageTileView == null) {
                tmb = TileMessageBinding.inflate(
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                    parent,
                    false
                )
                messageTileView = tmb.root
                val tileMessageHolder = TileMessageHolder(tmb.idTv, tmb.messageTv)
                messageTileView.tag = tileMessageHolder
            }

            val holder = messageTileView.tag as TileMessageHolder
            holder.idTv.setText(message.id.toString())
            holder.messageTv.maxLines = 1
            holder.messageTv.ellipsize = TextUtils.TruncateAt.END
            holder.messageTv.setText(message.message)

            return messageTileView
        }

        private data class TileMessageHolder(val idTv: TextView, val messageTv: TextView)
    }
