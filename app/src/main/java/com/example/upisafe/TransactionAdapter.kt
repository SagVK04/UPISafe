package com.example.upisafe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactionList: List<TransactionModel>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val upi_id: TextView = itemView.findViewById(R.id.rec_id)
        val trans_type: TextView = itemView.findViewById(R.id.tr_type)
        val  tvresult: TextView = itemView.findViewById(R.id.tvResult1)

        val res_score: TextView = itemView.findViewById(R.id.tvResult_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.tvAmount.text = "Amount: ₹${transaction.amount}"
        holder.tvDate.text = "Date: ${transaction.date}"
        holder.tvTime.text = "Time: ${transaction.time}"
        holder.upi_id.text = "Receiver's UPI Id: ${transaction.id}"
        holder.trans_type.text = "Transaction Type: ${transaction.tr_type}"
        holder.tvresult.text = "Result: ${transaction.result}"
        holder.res_score.text = "Risk Score: ${transaction.riskScore}"
        holder.tvAmount.textSize = 20F
        holder.tvDate.textSize = 20F
        holder.tvTime.textSize = 20F
        holder.upi_id.textSize = 20F
        holder.trans_type.textSize = 20F
        holder.tvresult.textSize = 20F
        holder.res_score.textSize = 20F
    }

    override fun getItemCount(): Int = transactionList.size
}
