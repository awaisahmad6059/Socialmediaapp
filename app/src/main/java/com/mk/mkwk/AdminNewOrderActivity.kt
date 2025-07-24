package com.mk.mkwk

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class AdminNewOrderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchBar: EditText
    private val firestore = FirebaseFirestore.getInstance()
    private val orderList = mutableListOf<NewOrder>()
    private lateinit var adapter: NewOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_new_order)

        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        searchBar = findViewById(R.id.search_bar)

        adapter = NewOrderAdapter(orderList) { order ->
            val intent = Intent(this, AdminNewOrderDetailActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s?.toString() ?: "")
            }
        })

        fetchOrders()

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchOrders() {
        progressBar.visibility = View.VISIBLE

        firestore.collection("newOrders")
            .whereEqualTo("status", "pending") // Only fetch orders with "Pending" status
            .get()
            .addOnSuccessListener { querySnapshot ->
                orderList.clear()
                val tasks = mutableListOf<Task<*>>()

                for (doc in querySnapshot) {
                    val order = NewOrder(
                        orderId = doc.id,
                        userId = doc.getString("userId") ?: "",
                        category = doc.getString("category") ?: "",
                        description = doc.getString("description") ?: "",
                        link = doc.getString("link") ?: "",
                        serviceBy = doc.getString("service") ?: "",
                        amount = doc.get("amount")?.toString() ?: "0",
                        charge = doc.get("charge")?.toString() ?: "0",
                        status = doc.getString("status") ?: "" // Make sure you include this in your NewOrder model
                    )

                    val task = firestore.collection("users").document(order.userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            order.username = userSnapshot.getString("username") ?: "Unknown"
                        }
                        .addOnFailureListener {
                            order.username = "Unknown"
                        }
                        .continueWith { null }

                    tasks.add(task)
                    orderList.add(order)
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    adapter.updateData(orderList)
                    progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching orders", e)
                progressBar.visibility = View.GONE
            }
    }
}