package com.example.upisafe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject
import java.util.Calendar
import java.util.Date

class activity_user_dashboard : AppCompatActivity() {
    private val bind: ActivityUserDashboardBinding by lazy {
        ActivityUserDashboardBinding.inflate(layoutInflater)
    }

    private lateinit var auth : FirebaseAuth

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

    var res_calculate = 0
    var in_amt = 0
    var in_time = 0
    var in_date = 0

    var in_result = ""

    private fun setIntentVar(finamt: Int, findate: Int, fintime: Int, pred_res: String){
        in_amt = finamt; in_date = findate; in_amt = fintime; in_result = pred_res
    }

    val url = "https://upisafe-flask-giuq.onrender.com"
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
                    if(res_calculate == 1){
                        val intent = Intent(this, activity_user_history::class.java)
                        intent.putExtra("amount", in_amt.toString())
                        intent.putExtra("date", in_date.toString())
                        intent.putExtra("time", bind.etTime.text.toString())
                        intent.putExtra("platform", bind.actvType.text.toString())
                        intent.putExtra("result",in_result)
                        startActivity(intent)
                        finish()
                    }
                    else if(res_calculate == 0){
                        val intent = Intent(this, activity_user_history::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
                R.id.nav_about_us -> {
                    Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        auth = FirebaseAuth.getInstance()

        setTransparentStatusBar()
        setUpSpinner()
        textwatcher()

        bind.btnCheckFraud.setOnClickListener {
            checkFraud()
        }
        bind.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        bind.btnClear.setOnClickListener{
            bind.etAmount.setText("")
            bind.etDate.setText("")
            bind.etTime.setText("")
            bind.actvType.setText("")
            bind.tvResult.setText("")
            bind.btnCheckFraud.isEnabled = true
        }
    }

    private fun setTransparentStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
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
        showLoading()
        val amount = bind.etAmount.text.toString()
        val date = bind.etDate.text.toString()
        val time = bind.etTime.text.toString()
        bind.btnCheckFraud.isEnabled = false
        bind.btnClear.isEnabled = false
        if(amount.isEmpty() || date.isEmpty() || time.isEmpty()) {
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
            if (hour !in 0..23 || min !in 0..59) {
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

            val findate = date.replace("/","").toInt()
            val fintime = time.replace(":","").toInt()
            checkFraud_1(finamt, findate, fintime)
        }
    }

    private fun checkFraud_1(finamt: Int, findate: Int, fintime: Int) {
        var pred_result: String = ""
        val str = object : StringRequest(Method.POST,url,
            Response.Listener<String>(){response->
                try {
                    val json_obj = JSONObject(response)
                    val pred = json_obj.getString("fraud_result")
                    val pred_score = json_obj.getString("fraud_score")
                    if(pred.equals("0"))    pred_result = "Safe"
                    if(pred.equals("1"))    pred_result = "Fraud"
                    res_calculate = 1
                    if(pred.equals("0")) {
                        bind.tvResult.setText("Predicted Result: Safe! [Risk Score: ${pred_score}%]")
                        bind.tvResult.setTextColor(Color.CYAN)
                    }
                    else {
                        bind.tvResult.setText("Predicted Result: Fraud! [Risk Score: ${pred_score}%]")
                        bind.tvResult.setTextColor(Color.RED)
                    }
                    setIntentVar(finamt,findate,fintime,pred_result)
                    hideLoading()
                }catch(e: Exception){
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                    bind.tvResult.setText("Error!❌")
                    hideLoading()
                }
            },
            Response.ErrorListener(){
                showLoading()
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
