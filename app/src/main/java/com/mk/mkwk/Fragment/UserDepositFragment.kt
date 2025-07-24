package com.mk.mkwk.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.UserPaymentAdapter
import com.mk.mkwk.databinding.FragmentUserDepositBinding

class UserDepositFragment : Fragment() {
    private var _binding: FragmentUserDepositBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val paymentDocuments = mutableListOf<DocumentSnapshot>()
    private lateinit var adapter: UserPaymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDepositBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserPaymentAdapter(paymentDocuments)
        binding.paymentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentRecyclerView.adapter = adapter

        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        firestore.collection("paymentMethods")
            .get()
            .addOnSuccessListener { result ->
                paymentDocuments.clear()
                paymentDocuments.addAll(result.documents)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("UserDepositFragment", "Error loading payment methods", e)
                Toast.makeText(requireContext(), "Failed to load payment methods", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
