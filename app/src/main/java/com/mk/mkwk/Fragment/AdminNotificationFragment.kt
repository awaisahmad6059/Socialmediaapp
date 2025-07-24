package com.mk.mkwk.Fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.AdminNotificationSentActivity
import com.mk.mkwk.R
import com.mk.mkwk.AdminUsersNotificationAdapter
import com.mk.mkwk.NotificationUser

class AdminNotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: AdminUsersNotificationAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val allUsers = mutableListOf<NotificationUser>()
    private val displayedUsers = mutableListOf<NotificationUser>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_notification, container, false)

        recyclerView = view.findViewById(R.id.rvUsers)
        searchBar = view.findViewById(R.id.etSearch)

        setupRecyclerView()
        setupSearch()
        fetchUsers()

        return view
    }

    private fun setupRecyclerView() {
        adapter = AdminUsersNotificationAdapter(displayedUsers) { user ->
            val intent = Intent(requireContext(), AdminNotificationSentActivity::class.java).apply {
                putExtra("username", user.username)
                putExtra("userId", user.userId) // Make sure NotificationUser has userId field
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s?.toString() ?: "")
            }
        })
    }

    private fun fetchUsers() {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                allUsers.clear()
                for (document in result) {
                    allUsers.add(NotificationUser(
                        document.getString("username") ?: "Unknown",
                        document.getString("userType") ?: "Unknown",
                        document.id // This is the userId

                    ))
                }
                filterUsers("")
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun filterUsers(query: String) {
        displayedUsers.clear()
        if (query.isEmpty()) {
            displayedUsers.addAll(allUsers)
        } else {
            val lowerCaseQuery = query.lowercase()
            displayedUsers.addAll(allUsers.filter {
                it.username.lowercase().contains(lowerCaseQuery) ||
                        it.userType.lowercase().contains(lowerCaseQuery)
            })
        }
        adapter.notifyDataSetChanged()
    }
}