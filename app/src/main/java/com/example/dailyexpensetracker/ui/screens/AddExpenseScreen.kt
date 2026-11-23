
package com.example.dailyexpensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailyexpensetracker.data.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val expenseCategories = listOf(
    "Food", "Travel", "Shopping", "Bills",
    "Entertainment", "Health", "Education", "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    existingExpense: Expense? = null,
    onSave: (Expense) -> Unit,
    onCancel: () -> Unit
) {
    var amount by remember(existingExpense?.id) {
        mutableStateOf(existingExpense?.amount?.toString() ?: "")
    }
    var note by remember(existingExpense?.id) {
        mutableStateOf(existingExpense?.note ?: "")
    }
    var category by remember(existingExpense?.id) {
        mutableStateOf(existingExpense?.category ?: "")
    }
    var selectedDate by remember(existingExpense?.id) {
        mutableLongStateOf(existingExpense?.date ?: System.currentTimeMillis())
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            if (existingExpense == null) "Add Expense" else "Edit Expense",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (₹)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                expenseCategories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            category = item
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Date: " + SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(selectedDate))
            )
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()

                when {
                    parsedAmount == null || parsedAmount <= 0 ->
                        error = "Enter an amount greater than zero"

                    category.isBlank() ->
                        error = "Please select a category"

                    else -> {
                        onSave(
                            Expense(
                                id = existingExpense?.id ?: 0,
                                amount = parsedAmount,
                                category = category,
                                note = note.trim(),
                                date = selectedDate
                            )
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (existingExpense == null) "Save Expense" else "Update Expense")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
