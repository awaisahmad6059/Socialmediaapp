package com.mk.mkwk

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminCompleteOrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_complete_order_detail)

        val usernameText = findViewById<TextView>(R.id.usernameTextView)
        val userIdText = findViewById<TextView>(R.id.useridTextView)
        val categoryText = findViewById<TextView>(R.id.categoryTextView)
        val descriptionText = findViewById<TextView>(R.id.descriptionTextView)
        val linkText = findViewById<TextView>(R.id.LinkTextView)
        val serviceByText = findViewById<TextView>(R.id.serviceByTextView)
        val amountText = findViewById<TextView>(R.id.amountTextView)
        val chargeText = findViewById<TextView>(R.id.chargeTextView)

        // Set text from intent
        usernameText.text = intent.getStringExtra("username")
        userIdText.text = intent.getStringExtra("userId")
        categoryText.text = intent.getStringExtra("category")
        descriptionText.text = intent.getStringExtra("description")
        linkText.text = intent.getStringExtra("link")
        serviceByText.text = intent.getStringExtra("serviceBy")
        amountText.text = intent.getStringExtra("amount")
        chargeText.text = intent.getStringExtra("charge")

        // Back button
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        // Long click to copy userId
        userIdText.setOnLongClickListener {
            copyToClipboard("User ID", userIdText.text.toString())
            true
        }

        // Long click to copy Link
        linkText.setOnLongClickListener {
            copyToClipboard("Link", linkText.text.toString())
            true
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
