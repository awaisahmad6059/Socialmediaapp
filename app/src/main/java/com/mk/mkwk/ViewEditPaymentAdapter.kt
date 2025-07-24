package com.mk.mkwk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.databinding.ItemEditablePaymentBinding

class ViewEditPaymentAdapter(
    private val data: List<Triple<String, String, List<String>>>,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<ViewEditPaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(private val binding: ItemEditablePaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isEditable = false

        fun bind(docId: String, service: String, others: List<String>) {
            val iban = others.getOrNull(0) ?: ""
            val name = others.getOrNull(1) ?: ""

            binding.serviceEditText.setText(service)
            binding.ibanEditText.setText(iban)
            binding.nameEditText.setText(name)

            setEditable(false)

            binding.editIcon.setOnClickListener {
                isEditable = !isEditable
                setEditable(isEditable)
                binding.saveIcon.visibility = if (isEditable) View.VISIBLE else View.GONE
            }

            binding.saveIcon.setOnClickListener {
                val newService = binding.serviceEditText.text.toString().trim()
                val newIban = binding.ibanEditText.text.toString().trim()
                val newName = binding.nameEditText.text.toString().trim()

                if (newService.isNotEmpty() && newIban.isNotEmpty() && newName.isNotEmpty()) {
                    val updates = mapOf(
                        "services" to newService,
                        "iban" to newIban,
                        "name" to newName
                    )

                    firestore.collection("paymentMethods").document(docId)
                        .update(updates)
                        .addOnSuccessListener {
                            setEditable(false)
                            binding.saveIcon.visibility = View.GONE
                        }
                }
            }
        }


        private fun setEditable(enabled: Boolean) {
            binding.serviceEditText.isEnabled = enabled
            binding.ibanEditText.isEnabled = enabled
            binding.nameEditText.isEnabled = enabled
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemEditablePaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val (id, services, other) = data[position]
        holder.bind(id, services, other)
    }

    override fun getItemCount(): Int = data.size
}
