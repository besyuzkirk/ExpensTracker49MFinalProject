package com.example.expensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var deletedTransaction: Expense
    private lateinit var oldTransactions : List<Expense>
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase
    private lateinit var expenses : List<Expense>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expenses = arrayListOf()

        expenseAdapter = ExpenseAdapter(expenses)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "expenses").build()

        recyclerview.apply {
            adapter = expenseAdapter
            layoutManager = linearLayoutManager
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(expenses[viewHolder.adapterPosition])
            }

        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        updateDashboard()

        addBtn.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAll(){
        GlobalScope.launch {
            expenses = db.expenseDao().getAll()

            runOnUiThread {
                updateDashboard()
                expenseAdapter.setData(expenses)
            }
        }
    }

    private fun updateDashboard(){
        val totalAmount = expenses.map { it.amount }.sum()
        val budgetAmount = expenses.filter { it.amount>0 }.map{it.amount}.sum()
        val expenseAmount = totalAmount - budgetAmount

        balance.text = "$ %.2f".format(totalAmount)
        budget.text = "$ %.2f".format(budgetAmount)
        expense.text = "$ %.2f".format(expenseAmount)
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.expenseDao().insertAll(deletedTransaction)

            expenses = oldTransactions

            runOnUiThread {
                expenseAdapter.setData(expenses)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Transaction deleted!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.black))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteTransaction(expense: Expense){
        deletedTransaction = expense
        oldTransactions = expenses

        GlobalScope.launch {
            db.expenseDao().delete(expense)

            expenses = expenses.filter { it.id != expense.id }
            runOnUiThread {
                updateDashboard()
                expenseAdapter.setData(expenses)
                showSnackbar()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}


