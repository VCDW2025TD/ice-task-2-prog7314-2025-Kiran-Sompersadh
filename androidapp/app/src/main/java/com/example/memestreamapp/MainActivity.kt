package com.example.memestreamapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val prefs by lazy { getSharedPreferences("memestream_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        updateUI(currentUser)

        findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            signOut()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val nameView = findViewById<TextView>(R.id.txtName)
        val emailView = findViewById<TextView>(R.id.txtEmail)
        val profileImage = findViewById<ImageView>(R.id.imgProfile)

        if (user != null) {
            nameView.text = user.displayName ?: "Unknown User"
            emailView.text = user.email ?: "No email available"

            user.photoUrl?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop() // makes it round
                    .into(profileImage)
            }
        } else {
            nameView.text = "Guest"
            emailView.text = ""
        }
    }

    private fun signOut() {
        // Sign out Firebase
        auth.signOut()

        // Sign out Google client as well
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            prefs.edit().putBoolean("biometric_enabled", false).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}