package com.example.upisafe

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.upisafe.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class login : AppCompatActivity() {
    private val bind : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    override fun onStart() {
        super.onStart()
        val cur_session:FirebaseUser? = auth.currentUser
        if(cur_session != null){
            val intent = Intent(this,activity_user_dashboard::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bind.root)
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bind.btnLogin.setOnClickListener {
            val email = bind.etEmail.text.toString()
            val pass = bind.etPassword.text.toString()
            if(email.isEmpty() || pass.isEmpty())
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show()
            else{
                auth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, activity_user_dashboard::class.java))
                            finish()
                        }
                        else{
                            Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        bind.tvGoToRegister.setOnClickListener{
            val intent = Intent(this,activity_register::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}