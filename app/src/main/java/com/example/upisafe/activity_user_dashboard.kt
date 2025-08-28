package com.example.upisafe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.upisafe.databinding.ActivityUserDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        textwatcher()

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

    private fun textwatcher() {
        val textWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // This method is intentionally left empty.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bind.tvResult.setText("")

            }

        }
        bind.etAmount.addTextChangedListener(textWatcher)
        bind.etDate.addTextChangedListener(textWatcher)
        bind.etTime.addTextChangedListener(textWatcher)
        bind.actvType.addTextChangedListener(textWatcher)
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
            if(finamt<=0){
                Toast.makeText(this, "Transaction Amount can't be Zero or Negative", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }
            val timepart = time.split(":")
            if(timepart.size !=2){
                Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = false
                bind.clear.isEnabled = false
            }
            val hour = timepart[0].toInt()
            val min = timepart[1].toInt()
            if (hour !in 0..23 || min !in 0..59) {
                Toast.makeText(this, "Invalid time! Hours must be 00-23, Minutes 00-59", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }

            val today : Calendar = Calendar.getInstance()

            val dToday : Date = today.time

            val dInput_1 = date.split("/")
            if(dInput_1.size != 3){
                Toast.makeText(this, "Invalid Date Format!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }

            var dInput_1_day = 0
            if(
                dInput_1[0] == "00" || (dInput_1[0] >"31" && dInput_1[1] != "02") || ((dInput_1[0] >"31" && dInput_1[1] != "2"))
                || (dInput_1[0] >="30" && dInput_1[1] == "2") || (dInput_1[0] >="30" && dInput_1[1] == "02")){
                Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }
            dInput_1_day = dInput_1[0].toInt()

            var dInput_1_month = 0
            if(dInput_1[1] == "00" || dInput_1[1] >= "12"){
                Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }
            dInput_1_month = dInput_1[1].toInt()

            var dInput_1_year = 0
            if(dInput_1[2] == "0000"){
                Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }
            dInput_1_year = dInput_1[2].toInt()

            val dInput : Date = DateHelper.getDate(dInput_1_day,dInput_1_month,dInput_1_year,hour,min)

            if(dInput.after(dToday)){
                Toast.makeText(this, "Date or Time can't be in Future!", Toast.LENGTH_SHORT).show()
                bind.btnCheckFraud.isEnabled = true
                bind.clear.isEnabled = true
                return
            }

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
                    val pred = json_obj.getString("fraud_score")
                    if(pred < 50.toString()) {
                        if (pred < 25.toString())
                            bind.tvResult.setText("Almost Safe! [Risk Score: ${pred}%]")
                        else
                            bind.tvResult.setText("Maybe Fraud! [Risk Score: ${pred}%]")
                    }
                    else
                        bind.tvResult.setText("Almost Fraud! [Risk Score: ${pred}%]")
                    bind.btnCheckFraud.isEnabled = true
                }catch(e: Exception){
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                    bind.tvResult.setText("Error! ❌")
                    bind.btnCheckFraud.isEnabled = true
                    bind.clear.isEnabled = true
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