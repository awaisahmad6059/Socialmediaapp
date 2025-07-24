package com.mk.mkwk.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.User
import com.mk.mkwk.UserAdapter
import com.mk.mkwk.databinding.FragmentUserManagementBinding

class UserManagementFragment : Fragment() {
    private lateinit var binding: FragmentUserManagementBinding
    private lateinit var userAdapter: UserAdapter
    private val users = mutableListOf<User>() // Displayed users
    private val allUsers = mutableListOf<User>() // All users from Firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView with UserAdapter
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(users)
        binding.rvUsers.adapter = userAdapter

        // Setup search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        fetchUsers()
    }

    private fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { result ->
                allUsers.clear()
                result.documents.forEach { document ->
                    allUsers.add(
                        User(
                            userId = document.id,
                            username = document.getString("username") ?: "",
                            userType = document.getString("userType") ?: ""
                        )
                    )
                }
                filterUsers("") // Show all users initially
            }
    }


    private fun filterUsers(query: String) {
        users.clear()
        if (query.isEmpty()) {
            users.addAll(allUsers)
        } else {
            users.addAll(allUsers.filter {
                it.username.contains(query, ignoreCase = true) ||
                        it.userType.contains(query, ignoreCase = true)
            })
        }
        userAdapter.notifyDataSetChanged()
    }
}