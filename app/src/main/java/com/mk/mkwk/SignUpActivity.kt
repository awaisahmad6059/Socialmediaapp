package com.mk.mkwk

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var alreadyHaveAccount: TextView
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var inputUsername: EditText
    private lateinit var btnRegister: Button
    private lateinit var back_button: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        alreadyHaveAccount = findViewById(R.id.tv_sign_in)
        inputEmail = findViewById(R.id.et_email)
        inputPassword = findViewById(R.id.et_password)
        inputConfirmPassword = findViewById(R.id.et_confirm_password)
        inputUsername = findViewById(R.id.et_username)
        btnRegister = findViewById(R.id.btn_sign_up)
        back_button = findViewById(R.id.back_button)

        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        back_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            performAuth()
        }
    }

    private fun performAuth() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()
        val confirmPassword = inputConfirmPassword.text.toString().trim()
        val username = inputUsername.text.toString().trim()

        if (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            inputEmail.error = "Enter a correct email"
            return
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword.error = "Enter a proper password"
            return
        } else if (password != confirmPassword) {
            inputConfirmPassword.error = "Passwords do not match"
            return
        }

        progressDialog.setMessage("Please wait while registering...")
        progressDialog.setTitle("Registration")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener

                    val userMap = hashMapOf(
                        "userId" to userId,
                        "email" to email,
                        "username" to username,
                        "password" to password, // 🔐 Save password (only for local/testing purposes)
                        "userType" to "User"
                    )

                    firestore.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, UserDashboardActivity::class.java)
                            intent.putExtra("userId", userId)
                            intent.putExtra("username", username)
                            intent.putExtra("userType", "User")
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
