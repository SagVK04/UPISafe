package com.example.upisafe

import android.app.StatusBarManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.upisafe.databinding.ActivityUpisafeInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class upisafe_info : AppCompatActivity() {
    private val bind: ActivityUpisafeInfoBinding by lazy {
        ActivityUpisafeInfoBinding.inflate(layoutInflater)
    }

    private lateinit var auth : FirebaseAuth

    override fun onStart() {
        super.onStart()
        val cur_ses : FirebaseUser? = auth.currentUser
        val head = bind.navViewInfo.getHeaderView(0)
        val displayname : TextView = head.findViewById(R.id.header_name_1)
        val displaymail : TextView = head.findViewById(R.id.header_mail_1)
        if (cur_ses != null) {
            val email = cur_ses.email
            val email_new = email?.split("@")
            displayname.setText("Hi ${email_new?.get(0)} !")
            displayname.textSize = 25F
            displaymail.setText(email)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        auth = FirebaseAuth.getInstance()

        // Set up the toolbar as the action bar and add a listener to open the drawer
        setSupportActionBar(bind.topAppBar)
        bind.topAppBar.setNavigationOnClickListener {
            bind.main1.openDrawer(GravityCompat.START)
        }

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(bind.main1) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.navViewInfo.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_user_dashboard -> {
                    val intent = Intent(this,activity_user_dashboard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.nav_details_hist_1 -> {
                    val intent = Intent(this, activity_user_history::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.nav_about_us_1 -> {
                    val intent = Intent(this, activity_about_us::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
}

