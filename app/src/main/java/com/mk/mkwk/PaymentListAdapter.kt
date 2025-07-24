package com.mk.mkwk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mk.mkwk.databinding.ItemPaymentDisplayBinding
import com.mk.mkwk.Fragment.PaymentMethod

class PaymentListAdapter : RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder>() {

    private var paymentList: List<PaymentMethod> = listOf()

    fun submitList(newList: List<PaymentMethod>) {
        paymentList = newList
        notifyDataSetChanged()
    }

    inner class PaymentViewHolder(private val binding: ItemPaymentDisplayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentMethod) {
            binding.servicesText.text = item.service
            binding.ibansText.text = item.iban
            binding.namesText.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentList[position])
    }

    override fun getItemCount(): Int = paymentList.size
}
