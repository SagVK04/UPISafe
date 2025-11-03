package com.example.upisafe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.upisafe.databinding.ActivityUserHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class activity_user_history : AppCompatActivity() {
    private lateinit var bind: ActivityUserHistoryBinding
    private val transactionList = mutableListOf<TransactionModel>()

    private lateinit var auth : FirebaseAuth

    override fun onStart() {
        super.onStart()
        val cur_session : FirebaseUser? = auth.currentUser
        val headerView_1 = bind.navViewDashNew.getHeaderView(0)
        val displayname : TextView = headerView_1.findViewById(R.id.header_name_2)
        val displaymail : TextView = headerView_1.findViewById(R.id.header_mail_2)
        if (cur_session != null) {
            val email = cur_session.email
            val email_new = email?.split("@")
            displayname.setText("Hi ${email_new?.get(0)} !")
            displayname.textSize = 25F
            displaymail.setText(email)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityUserHistoryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        auth = FirebaseAuth.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.openDrawer1.setOnClickListener {
            bind.main.openDrawer(GravityCompat.START)
        }

        bind.navViewDashNew.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_user_dashboard_2 -> {
                    val intent = Intent(this, activity_user_dashboard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.go_info -> {
                        val intent = Intent(this, upisafe_info::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                }
                R.id.nav_about_us_2 -> {
                    Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        // Get intent extras
        val amount = intent.getStringExtra("amount") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val platform = intent.getStringExtra("platform") ?: ""
        val result = intent.getStringExtra("result") ?:""

        // Add to list
        if (result.isNotEmpty()) {
            transactionList.add(TransactionModel(amount, date, time, platform, result))
            // Setup RecyclerView
            bind.rvTransactions.layoutManager = LinearLayoutManager(this)
            bind.rvTransactions.adapter = TransactionAdapter(transactionList)
        }
        bind.rvTransactions.visibility = View.VISIBLE
        bind.defaultText.visibility = View.GONE
    }
}