package com.mk.mkwk

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddServiceActivity : AppCompatActivity() {

    private lateinit var categoryEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var chargeEditText: EditText
    private lateinit var submitBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var addServiceIcon: ImageView
    private lateinit var serviceContainer: LinearLayout

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_service)

        categoryEditText = findViewById(R.id.editcategory)
        descEditText = findViewById(R.id.desc)
        chargeEditText = findViewById(R.id.charge)
        submitBtn = findViewById(R.id.submit_btn)
        cancelBtn = findViewById(R.id.cancel_btn)
        addServiceIcon = findViewById(R.id.addServiceIcon)
        serviceContainer = findViewById(R.id.serviceContainer)

        // Add the first service field by default
        addServiceField()

        addServiceIcon.setOnClickListener {
            addServiceField()
        }

        submitBtn.setOnClickListener {
            val category = categoryEditText.text.toString().trim()
            val description = descEditText.text.toString().trim()
            val chargeText = chargeEditText.text.toString().trim()

            if (category.isEmpty() || description.isEmpty() || chargeText.isEmpty()) {
                Toast.makeText(this, "Please fill all main fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val charge = chargeText.toDoubleOrNull()
            if (charge == null) {
                Toast.makeText(this, "Please enter a valid charge (e.g., 10, 0.5)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val serviceData = hashMapOf<String, Any>(
                "category" to category,
                "description" to description,
                "charge" to charge
            )

            var serviceCount = 1

            for (i in 0 until serviceContainer.childCount) {
                val child = serviceContainer.getChildAt(i)
                if (child is EditText) {
                    val serviceText = child.text.toString().trim()
                    if (serviceText.isNotEmpty()) {
                        serviceData["service$serviceCount"] = serviceText
                        serviceCount++
                    }
                }
            }

            if (serviceCount == 1) {
                Toast.makeText(this, "Please add at least one service", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("arrays")
                .add(serviceData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Services saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun addServiceField() {
        val newServiceField = EditText(this)
        newServiceField.hint = "Enter Service"
        newServiceField.background = ContextCompat.getDrawable(this, R.drawable.rounded_corner_background)
        newServiceField.inputType = InputType.TYPE_CLASS_TEXT
        newServiceField.setPadding(30, 30, 30, 30)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 12, 0, 0)
        newServiceField.layoutParams = params
        serviceContainer.addView(newServiceField)
    }
}
