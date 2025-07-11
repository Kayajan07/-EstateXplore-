package com.example.estatexplore5

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextAnswer: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var buttonResetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextAnswer = findViewById(R.id.editTextAnswer)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        buttonResetPassword = findViewById(R.id.buttonResetPassword)

        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val answer = editTextAnswer.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()

            if (email.isEmpty() || answer.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("user").document(email)

            docRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val correctAnswer = document.getString("secretAnswer")
                    if (answer.equals(correctAnswer, ignoreCase = true)) {
                        docRef.update("password", newPassword)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Incorrect answer", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No user found with that email", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error accessing database", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
