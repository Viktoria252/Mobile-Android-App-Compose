package com.mobile.vedroid.compose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.vedroid.compose.network.ItemRequest
import com.mobile.vedroid.compose.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EquipmentViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<ItemRequest>>(emptyList())
    val items: StateFlow<List<ItemRequest>> = _items.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var nextOffset = 0

    fun loadFirstPage() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            try {
                val list = RetrofitClient.apiService.getItems(PAGE_SIZE, 0)
                _items.value = list
                nextOffset = list.size
                _hasMore.value = list.size == PAGE_SIZE
            } catch (e: Exception) {
                _error.value = classifyError(e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadNextPage() {
        if (_isRefreshing.value || _isLoadingMore.value || !_hasMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val list = RetrofitClient.apiService.getItems(PAGE_SIZE, nextOffset)
                _items.value = (_items.value + list)
                nextOffset += list.size
                _hasMore.value = list.size == PAGE_SIZE
            } catch (e: Exception) {
                _error.value = classifyError(e)
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun clearError() { _error.value = null }

    private fun classifyError(e: Exception): String = when (e) {
        is UnknownHostException -> "Нет подключения к Интернету"
        is ConnectException, is SocketTimeoutException -> "Не удается подключиться к хосту"
        else -> "Ошибка подключения"
    }

    companion object {
        const val PAGE_SIZE = 4
    }
}