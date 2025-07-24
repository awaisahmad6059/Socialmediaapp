package com.mk.mkwk.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.AdminAddServiceActivity
import com.mk.mkwk.R
import com.mk.mkwk.Service
import com.mk.mkwk.ServiceAdapter

class AdminServiceFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private val serviceList = mutableListOf<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_service, container, false)

        recyclerView = view.findViewById(R.id.serviceRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        serviceAdapter = ServiceAdapter(serviceList)
        recyclerView.adapter = serviceAdapter

        view.findViewById<FloatingActionButton>(R.id.addServiceButton).setOnClickListener {
            startActivity(Intent(requireContext(), AdminAddServiceActivity::class.java))
        }

        fetchServicesFromFirestore()

        return view
    }

    private fun fetchServicesFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("arrays")
            .get()
            .addOnSuccessListener { documents ->
                serviceList.clear()
                for (doc in documents) {
                    val category = doc.getString("category") ?: continue
                    serviceList.add(Service(category))
                }
                serviceAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
