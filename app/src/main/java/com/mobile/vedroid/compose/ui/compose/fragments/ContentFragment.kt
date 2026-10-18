package com.mobile.vedroid.compose

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.annotations.SerializedName
import com.mobile.vedroid.compose.network.ItemRequest
import com.mobile.vedroid.compose.network.RetrofitClient
import com.mobile.vedroid.compose.ui.EquipmentViewModel
import com.mobile.vedroid.compose.ui.compose.ItemCatalog
import com.mobile.vedroid.compose.ui.compose.demoCatalog
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.String

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ContentFragment(
    onSettingsClick: () -> Unit = {},
    vm: EquipmentViewModel = viewModel()
) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) { Log.d("ContentFragment", "ON_CREATE") }
    LifecycleEventEffect(Lifecycle.Event.ON_START) { Log.d("ContentFragment", "ON_START") }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { Log.d("ContentFragment", "ON_RESUME") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { Log.d("ContentFragment", "ON_PAUSE") }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { Log.d("ContentFragment", "ON_STOP") }

    val items by vm.items.collectAsStateWithLifecycle()
    val isRefreshing by vm.isRefreshing.collectAsStateWithLifecycle()
    val isLoadingMore by vm.isLoadingMore.collectAsStateWithLifecycle()
    val hasMore by vm.hasMore.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { vm.loadFirstPage() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Каталог оборудования") },
                actions = {
                    IconButton(onClick = { vm.loadFirstPage() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            ShowItemItems(
                items = items,
                isLoadingMore = isLoadingMore,
                hasMore = hasMore,
                onLoadMore = { vm.loadNextPage() },
                onItemClick = { item ->
                    Log.d("ContentFragment", "click item: ${item.name}")
                }
            )
        }
    }
}
@Composable
private fun ShowItemItems(
    items: List<ItemRequest>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (ItemRequest) -> Unit
) {
    if (items.isEmpty()) {
        EmptyState(
            onRestoreClick = onLoadMore,
            onRetryClick = onLoadMore
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                "Всего позиций: ${items.size}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        items(items = items, key = { it.id }) { item ->
            ItemCatalog(item = item, onClick = { onItemClick(item) })
        }

        if (isLoadingMore) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }

        if (hasMore) {
            item {
                OutlinedButton(
                    onClick = onLoadMore,
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) { Text("Загрузить ещё") }
            }
        } else {
            item {
                Text(
                    "Больше позиций нет",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onRestoreClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Данные отсутствуют",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нет соединения с интернетом, и каталог пуст. " +
                    "Попробуйте восстановить данные из локальной копии.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRestoreClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Восстановить из копии", modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Повторить загрузку", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}


@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)

@Composable
private fun PreviewFragmentStart(){
    RentCamTheme (dynamicColor = false) {
        ContentFragment()
    }
}