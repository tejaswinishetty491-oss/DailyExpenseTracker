


package com.example.dailyexpensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpensetracker.data.Expense
import com.example.dailyexpensetracker.data.ExpenseRepository
import com.example.dailyexpensetracker.data.CategoryTotal
import com.example.dailyexpensetracker.utils.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> =
        repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val todayTotal: StateFlow<Double> =
        repository.getTotalBetween(
            DateUtils.startOfDay(),
            DateUtils.endOfDay()
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val monthTotal: StateFlow<Double> =
        repository.getTotalBetween(
            DateUtils.startOfMonth(),
            DateUtils.endOfMonth()
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val categoryTotals: StateFlow<List<CategoryTotal>> =
        repository.categoryTotals.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
