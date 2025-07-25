package com.mk.mkwk

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class AdminCompleteorderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private val firestore = FirebaseFirestore.getInstance()
    private val orderList = mutableListOf<CompletedOrder>()
    private lateinit var adapter: CompletedOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_completeorder)

        recyclerView = findViewById(R.id.recycler_view)
        searchBar = findViewById(R.id.search_bar)

        adapter = CompletedOrderAdapter(orderList,
            onDeleteOrder = { orderId -> showDeleteConfirmationDialog(orderId) },
            onItemClick = { order -> openOrderDetail(order) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Setup search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s?.toString() ?: "")
            }
        })

        fetchCompletedOrders()

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }

    private fun openOrderDetail(order: CompletedOrder) {
        val intent = Intent(this, AdminCompleteOrderDetailActivity::class.java)
        intent.putExtra("orderId", order.orderId)
        intent.putExtra("userId", order.userId)
        intent.putExtra("category", order.category)
        intent.putExtra("description", order.description)
        intent.putExtra("link", order.link)
        intent.putExtra("serviceBy", order.serviceBy)
        intent.putExtra("amount", order.amount)
        intent.putExtra("charge", order.charge)
        intent.putExtra("username", order.username)
        startActivity(intent)
    }


    private fun fetchCompletedOrders() {
        firestore.collection("completeOrders").get()
            .addOnSuccessListener { querySnapshot ->
                orderList.clear()
                val tasks = mutableListOf<Task<*>>()

                for (doc in querySnapshot) {
                    val order = CompletedOrder(
                        orderId = doc.id,
                        userId = doc.getString("userId") ?: "",
                        category = doc.getString("category") ?: "",
                        description = doc.getString("description") ?: "",
                        link = doc.getString("link") ?: "",
                        serviceBy = doc.getString("service") ?: "",
                        amount = doc.get("amount")?.toString() ?: "0",
                        charge = doc.get("charge")?.toString() ?: "0"
                    )

                    val task = firestore.collection("users").document(order.userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            order.username = userSnapshot.getString("username") ?: "Unknown"
                        }
                        .addOnFailureListener { order.username = "Unknown" }
                        .continueWith { null }

                    tasks.add(task)
                    orderList.add(order)
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    adapter.updateData(orderList)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching completed orders", e)
                Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog(orderId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteOrder(orderId)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteOrder(orderId: String) {
        firestore.collection("completeOrders").document(orderId).delete()
            .addOnSuccessListener {
                // Remove from local list and update adapter
                orderList.removeAll { it.orderId == orderId }
                adapter.updateData(orderList)
                Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting order", e)
                Toast.makeText(this, "Failed to delete order", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
            }
    }
}