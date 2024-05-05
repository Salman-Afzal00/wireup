package com.mani.wirup

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Message
import com.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Objects

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var messagebox:EditText
    private lateinit var btnsend:ImageView
    private lateinit var messageAdapter:MessageAdapter
    private lateinit var messageList:ArrayList<Message>
    private lateinit var dbuser:DatabaseReference
        var senderroom:String?=null
        var receiverroom:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)
        dbuser=FirebaseDatabase.getInstance().getReference()

        val name=intent.getStringExtra("NAME")
        val receiveruid=intent.getStringExtra("UID")
        val senderuid=FirebaseAuth.getInstance().currentUser?.uid
        senderroom=receiveruid + senderuid
        receiverroom=senderuid + receiveruid

        supportActionBar?.hide()
        mRecyclerView=findViewById(R.id.chatrecycle)
        messagebox=findViewById(R.id.edtchat)
        btnsend=findViewById(R.id.btnsend)
        messageList=ArrayList()
        messageAdapter=MessageAdapter(this,messageList)
        mRecyclerView.layoutManager=LinearLayoutManager(this)
        mRecyclerView.adapter=messageAdapter
        dbuser.child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postsnapshote in snapshot.children){
                        val message=postsnapshote.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })



            btnsend.setOnClickListener{
                val message=messagebox.text.toString()
                val messageobject=Message(message,senderuid)
                if (message.isNotEmpty()){
                    dbuser.child("chats").child(senderroom!!).child("messages").push()
                        .setValue(messageobject).addOnSuccessListener {
                            dbuser.child("chats").child(receiverroom!!).child("messages").push()
                                .setValue(messageobject)
                        }
                }
                else{
                    Toast.makeText(this, "Empty message not accepted", Toast.LENGTH_SHORT).show()
                }

                
                messagebox.setText("")
            }

       
    }
}