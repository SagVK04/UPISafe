package com.example.upisafe
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.upisafe.databinding.ActivityAboutUsBinding
import com.google.android.material.internal.TextScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class activity_about_us : AppCompatActivity() {
    private val bind : ActivityAboutUsBinding by lazy{
        ActivityAboutUsBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        val user : FirebaseUser? = auth.currentUser
        val head = bind.navViewAbout.getHeaderView(0)
        val name_display : TextView = head.findViewById(R.id.header_name_1);
        val email_display : TextView = head.findViewById(R.id.header_mail_1);
        if(user != null){
            val email_in = user.email
            val email_to_name = email_in?.split("@")?.get(0)
            name_display.setText("Hi ${email_to_name}")
            name_display.textSize = 25F
            email_display.setText(email_in)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        setSupportActionBar(bind.topAppBarabout)
        bind.topAppBarabout.setNavigationOnClickListener {
            bind.main2.openDrawer(GravityCompat.START)
        }
        bind.navViewAbout.setNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.nav_user_dashboard_2 ->{
                    val intent = Intent(this,activity_user_dashboard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.go_info_about ->{
                    val intent = Intent(this, upisafe_info::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.go_history ->{
                    val intent = Intent(this, activity_user_history::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
}