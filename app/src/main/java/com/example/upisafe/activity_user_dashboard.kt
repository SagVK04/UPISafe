package com.example.upisafe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.upisafe.databinding.ActivityUserDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import org.json.JSONObject
import java.util.Calendar
import java.util.Date

class activity_user_dashboard : AppCompatActivity() {
    private val bind: ActivityUserDashboardBinding by lazy {
        ActivityUserDashboardBinding.inflate(layoutInflater)
    }
    private val url_1 = "https://upisafebackend.onrender.com/"
    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference // <-- Add this variable

    override fun onStart() {
        super.onStart()
        val cur_session : FirebaseUser? = auth.currentUser
        val headerView = bind.navViewDash.getHeaderView(0)
        val displayname : TextView = headerView.findViewById(R.id.header_name)
        val displaymail : TextView = headerView.findViewById(R.id.header_mail)
        if (cur_session != null) {
            val email = cur_session.email
            val email_new = email?.split("@")
            displayname.setText("Hi ${email_new?.get(0)} !")
            displayname.textSize = 25F
            displaymail.setText(email)
        }
    }

    val url = "https://upisafe-flask-2-3.onrender.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        hideLoading()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTransparentStatusBar()
        setContentView(bind.root)

        // Fix: Apply the system bar padding to the content ScrollView instead of the main DrawerLayout.
        // The DrawerLayout itself handles its own insets, and this ensures the ScrollView content starts below the status bar.
        ViewCompat.setOnApplyWindowInsetsListener(bind.maindraw) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.opendrawer.setOnClickListener {
            bind.maindraw.openDrawer(GravityCompat.START)
        }
        bind.navViewDash.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_what_is_fraud -> {
                    val intent = Intent(this,upisafe_info::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.details_hist -> {
                    val intent = Intent(this, activity_user_history::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                R.id.nav_about_us -> {
                    val intent = Intent(this, activity_about_us::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        setTransparentStatusBar()
        tran_type()

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
        bind.btnClear.setOnClickListener{
            bind.etAmount.setText("")
            bind.etDate.setText("")
            bind.etTime.setText("")
            bind.acupiId.setText("")
            bind.actvTranType.setText("")
            bind.tvResult.setText("")
            bind.btnCheckFraud.isEnabled = true
        }
    }

    private fun setTransparentStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private fun checkFraud() {
        showLoading()
        val amount = bind.etAmount.text.toString()
        val date = bind.etDate.text.toString()
        val time = bind.etTime.text.toString()
        val id = bind.acupiId.text.toString()
        val type = bind.actvTranType.text.toString()
        bind.btnCheckFraud.isEnabled = false
        bind.btnClear.isEnabled = false
        if(amount.isEmpty() || date.isEmpty() ||
            time.isEmpty() || id.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            hideLoading()
            return
        }
        else{
            val finamt = amount.toInt()
            if(finamt<=0){
                Toast.makeText(this, "Transaction Amount can't be Zero or Negative", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            if(!time.contains(":")) {
                Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            val timepart = time.split(":")
            if(timepart.size !=2){
                Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            val hour = timepart[0].toInt()
            val min = timepart[1].toInt()

            if (type == "Debited" && hour !in 0..23 || min !in 0..59) {
                Toast.makeText(this, "Invalid time! Hours must be 00-23, Minutes 00-59", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            val today : Calendar = Calendar.getInstance()

            val dToday : Date = today.time

            val dInput_1 = date.split("/")
            if(dInput_1.size != 3){
                Toast.makeText(this, "Invalid Date Format!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }

            var dInput_1_day = 0
            if(
                (dInput_1[0].toInt() == 0) ||
                (dInput_1[0].toInt() > 31 && dInput_1[1].toInt() != 2) ||
                (dInput_1[0].toInt() > 30 && dInput_1[1].toInt() == 2)
                ){
                    Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                    hideLoading()
                    return
            }
            dInput_1_day = dInput_1[0].toInt()

            var dInput_1_month = 0
            if(dInput_1[1].toInt() == 0 || dInput_1[1].toInt() > 12){
                Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            dInput_1_month = dInput_1[1].toInt()

            var dInput_1_year = 0
            if(dInput_1[2].toInt() == 0 || dInput_1[2].length<4){
                Toast.makeText(this, "Invalid Date Format or Value!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            dInput_1_year = dInput_1[2].toInt()

            val dInput : Date = DateHelper.getDate(dInput_1_day,dInput_1_month,dInput_1_year,hour,min)

            if(dInput.after(dToday)){
                Toast.makeText(this, "Date or Time can't be in Future!", Toast.LENGTH_SHORT).show()
                hideLoading()
                return
            }
            val fintime = hour.toString()
            checkFraud_1(amount,date,time,id,type)
        }
    }

    private fun checkFraud_1(amt: String, date: String, time: String, id: String, type: String) {
        var pred_result = ""
        val str = object : StringRequest(Method.POST,url,
            Response.Listener<String>(){response->
                try {
                    val json_obj = JSONObject(response)
                    val pred = json_obj.getString("Fraud_Result")
                    val pred_score = json_obj.getString("Risk_Score")
                    if(pred.equals("False"))    pred_result = "Safe"
                    if(pred.equals("True"))    pred_result = "Fraud"
                    if(pred.equals("False")) {
                        bind.tvResult.setText("Predicted Result: Safe! [Risk Score: ${pred_score}%]")
                        bind.tvResult.setTextColor(Color.CYAN)
                    }
                    else {
                        bind.tvResult.setText("Predicted Result: Fraud! [Risk Score: ${pred_score}%]")
                        bind.tvResult.setTextColor(Color.RED)
                    }
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Create the history object
                        val historyEntry = TransactionModel(
                            amount = bind.etAmount.text.toString(),
                            date = bind.etDate.text.toString(),
                            time = bind.etTime.text.toString(),
                            id = bind.acupiId.text.toString(),
                            tr_type = bind.actvTranType.text.toString(),
                            result = pred_result,
                            riskScore = "${pred_score}%"
                            // timestamp is auto-generated by the data class
                        )

                        // Create a new unique key for this transaction
                        // This saves it under /histories/{userId}/{newTransactionId}
                        val newTransactionKey = database.child("histories").child(userId).push().key

                        if (newTransactionKey != null) {
                            database.child("histories").child(userId).child(newTransactionKey)
                                .setValue(historyEntry)
                                .addOnSuccessListener {
                                    Toast.makeText(this@activity_user_dashboard, "History Saved", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // Optional: Log or show the error
                                    Toast.makeText(
                                        this@activity_user_dashboard,
                                        "Failed to save history: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    hideLoading()
                }catch(e: Exception){
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                    bind.tvResult.setText("Error!❌")
                    hideLoading()
                }
            },
            Response.ErrorListener(){
                showLoading()
                checkFraud_1(amt,date,time,id,type)
            }
        ){
            override fun getParams(): Map<String,String> {
                val params  = HashMap<String,String>()
                params.put("upi_id",id)
                params.put("date",date)
                params.put("time",time)
                params.put("amount",amt)
                params.put("type",type)
                return params
            }
        }
        val req_que = Volley.newRequestQueue(this)
        req_que.add(str)
    }

    //All Dropdown Items
    private fun tran_type(){
        val dev_type = listOf("Requested","Debited")
        val dev_adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,dev_type)
        bind.actvTranType.setAdapter(dev_adapter)
    }

    private fun showLoading() {
        bind.loadingLayout.visibility = View.VISIBLE
        bind.btnCheckFraud.isEnabled = false
        bind.btnClear.isEnabled = false
    }

    private fun hideLoading() {
        bind.loadingLayout.visibility = View.GONE
        bind.btnCheckFraud.isEnabled = true
        bind.btnClear.isEnabled = true
    }
}
