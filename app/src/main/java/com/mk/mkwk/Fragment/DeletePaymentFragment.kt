package com.mk.mkwk.Fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mk.mkwk.DeletePaymentAdapter
import com.mk.mkwk.databinding.FragmentDeletePaymentBinding

class DeletePaymentFragment : Fragment() {

    private var _binding: FragmentDeletePaymentBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeletePaymentBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        listenToDataUpdates()

        return binding.root
    }

    private fun listenToDataUpdates() {
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection("paymentMethods")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val data = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val service = doc.getString("services") ?: return@mapNotNull null
                    val iban = doc.getString("iban") ?: ""
                    val name = doc.getString("name") ?: ""

                    Triple(id, "$service\n$name\n$iban", doc.reference)
                }

                binding.recyclerView.adapter = DeletePaymentAdapter(data) { docRef ->
                    docRef.delete().addOnSuccessListener {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}
