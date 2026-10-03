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
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.mobile.vedroid.compose.ui.compose.CatalogItem
import com.mobile.vedroid.compose.ui.compose.ItemCatalog
import com.mobile.vedroid.compose.ui.compose.demoCatalog
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
public fun ContentFragment(
    onSettingsClick: () -> Unit = {}
) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE)  { Log.d("StartFragment", "ON_CREATE") }
    LifecycleEventEffect(Lifecycle.Event.ON_START)   { Log.d("StartFragment", "ON_START") }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME)  { Log.d("StartFragment", "ON_RESUME") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE)   { Log.d("StartFragment", "ON_PAUSE") }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP)    { Log.d("StartFragment", "ON_STOP") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Список карточек как изменяемое состояние
    val items = remember { mutableStateListOf<CatalogItem>().apply { addAll(demoCatalog()) } }

    // Флаги для загрузки
    var isLoading by remember { mutableStateOf(false) }
    var loadPage by remember { mutableStateOf(1) }

    fun showMessage(msg: String) {
        scope.launch { snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Каталог оборудования",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isLoading = true
                        scope.launch {
                            delay(1000) // имитация сетевой загрузки
                            loadPage++
                            items.addAll(demoCatalog(page = loadPage))
                            isLoading = false
                            showMessage("Загружено ${items.size} карточек")
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // если список пуст
                items.isEmpty() -> {
                    EmptyState(
                        onRestoreClick = {
                            // Заглушка: имитируем восстановление из копии
                            items.addAll(demoCatalog())
                            showMessage("Данные восстановлены из резервной копии")
                        },
                        onRetryClick = {
                            items.addAll(demoCatalog())
                            showMessage("Данные загружены")
                        }
                    )
                }

                //если есть карточки — показываем список
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // Заголовок перед списком
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Всего позиций: ${items.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Карточки
                        items(
                            items = items,
                            key = { it.id } // ключ нужен для корректной анимации и переиспользования
                        ) { item ->
                            ItemCatalog(
                                item = item,
                                onClick = {
                                    Log.d("ContentActivity", "click item: ${item.name}")
                                    showMessage("Открыто: ${item.name}")
                                }
                            )
                        }

                        // Индикатор загрузки внизу списка
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        // Кнопка "Загрузить ещё" в самом низу
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    isLoading = true
                                    scope.launch {
                                        delay(800)
                                        loadPage++
                                        items.addAll(demoCatalog(page = loadPage))
                                        isLoading = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("Загрузить ещё")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
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