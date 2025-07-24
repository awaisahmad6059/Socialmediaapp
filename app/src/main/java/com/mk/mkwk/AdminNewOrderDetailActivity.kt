package com.mk.mkwk

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AdminNewOrderDetailActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_new_order_detail)

        val orderId = intent.getStringExtra("orderId") ?: return
        val deleteButton: Button = findViewById(R.id.deleteTask)
        val cancelButton: Button = findViewById(R.id.cancelTask)
        val completeButton: Button = findViewById(R.id.completeTask)


        fetchOrderDetails(orderId)
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
        cancelButton.setOnClickListener {
            updateOrderStatus(orderId, "Cancelled")
        }
        completeButton.setOnClickListener {
            markOrderAsComplete(orderId)
        }

        deleteButton.setOnClickListener {
            deleteOrder(orderId)
        }
        findViewById<TextView>(R.id.useridTextView).setOnClickListener {
            copyToClipboard("User ID", findViewById<TextView>(R.id.useridTextView).text.toString())
        }
        findViewById<TextView>(R.id.LinkTextView).setOnClickListener {
            copyToClipboard("Link", findViewById<TextView>(R.id.LinkTextView).text.toString())
        }
    }

    private fun deleteOrder(orderId: String) {
        firestore.collection("newOrders").document(orderId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete order", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateOrderStatus(orderId: String, status: String) {
        firestore.collection("newOrders").document(orderId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Order marked as $status", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchOrderDetails(orderId: String) {
        firestore.collection("newOrders").document(orderId).get()
            .addOnSuccessListener { doc ->
                val userId = doc.getString("userId") ?: ""
                val category = doc.getString("category") ?: ""
                val description = doc.getString("description") ?: ""
                val link = doc.getString("link") ?: ""
                val serviceBy = doc.getString("service") ?: ""

                val amount = doc.get("amount")?.toString() ?: ""
                val charge = doc.get("charge")?.toString() ?: ""

                findViewById<TextView>(R.id.useridTextView).text = userId
                findViewById<TextView>(R.id.categoryTextView).text = category
                findViewById<TextView>(R.id.descriptionTextView).text = description
                findViewById<TextView>(R.id.LinkTextView).text = link
                findViewById<TextView>(R.id.serviceByTextView).text = serviceBy
                findViewById<TextView>(R.id.amountTextView).text = amount
                findViewById<TextView>(R.id.chargeTextView).text = charge

                // ✅ Fetch username
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        val username = userDoc.getString("username") ?: "Unknown"
                        findViewById<TextView>(R.id.usernameTextView).text = username
                    }
            }
            .addOnFailureListener {
                // Optionally log or handle error
            }
    }
    private fun markOrderAsComplete(orderId: String) {
        firestore.collection("newOrders").document(orderId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val userId = doc.getString("userId") ?: ""
                    val category = doc.getString("category") ?: ""
                    val description = doc.getString("description") ?: ""
                    val link = doc.getString("link") ?: ""
                    val serviceBy = doc.getString("service") ?: ""
                    val amount = doc.get("amount")?.toString() ?: ""
                    val charge = doc.get("charge")?.toString() ?: ""

                    firestore.collection("users").document(userId).get()
                        .addOnSuccessListener { userDoc ->
                            val username = userDoc.getString("username") ?: "Unknown"

                            val completeOrder = hashMapOf(
                                "userId" to userId,
                                "username" to username,
                                "category" to category,
                                "description" to description,
                                "link" to link,
                                "service" to serviceBy,
                                "amount" to amount,
                                "charge" to charge,
                                "status" to "Completed"
                            )

                            firestore.collection("completeOrders").document(orderId)
                                .set(completeOrder)
                                .addOnSuccessListener {
                                    firestore.collection("newOrders").document(orderId).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Order completed", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                }
                        }
                }
            }
    }
    private fun copyToClipboard(label: String, text: String) {
        if (text.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
        }
    }
}
