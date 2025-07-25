package com.mk.mkwk

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class UserOrderHistoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_history_detail)

        val order = intent.getSerializableExtra("order") as? Order

        order?.let {
            findViewById<TextView>(R.id.usernameTextView).text = it.username
            findViewById<TextView>(R.id.useridTextView).text = it.userId
            findViewById<TextView>(R.id.categoryTextView).text = it.category
            findViewById<TextView>(R.id.descriptionTextView).text = it.description
            findViewById<TextView>(R.id.LinkTextView).text = it.link
            findViewById<TextView>(R.id.serviceByTextView).text = it.serviceBy
            findViewById<TextView>(R.id.amountTextView).text = it.amount
            findViewById<TextView>(R.id.chargeTextView).text = it.charge
            findViewById<TextView>(R.id.statusTextView).text = it.status
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }
}
