package com.mk.mkwk.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mk.mkwk.PaymentListAdapter
import com.mk.mkwk.databinding.FragmentShowPaymentBinding

data class PaymentMethod(
    val service: String = "",
    val iban: String = "",
    val name: String = ""
)

class ShowPaymentFragment : Fragment() {

    private var _binding: FragmentShowPaymentBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: PaymentListAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowPaymentBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PaymentListAdapter()
        binding.recyclerView.adapter = adapter

        listenToRealtimeUpdates()

        return binding.root
    }

    private fun listenToRealtimeUpdates() {
        listenerRegistration = firestore.collection("paymentMethods")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val updatedList = snapshot.documents.mapNotNull { doc ->
                    val service = doc.getString("services") ?: return@mapNotNull null
                    val iban = doc.getString("iban") ?: ""
                    val name = doc.getString("name") ?: ""
                    PaymentMethod(service, iban, name)
                }

                adapter.submitList(updatedList)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listenerRegistration?.remove()
    }
}
