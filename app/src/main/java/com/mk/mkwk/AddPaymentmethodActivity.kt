package com.mk.mkwk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mk.mkwk.databinding.ActivityAddPaymentmethodBinding

class AddPaymentmethodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPaymentmethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaymentmethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PaymentPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Show All"
                1 -> "View/Edit"
                2 -> "Add"
                3 -> "Delete"
                else -> ""
            }
        }.attach()

    }
    fun switchToTab(position: Int) {
        binding.viewPager.currentItem = position
    }
}
