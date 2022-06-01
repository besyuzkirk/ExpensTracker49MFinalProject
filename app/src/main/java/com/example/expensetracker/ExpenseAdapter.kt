package com.example.expensetracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>()
{
    class ExpenseHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_layout, parent, false)
        return ExpenseHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int)
    {
        val expense = expenses[position]
        val context = holder.amount.context

        if(expense.amount >= 0){
            holder.amount.text = "+ $%.2f".format(expense.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black))
        }else {
            holder.amount.text = "- $%.2f".format(Math.abs(expense.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = expense.label


        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", expense)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return expenses.size
    }


    fun setData(expenses: List<Expense>){
        this.expenses = expenses
        notifyDataSetChanged()
    }
}