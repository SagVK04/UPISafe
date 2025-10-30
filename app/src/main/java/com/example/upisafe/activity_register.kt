package com.example.upisafe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.upisafe.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class activity_register : AppCompatActivity() {
    private val bind : ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        bind.btnRegister.setOnClickListener {
            val email = bind.etEmail.text.toString()
            val pass=bind.etPassword.text.toString()
            val re_pass=bind.etConfirmPassword.text.toString()
            if(email.isEmpty()||pass.isEmpty()||re_pass.isEmpty()){
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
            else{
                if(pass != re_pass){
                    Toast.makeText(this, "Password not matched", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this) {task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,activity_user_dashboard::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this, "User creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}