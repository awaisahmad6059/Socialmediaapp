package com.mk.mkwk

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mk.mkwk.R

class OrderHistoryAdapter(
    private val orderList: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.category)
        val description: TextView = view.findViewById(R.id.description)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.category.text = order.category
        holder.description.text = order.description
        holder.status.text = order.status

        // Status color
        when (order.status.lowercase()) {
            "cancelled" -> holder.status.setTextColor(Color.RED)
            "completed" -> holder.status.setTextColor(Color.parseColor("#4CAF50")) // Green
            "pending" -> holder.status.setTextColor(Color.parseColor("#FFC107")) // Yellow
            else -> holder.status.setTextColor(Color.DKGRAY)
        }

        // Item click listener
        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    override fun getItemCount(): Int = orderList.size
}
