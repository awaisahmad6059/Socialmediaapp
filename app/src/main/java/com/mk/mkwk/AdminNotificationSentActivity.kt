package com.mk.mkwk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.databinding.ActivityAdminNotificationSentBinding

class AdminNotificationSentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminNotificationSentBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNotificationSentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""

        binding.tvRecipientName.text = username

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()

            if (title.isEmpty()) {
                binding.tilTitle.error = "Please enter a title"
                return@setOnClickListener
            } else {
                binding.tilTitle.error = null
            }

            if (message.isEmpty()) {
                binding.tilMessage.error = "Please enter a message"
                return@setOnClickListener
            } else {
                binding.tilMessage.error = null
            }

            sendNotification(userId, title, message)
        }
    }

    private fun sendNotification(userId: String, title: String, message: String) {
        val notificationData = hashMapOf(
            "title" to title,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "read" to false
        )

        firestore.collection("users").document(userId)
            .collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Notification sent successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send notification: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}