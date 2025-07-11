package com.example.estatexplore5

import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var checkboxRemember: CheckBox
    private lateinit var textGoToRegister: TextView
    private lateinit var textForgotPassword: TextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Giriş bileşenlerini bağla
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        checkboxRemember = findViewById(R.id.checkboxRemember)
        textGoToRegister = findViewById(R.id.textGoToRegister)
        textForgotPassword = findViewById(R.id.textForgotPassword)

        // SharedPreferences başlat
        sharedPrefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Önceki giriş bilgilerini göster
        val savedEmail = sharedPrefs.getString("email", "")
        val savedPassword = sharedPrefs.getString("password", "")
        val isRemembered = sharedPrefs.getBoolean("rememberMe", false)

        if (isRemembered) {
            editTextEmail.setText(savedEmail)
            editTextPassword.setText(savedPassword)
            checkboxRemember.isChecked = true
        }

        // Kayıt sayfasına git
        textGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Şifremi unuttum sayfasına git
        textForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))

        }

        // Giriş yap
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val rememberMeChecked = checkboxRemember.isChecked

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (rememberMeChecked) {
                    sharedPrefs.edit().apply {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("rememberMe", true)
                        apply()
                    }
                } else {
                    sharedPrefs.edit().clear().apply()
                }

                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("admin")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    goToMenu("admin", email)
                } else {
                    db.collection("user")
                        .whereEqualTo("email", email)
                        .whereEqualTo("password", password)
                        .get()
                        .addOnSuccessListener { userResult ->
                            if (!userResult.isEmpty) {
                                goToMenu("user", email)
                            } else {
                                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error checking user", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking admin", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToMenu(role: String, email: String) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("role", role)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}
