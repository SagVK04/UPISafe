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
        val tvPlatform: TextView = itemView.findViewById(R.id.tvPlatform)
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
        holder.tvPlatform.text = "Platform: ${transaction.platform}"
        holder.tvresult.text = "Result: ${transaction.result}"
        holder.res_score.text = "Risk Score: ${transaction.riskScore}"
    }

    override fun getItemCount(): Int = transactionList.size
}
