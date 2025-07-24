package com.mk.mkwk.Fragment

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
        adapter = OrderHistoryAdapter(orderList)
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
                    orderList.add(Order(category, description, status))
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
                            orderList.add(Order(category, description, status))
                        }

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
