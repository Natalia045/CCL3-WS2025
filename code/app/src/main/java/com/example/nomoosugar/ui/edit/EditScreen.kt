package com.example.nomoosugar.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.platform.LocalContext
import com.example.nomoosugar.NoMooSugarApplication

@Composable
fun EditScreen(nav: NavController, entryId: Long) {
    val app = LocalContext.current.applicationContext as NoMooSugarApplication
    val backStackEntry = nav.currentBackStackEntry
    val savedStateHandle = checkNotNull(backStackEntry?.savedStateHandle) {
        "SavedStateHandle not available"
    }
    savedStateHandle["entryId"] = entryId
    
    val viewModel: EditViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EditViewModel(app.sugarRepository, savedStateHandle) as T
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
            Spacer(modifier = Modifier.height(8.dp))

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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.confirmDeleteDialog() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCDD2),
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.onSave { nav.popBackStack() } },
                    enabled = uiState.canSave && !uiState.isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
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

    LaunchedEffect(savedStateHandle) {
        savedStateHandle.getStateFlow("requestDiscard", false).collectLatest { requested ->
            if (requested) {
                viewModel.handleClose { nav.popBackStack() }
                savedStateHandle["requestDiscard"] = false
            }
        }
    }
}
