package com.mk.mkwk.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.Order
import com.mk.mkwk.OrderHistoryAdapter
import com.mk.mkwk.R
import com.mk.mkwk.UserOrderHistoryDetailActivity

class UserOrderHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val orderList = mutableListOf<Order>()
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_order_history, container, false)
        recyclerView = view.findViewById(R.id.orderHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrderHistoryAdapter(orderList) { order ->
            val intent = Intent(requireContext(), UserOrderHistoryDetailActivity::class.java)
            intent.putExtra("order", order)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadUserOrders()

        return view
    }

    private fun loadUserOrders() {
        val currentUserId = auth.currentUser?.uid ?: return
        orderList.clear()

        // Fetch from newOrders
        firestore.collection("newOrders")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { newOrders ->
                for (doc in newOrders) {
                    val category = doc.getString("category") ?: ""
                    val description = doc.getString("description") ?: ""
                    val status = doc.getString("status") ?: "In Progress"
                    val userId = doc.getString("userId") ?: ""
                    val link = doc.getString("link") ?: ""
                    val serviceBy = doc.getString("service") ?: ""
                    val amount = doc.get("amount")?.toString() ?: ""
                    val charge = doc.get("charge")?.toString() ?: ""

                    // Fetch username from users collection
                    firestore.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val username = userDoc.getString("username") ?: "Unknown"

                            // Now add to list with username
                            orderList.add(
                                Order(category, description, status, userId, username, link, serviceBy, amount, charge)
                            )
                            adapter.notifyDataSetChanged()
                        }
                }


                // Fetch from completeOrders
                firestore.collection("completeOrders")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnSuccessListener { completedOrders ->
                        for (doc in completedOrders) {
                            val category = doc.getString("category") ?: ""
                            val description = doc.getString("description") ?: ""
                            val status = doc.getString("status") ?: "Completed"
                            val userId = doc.getString("userId") ?: ""
                            val username = doc.getString("username") ?: ""
                            val link = doc.getString("link") ?: ""
                            val serviceBy = doc.getString("service") ?: ""
                            val amount = doc.get("amount")?.toString() ?: ""
                            val charge = doc.get("charge")?.toString() ?: ""
                            orderList.add(
                                Order(category, description, status, userId, username, link, serviceBy, amount, charge)
                            )                        }

                        // Notify adapter after both fetches are done
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Still notify even if second query fails
                        adapter.notifyDataSetChanged()
                    }
            }
            .addOnFailureListener {
                // If first query fails, nothing to show
                adapter.notifyDataSetChanged()
            }
    }

}
