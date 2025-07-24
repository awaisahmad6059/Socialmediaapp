// PaymentPagerAdapter.kt
package com.mk.mkwk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mk.mkwk.Fragment.AddPaymentFragment
import com.mk.mkwk.Fragment.ViewEditPaymentFragment
import com.mk.mkwk.Fragment.ShowPaymentFragment
import com.mk.mkwk.Fragment.DeletePaymentFragment

class PaymentPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShowPaymentFragment()
            1 -> ViewEditPaymentFragment()
            2 -> AddPaymentFragment()
            3 -> DeletePaymentFragment()
            else -> ShowPaymentFragment()
        }
    }
}
