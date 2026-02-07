package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.db.entity.JellyfishEntity
import com.jellydrink.app.data.db.entity.UserProfileEntity
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val profile: UserProfileEntity? = null,
    val decorations: List<DecorationEntity> = emptyList(),
    val jellyfish: List<JellyfishEntity> = emptyList()
) {
    val currentXp: Int get() = profile?.spendableXp ?: 0  // Mostra XP spendibili
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult.asStateFlow()

    val uiState: StateFlow<ShopUiState> = combine(
        repository.getProfile(),
        repository.getAllDecorations(),
        repository.getAllJellyfish()
    ) { profile, decorations, jellyfish ->
        ShopUiState(
            profile = profile,
            decorations = decorations,
            jellyfish = jellyfish
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShopUiState()
    )

    fun purchaseDecoration(id: String) {
        viewModelScope.launch {
            val success = repository.purchaseDecoration(id)
            _purchaseResult.value = if (success) {
                PurchaseResult.Success
            } else {
                PurchaseResult.InsufficientXp
            }
        }
    }

    fun toggleDecorationPlaced(id: String) {
        viewModelScope.launch {
            repository.toggleDecorationPlaced(id)
        }
    }

    fun purchaseJellyfish(id: String) {
        viewModelScope.launch {
            val success = repository.purchaseJellyfish(id)
            _purchaseResult.value = if (success) {
                PurchaseResult.Success
            } else {
                PurchaseResult.InsufficientXp
            }
        }
    }

    fun selectJellyfish(id: String) {
        viewModelScope.launch {
            repository.selectJellyfish(id)
        }
    }

    fun dismissPurchaseResult() {
        _purchaseResult.value = null
    }

    sealed class PurchaseResult {
        data object Success : PurchaseResult()
        data object InsufficientXp : PurchaseResult()
    }
}
