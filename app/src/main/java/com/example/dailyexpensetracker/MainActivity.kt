
package com.example.dailyexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.data.ExpenseDatabase
import com.example.dailyexpensetracker.data.ExpenseRepository
import com.example.dailyexpensetracker.ui.screens.AddExpenseScreen
import com.example.dailyexpensetracker.ui.screens.HomeScreen
import com.example.dailyexpensetracker.ui.theme.DailyExpenseTrackerTheme
import com.example.dailyexpensetracker.viewmodel.ExpenseViewModel
import com.example.dailyexpensetracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ExpenseDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.expenseDao())
        val factory = ExpenseViewModelFactory(repository)

        setContent {
            DailyExpenseTrackerTheme {
                val expenseViewModel = remember {
                    factory.create(ExpenseViewModel::class.java)
                }

                ExpenseTrackerApp(expenseViewModel)
            }
        }
    }
}

@Composable
fun ExpenseTrackerApp(viewModel: ExpenseViewModel) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedExpenseId by remember { mutableIntStateOf(0) }

    val expenses by viewModel.expenses.collectAsState()

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = {
                    selectedExpenseId = 0
                    currentScreen = "add"
                },
                onExpenseClick = { id ->
                    selectedExpenseId = id
                    currentScreen = "edit"
                }
            )
        }

        "add", "edit" -> {
            val selectedExpense: Expense? =
                expenses.find { it.id == selectedExpenseId }

            AddExpenseScreen(
                existingExpense = if (currentScreen == "edit") {
                    selectedExpense
                } else {
                    null
                },
                onSave = { expense ->
                    if (currentScreen == "edit") {
                        viewModel.updateExpense(expense)
                    } else {
                        viewModel.addExpense(expense)
                    }
                    currentScreen = "home"
                },
                onCancel = {
                    currentScreen = "home"
                }
            )
        }
    }
}
