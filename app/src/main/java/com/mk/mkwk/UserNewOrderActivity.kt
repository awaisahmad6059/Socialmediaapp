package com.mk.mkwk

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserNewOrderActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerServices: Spinner
    private lateinit var inputDescription: TextView
    private lateinit var inputLink: EditText
    private lateinit var inputAmount: EditText
    private lateinit var inputCharge: TextView
    private lateinit var submitBtn: Button

    private var userId: String? = null
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val categoryMap = mutableMapOf<String, Map<String, Any>>()
    private var currentCharge: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_new_order)

        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerServices = findViewById(R.id.spinner_services)
        inputDescription = findViewById(R.id.input_description)
        inputLink = findViewById(R.id.link)
        inputAmount = findViewById(R.id.amount)
        inputCharge = findViewById(R.id.input_charge)
        submitBtn = findViewById(R.id.submit_btn)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val cancelButton = findViewById<Button>(R.id.cancel_btn)

        backButton.setOnClickListener { finish() }
        cancelButton.setOnClickListener { finish() }

        userId = intent.getStringExtra("userId") ?: auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User ID is not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchCategories()
        setupAmountListener()
        setupSubmitButton()
    }

    private fun fetchCategories() {
        firestore.collection("arrays")
            .get()
            .addOnSuccessListener { result ->
                val categoryList = mutableListOf("Choose Category")

                for (document in result) {
                    val category = document.getString("category") ?: continue
                    categoryList.add(category)
                    categoryMap[category] = document.data
                }

                val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = categoryAdapter

                spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedCategory = categoryList[position]
                        if (selectedCategory != "Choose Category") {
                            updateServicesAndDescription(selectedCategory)
                        } else {
                            inputDescription.setText("")
                            spinnerServices.adapter = ArrayAdapter(
                                this@UserNewOrderActivity,
                                android.R.layout.simple_spinner_item,
                                listOf("Choose Service")
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load categories: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateServicesAndDescription(category: String) {
        val data = categoryMap[category] ?: return
        inputDescription.setText(data["description"] as? String ?: "")

        val services = mutableListOf("Choose Service")
        services.addAll(
            data.filterKeys { it.startsWith("service") }
                .map { it.value.toString() }
        )

        val serviceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, services)
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServices.adapter = serviceAdapter

        currentCharge = when (val charge = data["charge"]) {
            is Long -> charge.toDouble()
            is Double -> charge
            is String -> charge.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

        calculateAndUpdateCharge()
    }

    private fun setupAmountListener() {
        inputAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amountText = s.toString()
                if (amountText.isNotEmpty()) {
                    try {
                        val amount = amountText.toDouble()
                        if (amount < 100) {
                            inputCharge.text = ""
                            Toast.makeText(this@UserNewOrderActivity, "Minimum amount is 100", Toast.LENGTH_SHORT).show()
                        } else {
                            calculateAndUpdateCharge()
                        }
                    } catch (e: NumberFormatException) {
                        inputCharge.text = ""
                    }
                } else {
                    inputCharge.text = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun calculateAndUpdateCharge() {
        val amount = inputAmount.text.toString().toDoubleOrNull()
        if (amount != null && amount >= 100) {
            val total = currentCharge * amount
            inputCharge.text = "Rs ${"%.2f".format(total)}"
        } else {
            inputCharge.text = ""
        }
    }

    private fun setupSubmitButton() {
        submitBtn.setOnClickListener {
            val selectedCategory = spinnerCategory.selectedItem?.toString() ?: ""
            val selectedService = spinnerServices.selectedItem?.toString() ?: ""
            val description = inputDescription.text.toString().trim()
            val link = inputLink.text.toString().trim()
            val amount = inputAmount.text.toString().toDoubleOrNull()
            val chargeText = inputCharge.text.toString()

            if (selectedCategory == "Choose Category" ||
                selectedService == "Choose Service" ||
                description.isEmpty() ||
                link.isEmpty() ||
                amount == null || amount < 100 ||
                chargeText.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Extract the numeric value from chargeText (e.g., "Rs 100.00" -> 100.00)
            val chargeValue = chargeText.replace("Rs ", "").toDoubleOrNull() ?: 0.0

            // Check user's coin balance
            firestore.collection("coins").document(userId!!)
                .get()
                .addOnSuccessListener { document ->
                    val currentCoins = document.getLong("coins")?.toDouble() ?: 0.0

                    if (currentCoins < chargeValue) {
                        Toast.makeText(
                            this,
                            "Insufficient coins balance. You need ${chargeValue.toInt()}/- pkr but have only ${currentCoins.toInt()}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Proceed with order submission
                        submitOrder(selectedCategory, selectedService, description, link, amount, chargeText, chargeValue)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to check coins balance: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun submitOrder(
        category: String,
        service: String,
        description: String,
        link: String,
        amount: Double,
        chargeText: String,
        chargeValue: Double
    ) {
        val orderData = hashMapOf(
            "userId" to userId,
            "category" to category,
            "service" to service,
            "description" to description,
            "link" to link,
            "amount" to amount,
            "charge" to chargeText,
            "timestamp" to getCurrentFormattedDateTime(),
            "status" to "pending"
        )

        firestore.collection("newOrders")
            .add(orderData)
            .addOnSuccessListener { documentReference ->
                // Deduct coins after successful order submission
                deductCoins(chargeValue)
                Toast.makeText(this, "Order submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deductCoins(chargeValue: Double) {
        firestore.collection("coins").document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                val currentCoins = document.getLong("coins")?.toDouble() ?: 0.0
                val newCoins = currentCoins - chargeValue

                firestore.collection("coins").document(userId!!)
                    .update("coins", newCoins)
                    .addOnFailureListener { e ->
                        Log.e("UserNewOrder", "Failed to update coins", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("UserNewOrder", "Failed to get current coins", e)
            }
    }

    private fun getCurrentFormattedDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}