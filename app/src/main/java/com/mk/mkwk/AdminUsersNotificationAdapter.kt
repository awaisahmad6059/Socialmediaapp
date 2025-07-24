package com.mk.mkwk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mk.mkwk.databinding.AdminnotificationitemBinding

class AdminUsersNotificationAdapter(
    private val users: List<NotificationUser>,
    private val onItemClick: (NotificationUser) -> Unit
) : RecyclerView.Adapter<AdminUsersNotificationAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: AdminnotificationitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = AdminnotificationitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.username
        holder.binding.tvRole.text = user.userType
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int = users.size
}
