package com.mk.mkwk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.mk.mkwk.databinding.ItemDeletePaymentBinding

class DeletePaymentAdapter(
    private val items: List<Triple<String, String, DocumentReference>>,
    private val onDelete: (DocumentReference) -> Unit
) : RecyclerView.Adapter<DeletePaymentAdapter.DeleteViewHolder>() {

    inner class DeleteViewHolder(private val binding: ItemDeletePaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(id: String, title: String, ref: DocumentReference) {
            binding.paymentTitle.text = title
            binding.deleteButton.setOnClickListener {
                onDelete(ref)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val binding = ItemDeletePaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {
        val (id, title, ref) = items[position]
        holder.bind(id, title, ref)
    }

    override fun getItemCount(): Int = items.size
}
