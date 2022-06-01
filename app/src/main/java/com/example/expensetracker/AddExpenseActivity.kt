    package com.example.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        labelInput.addTextChangedListener {
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if(it!!.count() > 0)
                amountLayout.error = null
        }

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                val transaction  = Expense(0, label, amount, description)
                insert(transaction)
            }

        }

        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(expense:  Expense){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "expenses").build()

        GlobalScope.launch {
            db.expenseDao().insertAll(expense)
            finish()
        }
    }


}