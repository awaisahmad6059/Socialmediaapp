package com.mk.mkwk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.Fragment.UserContactFragment
import com.mk.mkwk.Fragment.UserDashboardFragment
import com.mk.mkwk.Fragment.UserDepositFragment
import com.mk.mkwk.Fragment.UserOrderHistoryFragment

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val userDashboardFragment = UserDashboardFragment()
        val bundle = Bundle()
        bundle.putString("userId", userId)
        userDashboardFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userDashboardFragment) // Must match the ID in your XML
            .commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Get intent extras
        userId = intent.getStringExtra("userId")
        val userType = intent.getStringExtra("userType")
        val username = intent.getStringExtra("username")

        Toast.makeText(this, "Logged in as User: $username", Toast.LENGTH_SHORT).show()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(UserDashboardFragment(), userId, false)
        }

        // Bottom navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(UserDashboardFragment(), userId, true)
                R.id.order -> loadFragment(UserOrderHistoryFragment(), userId, true)
                R.id.funds -> loadFragment(UserDepositFragment(), userId, true)
                R.id.contact -> {
                    fetchAndOpenWhatsAppNumber()
                    false
                }

            }
            true
        }

        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

                if (currentFragment is UserOrderHistoryFragment ||
                    currentFragment is UserDepositFragment ||
                    currentFragment is UserContactFragment
                ) {
                    loadFragment(UserDashboardFragment(), userId, false)
                    bottomNavigationView.selectedItemId = R.id.home
                } else if (currentFragment is UserDashboardFragment) {
                    finish() // Exit app
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun fetchAndOpenWhatsAppNumber() {
        val firestore = FirebaseFirestore.getInstance()
        val contactDocRef = firestore.collection("contact").document("admin_contact")

        contactDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val number = document.getString("whatsappNumber")
                    if (!number.isNullOrEmpty()) {
                        openWhatsApp(number)
                    } else {
                        Toast.makeText(this, "WhatsApp number not set", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Contact document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch WhatsApp number", Toast.LENGTH_SHORT).show()
            }
    }


    private fun openWhatsApp(phoneNumber: String) {
        // Format number without "+"
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "")

        // You can also add a default message here if you want
        val message = ""
        val uri = "https://wa.me/$cleanNumber?text=${Uri.encode(message)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.whatsapp") // Ensures it opens only in WhatsApp

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "WhatsApp not installed or number not on WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }



    private fun loadFragment(fragment: Fragment, userId: String?, withAnimation: Boolean) {
        val bundle = Bundle().apply {
            putString("userId", userId)
        }
        fragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
