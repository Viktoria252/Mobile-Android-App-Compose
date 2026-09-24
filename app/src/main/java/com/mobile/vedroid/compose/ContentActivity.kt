package com.mobile.vedroid.compose

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Surface
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
import com.mobile.vedroid.compose.ui.compose.CatalogItem
import com.mobile.vedroid.compose.ui.compose.ItemCatalog
import com.mobile.vedroid.compose.ui.compose.ItemStatus
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ContentActivity", "onCreate")
        enableEdgeToEdge()

        // Читаем email, переданный из StartActivity (если нужно для приветствия)
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        setContent {
            RentCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentScreen(
                        userEmail = userEmail,
                        onSettingsClick = {
                            Log.d("ContentActivity", "click: settings")
                            val intent = Intent(this, SettingsActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // === Логирование жизненного цикла ===
    override fun onStart()   { super.onStart();   Log.d("ContentActivity", "onStart") }
    override fun onResume()  { super.onResume();  Log.d("ContentActivity", "onResume") }
    override fun onPause()   { super.onPause();   Log.d("ContentActivity", "onPause") }
    override fun onStop()    { super.onStop();     Log.d("ContentActivity", "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("ContentActivity", "onDestroy") }
    override fun onRestart() { super.onRestart(); Log.d("ContentActivity", "onRestart") }
}

@Preview(showSystemUi = true, name = "Light")
@Composable
fun ContentScreenPreviewLight() {
    RentCamTheme(darkTheme = false) {
        ContentScreen()
    }
}

@Preview(showSystemUi = true, name = "Dark")
@Composable
fun ContentScreenPreviewDark() {
    RentCamTheme(darkTheme = true) {
        ContentScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    userEmail: String = "",
    onSettingsClick: () -> Unit = {}
) {
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
//                        if (userEmail.isNotBlank()) {
//                            Text(
//                                text = userEmail,
//                                style = MaterialTheme.typography.labelSmall,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
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

/**
 * Заглушка для пустого списка с двумя действиями:
 * 1. Восстановить из резервной копии.
 * 2. Попробовать загрузить снова.
 */
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

/**
 * Демо-данные для каталога.
 */
private fun demoCatalog(page: Int = 1): List<CatalogItem> {
    val base = listOf(
        CatalogItem(1, "Sony A7 III", "Камера",
            "Полнокадровая беззеркальная камера 24 МП", 2500, ItemStatus.AVAILABLE,
            imageRes = R.drawable.sony_a7),
        CatalogItem(2, "Canon EOS R6", "Камера",
            "Полнокадровая камера с 4K-видео и стабилизацией", 3200, ItemStatus.RENTED,
            imageRes = R.drawable.canon_eos_r6),
        CatalogItem(3, "Nikon Z6 II", "Камера",
            "Гибридная камера для фото и видео 24 МП", 2800, ItemStatus.AVAILABLE,
            imageRes = R.drawable.nikon_z6),
        CatalogItem(4, "Sony FX3", "Видеокамера",
            "Кинематографическая камера с S-Cinetone", 5500, ItemStatus.BOOKED,
            imageRes = R.drawable.sony_fx3),
        CatalogItem(5, "Blackmagic Pocket 6K", "Видеокамера",
            "Компактная кинокамера с сенсором Super 35", 4200, ItemStatus.AVAILABLE,
            imageRes = R.drawable.blackmagic_pocket_6k),
        CatalogItem(6, "Canon EF 50mm f/1.8", "Объектив",
            "Светосильный фикс для портретной съёмки", 500, ItemStatus.AVAILABLE,
            imageRes = R.drawable.canon_ef_50mm),
        CatalogItem(7, "Sigma 24-70mm f/2.8", "Объектив",
            "Универсальный зум для репортажа", 1200, ItemStatus.RENTED,
            imageRes = R.drawable.sigma_24_70mm),
        CatalogItem(8, "Sony FE 70-200mm f/4", "Объектив",
            "Телезум для спорта и wildlife", 1500, ItemStatus.AVAILABLE,
            imageRes = R.drawable.sony_fe_70_200mm),
        CatalogItem(9, "Aputure 120D II", "Свет",
            "LED-моноблок 180 Вт с Bowens-байонетом", 1800, ItemStatus.AVAILABLE,
            imageRes = R.drawable.aputure_120d),
        CatalogItem(10, "Godox SL-60W", "Свет",
            "Компактный LED-источник 60 Вт", 700, ItemStatus.BOOKED,
            imageRes = R.drawable.godox_sl_60w),
        CatalogItem(11, "Manfrotto MT055XPRO3", "Штатив",
            "Алюминиевый штатив до 9 кг", 700, ItemStatus.AVAILABLE,
            imageRes = R.drawable.manfrotto_mt055xpxo3),
        CatalogItem(12, "DJI RS 3 Pro", "Стабилизатор",
            "Профессиональный стабилизатор до 4.5 кг", 3000, ItemStatus.RENTED,
            imageRes = R.drawable.dji_rs_3_pro),
    )

    return if (page == 1) base
    else base.mapIndexed { index, item ->
        item.copy(
            id = item.id + (page - 1) * 100 + index,
            name = "${item.name} (стр. $page)"
        )
    }
}