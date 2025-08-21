package com.example.upisafe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.upisafe.databinding.ActivityUserDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class activity_user_dashboard : AppCompatActivity() {
    private val bind: ActivityUserDashboardBinding by lazy {
        ActivityUserDashboardBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    val url = "https://upisafe-flask-giuq.onrender.com"
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
        setUpSpinner()

        bind.btnCheckFraud.setOnClickListener {
            checkFraud()
        }
        bind.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,activity_login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        bind.clear.setOnClickListener{
            bind.etAmount.setText("")
            bind.etDate.setText("")
            bind.etTime.setText("")
            bind.actvType.setText("")
            bind.tvResult.setText("")
            bind.btnCheckFraud.isEnabled = true
        }
    }

    private fun checkFraud() {
        val amount = bind.etAmount.text.toString()
        val date = bind.etDate.text.toString()
        val time = bind.etTime.text.toString()
        bind.btnCheckFraud.isEnabled = false
        bind.clear.isEnabled = false
        if(amount.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            bind.btnCheckFraud.isEnabled = true
            bind.clear.isEnabled = true
        }
        else{
            val finamt = amount.toInt()
            val findate = date.replace("/","").toInt()
            val fintime = time.replace(":","").toInt()
            checkFraud_1(finamt,findate,fintime)
        }
    }

    private fun checkFraud_1(finamt: Int, findate: Int, fintime: Int) {
        val str = object : StringRequest(Method.POST,url,
            Response.Listener<String>(){response->
                try {
                    val json_obj = JSONObject(response)
                    val pred = json_obj.getString("Fraud Possibility")
                    if(pred == "[1]")
                        bind.tvResult.setText("Possibly Fraud!")
                    else
                        bind.tvResult.setText("Possibly Safe!")
                    bind.btnCheckFraud.isEnabled = true
                }catch(e: Exception){
                    Toast.makeText(this, "Exception occurs", Toast.LENGTH_SHORT).show()
                }
                finally {
                    bind.clear.isEnabled = true
                }
            },
            Response.ErrorListener(){
                checkFraud_1(finamt,findate,fintime)
            }
        ){
            override fun getParams(): Map<String,String> {
                val params  = HashMap<String,String>()
                params.put("amount",finamt.toString())
                params.put("date",findate.toString())
                params.put("time",fintime.toString())

                return params
            }
        }
        val req_que = Volley.newRequestQueue(this)
        req_que.add(str)
    }

    private fun setUpSpinner() {
        val plat = listOf("GPay", "Paytm", "PhonePe", "BharatPe", "PayPal", "Others")
        val plat_adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,plat)
        bind.actvType.setAdapter(plat_adapter)
    }
}