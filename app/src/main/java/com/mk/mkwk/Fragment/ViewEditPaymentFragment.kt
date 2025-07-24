package com.mk.mkwk.Fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mk.mkwk.ViewEditPaymentAdapter
import com.mk.mkwk.databinding.FragmentViewEditPaymentBinding

class ViewEditPaymentFragment : Fragment() {

    private var _binding: FragmentViewEditPaymentBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    private var adapter: ViewEditPaymentAdapter? = null
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewEditPaymentBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        listenForUpdates()

        return binding.root
    }

    private fun listenForUpdates() {
        listenerRegistration = firestore.collection("paymentMethods")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val items = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val service = doc.getString("services") ?: return@mapNotNull null
                    val iban = doc.getString("iban") ?: ""
                    val name = doc.getString("name") ?: ""
                    Triple(id, service, listOf(iban, name)) // keep format similar to adapter
                }

                adapter = ViewEditPaymentAdapter(items, firestore)
                binding.recyclerView.adapter = adapter
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}
