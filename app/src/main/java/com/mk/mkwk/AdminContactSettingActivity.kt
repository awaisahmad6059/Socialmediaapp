package com.mk.mkwk

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminContactSettingActivity : AppCompatActivity() {

    private lateinit var whatsappNumberEditText: EditText
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val contactDocRef = firestore.collection("contact").document("admin_contact")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_contact_setting)

        whatsappNumberEditText = findViewById(R.id.whatsappNumberEditText)
        addButton = findViewById(R.id.addButton)
        cancelButton = findViewById(R.id.cancelButton)

        loadExistingContact()

        addButton.setOnClickListener {
            val number = whatsappNumberEditText.text.toString().trim()
            if (number.isEmpty()) {
                Toast.makeText(this, "Please enter a WhatsApp number", Toast.LENGTH_SHORT).show()
            } else {
                saveContactNumber(number)
            }
        }

        cancelButton.setOnClickListener {
            whatsappNumberEditText.setText("")
        }
    }

    private fun loadExistingContact() {
        contactDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val number = document.getString("whatsappNumber")
                whatsappNumberEditText.setText(number)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load contact", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContactNumber(number: String) {
        val data = hashMapOf("whatsappNumber" to number)

        contactDocRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show()
            }
    }
}
