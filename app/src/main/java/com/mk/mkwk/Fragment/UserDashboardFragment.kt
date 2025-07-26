package com.mk.mkwk.Fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mk.mkwk.LoginActivity
import com.mk.mkwk.Notification
import com.mk.mkwk.NotificationAdapter
import com.mk.mkwk.R
import com.mk.mkwk.UserNewOrderActivity

class UserDashboardFragment : Fragment() {

    private var userId: String? = null
    private lateinit var userName: TextView
    private lateinit var balanceText: TextView
    private lateinit var requestMaintenanceLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var notificationListener: ListenerRegistration? = null
    private val notifications = mutableListOf<Notification>()
    private lateinit var adapter: NotificationAdapter
    private var coinsListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_dashboard, container, false)

        userId = arguments?.getString("userId")
        userName = view.findViewById(R.id.user_name)
        balanceText = view.findViewById(R.id.balance)
        requestMaintenanceLayout = view.findViewById(R.id.requestmaintanace)
        recyclerView = view.findViewById(R.id.recycler_view)

        // Setup RecyclerView
        adapter = NotificationAdapter(notifications) { notification ->
            showNotificationDetail(notification)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        requestMaintenanceLayout.setOnClickListener {
            if (userId != null) {
                checkBalanceBeforeOrder()
            } else {
                Toast.makeText(requireContext(), "User ID not available", Toast.LENGTH_SHORT).show()
            }
        }

        val logoutBtn = view.findViewById<ImageView>(R.id.sign_out_icon)
        logoutBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // Perform logout
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Just dismiss the dialog
                }
                .setCancelable(false)
                .show()
        }


        if (userId != null) {
            loadUserProfile(userId!!)
            loadUserCoins(userId!!)
            setupNotificationListener(userId!!)
        } else {
            Log.e("UserDashboardFragment", "User ID is null")
        }

        return view
    }

    private fun checkBalanceBeforeOrder() {
        firestore.collection("coins").document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                val currentCoins = document.getLong("coins") ?: 0
                if (currentCoins < 0) {
                    Toast.makeText(
                        requireContext(),
                        "You need to add balance to place an order. Current balance: $currentCoins",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val intent = Intent(requireContext(), UserNewOrderActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to check balance: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadUserCoins(userId: String) {
        coinsListener?.remove()

        coinsListener = firestore.collection("coins").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserDashboardFragment", "Coins listener error", error)
                    balanceText.text = "Error"
                    balanceText.setTextColor(Color.GRAY)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val coins = snapshot.getLong("coins") ?: 0
                    balanceText.text = "$coins"

                    if (coins < 1) {
                        balanceText.setTextColor(Color.RED)
                    } else {
                        balanceText.setTextColor(Color.GREEN)
                    }
                } else {
                    balanceText.text = "0"
                    balanceText.setTextColor(Color.RED)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        userId?.let { setupNotificationListener(it) }
    }

    override fun onPause() {
        super.onPause()
        notificationListener?.remove()
        coinsListener?.remove()
    }

    private fun setupNotificationListener(userId: String) {
        notificationListener = firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("UserDashboard", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    if (snapshots.size() > 10) {
                        val docsToDelete = snapshots.documents
                            .sortedBy { it.getLong("timestamp") ?: 0 } // Sort by timestamp ASC
                            .take(snapshots.size() - 10) // Get extra notifications beyond 10

                        for (doc in docsToDelete) {
                            doc.reference.delete()
                        }
                    }

                    // 🔁 Now update UI with latest (up to 10)
                    notifications.clear()
                    snapshots.documents
                        .take(10) // Only keep latest 10
                        .forEach { document ->
                            val notification = Notification(
                                id = document.id,
                                title = document.getString("title") ?: "",
                                message = document.getString("message") ?: "",
                                timestamp = document.getLong("timestamp") ?: 0,
                                read = document.getBoolean("read") ?: false
                            )
                            notifications.add(notification)

                            // Mark as read
                            if (!notification.read) {
                                document.reference.update("read", true)
                            }
                        }

                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun showNotificationDetail(notification: Notification) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_notification_detail, null)

        val titleTextView = dialogView.findViewById<TextView>(R.id.notificationTitleTextView)
        val messageTextView = dialogView.findViewById<TextView>(R.id.notificationMessageTextView)

        titleTextView.text = notification.title
        messageTextView.text = notification.message
        messageTextView.movementMethod = LinkMovementMethod.getInstance()

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName.text = document.getString("username") ?: "Unknown User"
                } else {
                    Log.d("UserDashboardFragment", "No user profile found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDashboardFragment", "Error loading user profile", e)
            }
    }
}