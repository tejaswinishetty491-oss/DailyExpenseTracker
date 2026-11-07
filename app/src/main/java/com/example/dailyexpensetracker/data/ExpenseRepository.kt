
package com.example.dailyexpensetracker.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {

    val allExpenses: Flow<List<Expense>> =
        expenseDao.getAllExpenses()

    fun getTotalBetween(
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        return expenseDao.getTotalBetween(startDate, endDate)
    }

    val categoryTotals: Flow<List<CategoryTotal>> =
        expenseDao.getCategoryTotals()

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}


