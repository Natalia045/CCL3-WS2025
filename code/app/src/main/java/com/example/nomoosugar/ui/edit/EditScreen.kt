package com.example.nomoosugar.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.nomoosugar.NoMooSugarApplication
import com.example.nomoosugar.ui.AppViewModelProvider

@Composable
fun EditScreen(nav: NavController, entryId: Long) {
    val app = LocalContext.current.applicationContext as NoMooSugarApplication
    val viewModel: EditViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EditViewModel(app.sugarRepository, androidx.lifecycle.SavedStateHandle(mapOf("entryId" to entryId))) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    val showDiscardDialog = uiState.showDiscardDialog
    val showDeleteDialog = uiState.showDeleteDialog

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Entry",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.requestDiscard() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Added at: ${uiState.timestampText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.label,
                onValueChange = viewModel::onLabelChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Sugar (g)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { viewModel.confirmDeleteDialog() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete")
                }

                Button(
                    onClick = { viewModel.onSave { nav.popBackStack() } },
                    enabled = uiState.canSave && !uiState.isSaving,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save")
                }
            }
        }

        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.cancelDiscard() },
                title = { Text("Discard changes?") },
                text = { Text("You have unsaved changes. Discard them?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDiscard { nav.popBackStack() } }) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelDiscard() }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.cancelDeleteDialog() },
                title = { Text("Delete entry?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onDelete { nav.popBackStack() } }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelDeleteDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
