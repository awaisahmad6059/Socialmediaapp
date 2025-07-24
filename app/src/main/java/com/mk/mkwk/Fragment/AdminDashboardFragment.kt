package com.mk.mkwk.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.AddPaymentmethodActivity
import com.mk.mkwk.AdminCompleteorderActivity
import com.mk.mkwk.AdminContactSettingActivity
import com.mk.mkwk.AdminNewOrderActivity
import com.mk.mkwk.AdmincancelActivity
import com.mk.mkwk.LoginActivity
import com.mk.mkwk.R

class AdminDashboardFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuButton: ImageView
    private lateinit var totalPendingRequestTextView: TextView
    private lateinit var totalCompleteTaskTextView: TextView
    private lateinit var totalcancelTaskTextView: TextView



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

        view.findViewById<LinearLayout>(R.id.pendingrequest).setOnClickListener {
            startActivity(Intent(requireActivity(), AdminNewOrderActivity::class.java))
        }
        view.findViewById<LinearLayout>(R.id.completerequests).setOnClickListener {
            startActivity(Intent(requireActivity(), AdminCompleteorderActivity::class.java))
        }
        view.findViewById<LinearLayout>(R.id.canceltask).setOnClickListener {
            startActivity(Intent(requireActivity(), AdmincancelActivity::class.java))
        }
        view.findViewById<LinearLayout>(R.id.paymenttask).setOnClickListener {
            startActivity(Intent(requireActivity(), AddPaymentmethodActivity::class.java))
        }
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        menuButton = view.findViewById(R.id.menu_button)
        totalPendingRequestTextView = view.findViewById(R.id.total_pending_request_count)
        totalCompleteTaskTextView = view.findViewById(R.id.total_complete_order_count)
        totalcancelTaskTextView = view.findViewById(R.id.totalcanceltask)



        // Open drawer on menu click
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        // Handle nav drawer items
        navView.setNavigationItemSelectedListener(this)

        // Fetch admin username
        fetchAdminDetails(view)
        fetchTotalOrdersCount()
        fetchTotalCompleteTask()
        fetchTotalcancelTask()



        return view
    }

    private fun fetchTotalcancelTask() {
        val db = FirebaseFirestore.getInstance()
        db.collection("newOrders")
            .whereEqualTo("status", "Cancelled") // Filter to only "Pending" orders
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val totalPending = snapshot?.size() ?: 0
                totalcancelTaskTextView.text = totalPending.toString()
            }
    }

    private fun fetchTotalCompleteTask()  {
        val db = FirebaseFirestore.getInstance()
        db.collection("completeOrders")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val totalTask = snapshot?.size() ?: 0
                totalCompleteTaskTextView.text = totalTask.toString()
            }
    }

    private fun fetchTotalOrdersCount() {
        val db = FirebaseFirestore.getInstance()
        db.collection("newOrders")
            .whereEqualTo("status", "pending") // Filter to only "Pending" orders
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val totalPending = snapshot?.size() ?: 0
                totalPendingRequestTextView.text = totalPending.toString()
            }
    }


    private fun fetchAdminDetails(view: View) {
        val db = FirebaseFirestore.getInstance()
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("admins")
            .document(adminId)
            .addSnapshotListener { document, error ->
                val userNameTextView = view.findViewById<TextView>(R.id.user_name)

                if (error != null) {
                    userNameTextView.text = "Admin"
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val name = document.getString("username")
                    userNameTextView.text = name ?: "Admin"
                } else {
                    userNameTextView.text = "Admin"
                }
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.contact -> {
                startActivity(Intent(requireActivity(), AdminContactSettingActivity::class.java))
            }
            R.id.nav_signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            requireActivity().onBackPressed()
        }
    }
}
