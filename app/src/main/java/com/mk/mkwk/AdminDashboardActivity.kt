package com.mk.mkwk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mk.mkwk.Fragment.AdminDashboardFragment
import com.mk.mkwk.Fragment.AdminNotificationFragment
import com.mk.mkwk.Fragment.AdminServiceFragment
import com.mk.mkwk.Fragment.UserManagementFragment

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val adminId = intent.getStringExtra("adminId")
        Toast.makeText(this, "Logged in as Admin: $adminId", Toast.LENGTH_SHORT).show()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        loadFragment(AdminDashboardFragment(), false)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(AdminDashboardFragment(), true)
                R.id.user -> loadFragment(UserManagementFragment(), true)
                R.id.service -> loadFragment(AdminServiceFragment(), true)
                R.id.notification -> loadFragment(AdminNotificationFragment(), true)
            }
            true
        }

        // ✅ Modern back handling using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

                if (currentFragment is UserManagementFragment ||
                    currentFragment is AdminServiceFragment ||
                    currentFragment is AdminNotificationFragment
                ) {
                    loadFragment(AdminDashboardFragment(), false)
                } else if (currentFragment is AdminDashboardFragment) {
                    finish() // exit the app
                } else {
                    // fallback behavior if unknown fragment
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun loadFragment(fragment: Fragment, withAnimation: Boolean) {
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
