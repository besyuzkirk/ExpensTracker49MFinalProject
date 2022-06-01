package com.example.expensetracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "expenses")
data class Expense( @PrimaryKey(autoGenerate = true) val id: Int,
                    val label: String,
                    val amount: Double,
                    val description: String) :  Serializable  {

}