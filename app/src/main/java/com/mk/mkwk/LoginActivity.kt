package com.mk.mkwk

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var whatsapp: ImageView
    private lateinit var tvSignUp: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_sign_in)
        whatsapp = findViewById(R.id.iv_whatsapp)
        tvSignUp = findViewById(R.id.tv_sign_up)
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnLogin.setOnClickListener {
            loginUser()
        }
        whatsapp.setOnClickListener {
            fetchAndOpenWhatsAppNumber()
        }
    }

    private fun fetchAndOpenWhatsAppNumber() {
        val loadingDialog = ProgressDialog(this).apply {
            setMessage("Fetching WhatsApp contact...")
            setCancelable(false)
            show()
        }

        val contactDocRef = firestore.collection("contact").document("admin_contact")

        contactDocRef.get()
            .addOnSuccessListener { document ->
                loadingDialog.dismiss()
                if (document.exists()) {
                    val number = document.getString("whatsappNumber")
                    if (!number.isNullOrEmpty()) {
                        showWhatsAppDialog(number)
                    } else {
                        Toast.makeText(this, "WhatsApp number not set", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Contact document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "Failed to fetch WhatsApp number", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showWhatsAppDialog(number: String) {
        if (isFinishing || isDestroyed) return  // prevent window leak

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Contact via WhatsApp")
        builder.setMessage("WhatsApp Number: $number")

        builder.setPositiveButton("Chat") { _, _ ->
            openWhatsApp(number)
        }

        builder.setNegativeButton("Copy") { dialog, _ ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("WhatsApp Number", number)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Number copied to clipboard", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Check again before showing dialog
        val dialog = builder.create()
        if (!isFinishing && !isDestroyed) {
            dialog.show()
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


    private fun loginUser() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = mAuth.currentUser?.uid ?: return@addOnSuccessListener

                firestore.collection("admins").document(userId).get()
                    .addOnSuccessListener { adminDoc ->
                        if (adminDoc.exists()) {
                            progressDialog.dismiss()
                            val intent = Intent(this, AdminDashboardActivity::class.java)
                            intent.putExtra("adminId", userId)
                            startActivity(intent)
                            finish()
                        } else {
                            // If not found in admins, check in users
                            firestore.collection("users").document(userId).get()
                                .addOnSuccessListener { userDoc ->
                                    progressDialog.dismiss()
                                    if (userDoc.exists()) {
                                        val intent = Intent(this, UserDashboardActivity::class.java)
                                        intent.putExtra("userId", userId)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "User not found in any collection", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
