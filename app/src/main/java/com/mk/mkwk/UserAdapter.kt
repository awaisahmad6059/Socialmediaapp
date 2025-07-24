package com.mk.mkwk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.databinding.UsermanagementItemBinding

data class User(
    val userId: String = "",
    val username: String = "",
    val userType: String = "",
    var coins: Int = 0

)

class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: UsermanagementItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UsermanagementItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val firestore = FirebaseFirestore.getInstance()
        val coinsRef = firestore.collection("coins").document(user.userId)

        // Fetch real-time coins
        coinsRef.addSnapshotListener { snapshot, _ ->
            val coinValue = snapshot?.getLong("coins")?.toInt() ?: 0
            user.coins = coinValue
            holder.binding.tvCount.setText(coinValue.toString())
        }
        holder.binding.tvName.text = user.username
        holder.binding.tvRole.text = user.userType
        holder.binding.btnOk.visibility = View.GONE
        holder.binding.btnPlus.setOnClickListener {
            user.coins++
            holder.binding.tvCount.setText(user.coins.toString())
            saveCoinsToFirestore(coinsRef, user)
        }
        holder.binding.btnMinus.setOnClickListener {
            if (user.coins > 0) {
                user.coins--
                holder.binding.tvCount.setText(user.coins.toString())
                saveCoinsToFirestore(coinsRef, user)
            }
        }
        holder.binding.btnOk.visibility = View.GONE

        holder.binding.tvCount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Show OK button as soon as user starts editing
                holder.binding.btnOk.visibility = View.VISIBLE
            } else {
                val entered = holder.binding.tvCount.text.toString().toIntOrNull()
                if (entered != null && entered == user.coins) {
                    holder.binding.btnOk.visibility = View.GONE
                }
            }
        }

        holder.binding.btnOk.setOnClickListener {
            val entered = holder.binding.tvCount.text.toString().toIntOrNull() ?: user.coins
            user.coins = entered
            saveCoinsToFirestore(coinsRef, user)
            holder.binding.btnOk.visibility = View.GONE
        }

    }
    private fun saveCoinsToFirestore(docRef: DocumentReference, user: User) {
        val data = mapOf(
            "userId" to user.userId,
            "username" to user.username,
            "coins" to user.coins
        )
        docRef.set(data)
    }

    override fun getItemCount(): Int = users.size

}

