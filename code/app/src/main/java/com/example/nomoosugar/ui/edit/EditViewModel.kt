package com.example.nomoosugar.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.SugarEntryEntity
import com.example.nomoosugar.repository.SugarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class EditUiState(
    val label: String = "",
    val amount: String = "",
    val timestampText: String = "",
    val isDirty: Boolean = false,
    val isSaving: Boolean = false,
    val canSave: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
)

class EditViewModel(
    private val repository: SugarRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long = savedStateHandle["entryId"] ?: 0L

    private var original: SugarEntryEntity? = null

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    init {
        if (entryId != 0L) {
            viewModelScope.launch {
                repository.observeEntry(entryId)
                    .filterNotNull()
                    .first()
                    .let { entry ->
                        original = entry
                        _uiState.value = _uiState.value.copy(
                            label = entry.label ?: "",
                            amount = entry.amount.toString(),
                            timestampText = formatTimestamp(entry.timestamp),
                            isDirty = false,
                            canSave = false
                        )
                    }
            }
        }
    }

    private fun formatTimestamp(ts: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(ts)
    }

    fun onLabelChange(newLabel: String) {
        _uiState.value = _uiState.value.copy(
            label = newLabel,
            isDirty = computeDirty(newLabel, _uiState.value.amount),
            canSave = computeCanSave(newLabel, _uiState.value.amount)
        )
    }

    fun onAmountChange(newAmount: String) {
        _uiState.value = _uiState.value.copy(
            amount = newAmount,
            isDirty = computeDirty(_uiState.value.label, newAmount),
            canSave = computeCanSave(_uiState.value.label, newAmount)
        )
    }

    private fun computeDirty(label: String, amount: String): Boolean {
        val orig = original ?: return false
        val amountDouble = amount.toDoubleOrNull()
        return (label != (orig.label ?: "")) || (amountDouble != orig.amount)
    }

    private fun computeCanSave(label: String, amount: String): Boolean {
        val amountDouble = amount.toDoubleOrNull()
        return !label.isBlank() && amountDouble != null && amountDouble > 0.0 && computeDirty(label, amount)
    }

    fun onSave(onDone: () -> Unit) {
        val amountDouble = _uiState.value.amount.toDoubleOrNull() ?: return
        val current = original ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val updated = current.copy(
                label = _uiState.value.label,
                amount = amountDouble
            )
            repository.updateEntry(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, isDirty = false, canSave = false)
            onDone()
        }
    }

    fun onDelete(onDone: () -> Unit) {
        val current = original ?: return
        viewModelScope.launch {
            repository.deleteEntry(current)
            onDone()
        }
    }

    fun handleClose(onDone: () -> Unit) {
        if (_uiState.value.isDirty) {
            _uiState.value = _uiState.value.copy(showDiscardDialog = true)
        } else {
            onDone()
        }
    }

    fun confirmDiscard(onDone: () -> Unit) {
        _uiState.value = _uiState.value.copy(showDiscardDialog = false)
        onDone()
    }

    fun cancelDiscard() {
        _uiState.value = _uiState.value.copy(showDiscardDialog = false)
    }

    fun confirmDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun cancelDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }
}
