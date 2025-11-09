package com.example.upisafe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.example.upisafe.databinding.ActivitySplashScreenBinding
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class activity_splash_screen : AppCompatActivity() {
    private val bind : ActivitySplashScreenBinding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }
    private val url = "https://upisafe-flask-2-3.onrender.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkServerConnection()
    }
    private fun checkServerConnection() {
        // Create a simple GET request to the base URL
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val return_res = JSONObject(response)
                val result = return_res.getString("Request_Result")
                val action = return_res.getString("Next_Actions")
                // Get the response body content,
                // assuming a successful connection to the Flask server.
                //Toast.makeText(this, "Server connected successfully!\n" +
                //        "${result}\n"+"${action}",Toast.LENGTH_SHORT).show()
                // Proceed to the main activity
                bind.root.postDelayed({
                    navigateToDashboard()
                },5000)
            },
            { error ->
                // This block executes if there's an error (e.g., no internet, server not running)
                Toast.makeText(this, "Connection failed. Retrying...: ${error.message}", Toast.LENGTH_LONG).show()
                // Implement a simple retry mechanism after a delay (e.g., 3 seconds)
                bind.root.postDelayed({
                    checkServerConnection()
                }, 5000) // 3-second delay before retry
            }
        )
        Volley.newRequestQueue(this).add(stringRequest)
    }
}

private fun activity_splash_screen.navigateToDashboard() {
    val intent = Intent(this, activity_user_dashboard::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}
