package com.yalla.meshwar

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.R
import com.yalla.meshwar.adapters.ChatAdapter
import com.yalla.meshwar.models.ChatMessage

open class ChatActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    private var database: DatabaseReference? = null
    private var rideId: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        rideId = intent.getStringExtra("RIDE_ID") ?: intent.getStringExtra("REQUEST_ID") ?: "DEFAULT_RIDE"
        currentUserId = try {
            FirebaseAuth.getInstance().currentUser?.uid ?: "USER_CLIENT"
        } catch (e: Exception) {
            "USER_CLIENT"
        }

        try {
            database = FirebaseDatabase.getInstance().getReference("Chats").child(rideId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        chatAdapter = ChatAdapter(messageList, currentUserId)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = chatAdapter

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
            }
        }

        listenForMessages()
    }

    private fun sendMessage(text: String) {
        val db = database
        if (db != null) {
            val messageId = db.push().key ?: System.currentTimeMillis().toString()
            val message = ChatMessage(senderId = currentUserId, messageText = text)

            db.child(messageId).setValue(message).addOnSuccessListener {
                etMessage.setText("")
            }.addOnFailureListener {
                messageList.add(message)
                chatAdapter.notifyItemInserted(messageList.size - 1)
                rvMessages.scrollToPosition(messageList.size - 1)
                etMessage.setText("")
            }
        } else {
            val message = ChatMessage(senderId = currentUserId, messageText = text)
            messageList.add(message)
            chatAdapter.notifyItemInserted(messageList.size - 1)
            rvMessages.scrollToPosition(messageList.size - 1)
            etMessage.setText("")
        }
    }

    private fun listenForMessages() {
        val db = database ?: return
        try {
            db.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    if (message != null) {
                        messageList.add(message)
                        chatAdapter.notifyItemInserted(messageList.size - 1)
                        rvMessages.scrollToPosition(messageList.size - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
