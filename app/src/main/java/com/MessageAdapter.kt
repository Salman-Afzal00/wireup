package com

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.mani.wirup.R


class MessageAdapter(val context: Context,val messageList:ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val itemsend=2
    val itemreceive=1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          if (viewType==1){
              val view:View= LayoutInflater.from(context).inflate(R.layout.recieve,parent,false)
              return receiveViewHolder(view)
          }
        else{
              val view:View= LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
              return sentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage=messageList[position]
        if (holder.javaClass==sentViewHolder::class.java){

           val viewHolder=holder as sentViewHolder
           holder.sentmessage.text=currentMessage.message
       }
        else{
            val viewHolder=holder as receiveViewHolder
            holder.receivemessage.text=currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentmessage=messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentmessage.senderId)){
            return itemsend
        }
        else{
            return itemreceive
        }
    }



    class sentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
       val sentmessage=itemView.findViewById<TextView>(R.id.tvsent)
    }
    class receiveViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
      val receivemessage=itemView.findViewById<TextView>(R.id.tvrecieve)
    }


}