package com.mobile.vedroid.compose

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.mobile.vedroid.compose.ui.compose.BackupManager
import com.mobile.vedroid.compose.ui.compose.PrefsKeys
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.vedroid.compose.network.ItemRequest
import com.mobile.vedroid.compose.ui.EquipmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.appDataStore by preferencesDataStore(name = "app_settings")

object DataStoreKeys {
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val LANGUAGE   = stringPreferencesKey("language")
    val DARK_THEME = booleanPreferencesKey("dark_theme")
}

class SettingsDataStore(private val context: Context) {

    val fontScale: Flow<Float> = context.appDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[DataStoreKeys.FONT_SCALE] ?: 1f }

    val language: Flow<String> = context.appDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[DataStoreKeys.LANGUAGE] ?: "ru" }

    val dark_theme: Flow<Boolean> = context.appDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[DataStoreKeys.DARK_THEME] ?: false }

    suspend fun saveFontScale(value: Float) {
        context.appDataStore.edit { it[DataStoreKeys.FONT_SCALE] = value }
    }

    suspend fun saveLanguage(code: String) {
        context.appDataStore.edit { it[DataStoreKeys.LANGUAGE] = code }
    }

    suspend fun saveDarkTheme(theme: Boolean){
        context.appDataStore.edit { it[DataStoreKeys.DARK_THEME] = theme}
    }
}
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
public fun SettingsFragment(
    onBackClick: () -> Unit = {},
    vm: EquipmentViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val prefs = remember {
        context.getSharedPreferences(PrefsKeys.PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }
    val initialName = prefs.getString(PrefsKeys.USER_NAME, "") ?: ""
    val initialGender = prefs.getString(PrefsKeys.USER_GENDER, "") ?: ""
    val initialAge = prefs.getInt(PrefsKeys.USER_AGE, 0)

    val dataStore = remember { SettingsDataStore(context.applicationContext) }
    val savedFontScale by dataStore.fontScale.collectAsStateWithLifecycle(initialValue = 1f)
    val savedLanguage by dataStore.language.collectAsStateWithLifecycle(initialValue = "ru")
    val savedDarkTheme by dataStore.dark_theme.collectAsStateWithLifecycle(initialValue = false)

    val equipment by vm.items.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE)  { Log.d("StartFragment", "ON_CREATE") }
    LifecycleEventEffect(Lifecycle.Event.ON_START)   { Log.d("StartFragment", "ON_START") }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME)  { Log.d("StartFragment", "ON_RESUME") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE)   { Log.d("StartFragment", "ON_PAUSE") }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP)    { Log.d("StartFragment", "ON_STOP") }

    // Локальное состояние профиля (prefs)
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf(initialGender) }
    var ageText by remember { mutableStateOf(if (initialAge > 0) initialAge.toString() else "") }
    var ageError by remember { mutableStateOf<String?>(null) }

    // Черновик слайдера (пока пользователь тянет)
    var draftFontScale by remember(savedFontScale) { mutableFloatStateOf(savedFontScale) }

    // Состояние файлов бэкапа
    var externalInfo by remember { mutableStateOf(BackupManager.externalInfo(context)) }
    var internalExists by remember { mutableStateOf(BackupManager.hasInternal(context)) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun showMessage(msg: String) {
        scope.launch { snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // БЛОК 1: Профиль
            SettingsSection(title = "Профиль пользователя") {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        ageText = it.filter { ch -> ch.isDigit() }
                        ageError = null
                    },
                    label = { Text("Возраст") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = ageError != null,
                    supportingText = {
                        if (ageError != null) {
                            Text(text = ageError!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Пол",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Мужской", "Женский", "Не указан").forEach { option ->
                        FilterChip(
                            selected = gender == option,
                            onClick = { gender = option },
                            label = { Text(option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        // Валидация возраста
                        val age = ageText.toIntOrNull()
                        when {
                            ageText.isBlank() -> {
                                ageError = "Введите возраст"
                                showMessage("Проверьте поле «Возраст»")
                            }
                            age == null || age <= 0 -> {
                                ageError = "Возраст должен быть положительным"
                                showMessage("Некорректный возраст")
                            }
                            age > 120 -> {
                                ageError = "Возраст не может быть больше 120"
                                showMessage("Некорректный возраст")
                            }
                            else -> {
                                ageError = null
                                prefs.edit()
                                    .putString(PrefsKeys.USER_NAME, name.trim())
                                    .putString(PrefsKeys.USER_GENDER, gender)
                                    .putInt(PrefsKeys.USER_AGE, age)
                                    .apply()
                                showMessage("Профиль сохранён")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить профиль")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Оформление
            SettingsSection(title = "Оформление") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Тёмная тема", modifier = Modifier.weight(1f))
                    Switch(
                        checked = savedDarkTheme,
                        onCheckedChange = {value -> scope.launch { dataStore.saveDarkTheme(value) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Размер шрифта: ${"%.1f".format(draftFontScale)}x")
                Slider(
                    value = draftFontScale,
                    onValueChange = { draftFontScale = it },
                    onValueChangeFinished = {
                        scope.launch { dataStore.saveFontScale(draftFontScale) }
                    },
                    valueRange = 0.8f..1.5f,
                    steps = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Язык")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ru" to "Русский", "en" to "English").forEach { (code, label) ->
                        FilterChip(
                            selected = savedLanguage == code,
                            onClick = {
                                scope.launch { dataStore.saveLanguage(code) }
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === External file section ===
            SettingsSection(title = "Файл каталога в Downloads") {
                val info = externalInfo
                Text(
                    text = if (info == null) "Файл отсутствует"
                    else buildString {
                        appendLine("Имя: ${info.name}")
                        appendLine("Расположение: ${info.path}")
                        appendLine("Размер: ${info.sizeText()}")
                        append("Создан: ${info.dateText()}")
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (equipment.isEmpty()) {
                            showMessage("Сначала откройте каталог и загрузите данные")
                            return@Button
                        }
                        scope.launch {
                            val content = buildEquipmentText(equipment)
                            val ok = withContext(Dispatchers.IO) {
                                BackupManager.saveToExternal(context, content)
                            }
                            externalInfo = BackupManager.externalInfo(context)
                            showMessage(if (ok) "Файл сохранён в Downloads" else "Ошибка сохранения")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Создать файл в Downloads")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val ok = withContext(Dispatchers.IO) {
                                BackupManager.deleteExternalSavingInternal(context)
                            }
                            externalInfo = BackupManager.externalInfo(context)
                            internalExists = BackupManager.hasInternal(context)
                            showMessage(if (ok) "Удалено, копия сохранена внутри" else "Ошибка")
                        }
                    },
                    enabled = externalInfo != null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Удалить (с сохранением копии)") }
            }

            Spacer(Modifier.height(16.dp))

// === Internal backup section ===
            SettingsSection(title = "Резервная копия (internal)") {
                Text(
                    text = if (internalExists) "Резервная копия найдена"
                    else "Резервная копия отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (internalExists) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!internalExists) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Сначала удалите файл из /Downloads — его содержимое " +
                                "сохранится как резервная копия внутри приложения.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val ok = withContext(Dispatchers.IO) {
                                BackupManager.restoreInternalToExternal(context)
                            }
                            externalInfo = BackupManager.externalInfo(context)
                            showMessage(if (ok) "Файл восстановлен в Downloads" else "Ошибка")
                        }
                    },
                    enabled = internalExists,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Восстановить в Downloads") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка возврата
            Button(
                onClick = { onBackClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("К каталогу", modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun buildEquipmentText(items: List<ItemRequest>): String =
    if (items.isEmpty()) "Каталог пуст"
    else buildString {
        appendLine("Каталог оборудования. Всего позиций: ${items.size}")
        appendLine(
            "Сформировано: " +
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        )
        appendLine()
        items.forEachIndexed { i, item ->
            appendLine("${i + 1}. ${item.name}")
            appendLine("   Категория: ${item.category}")
            appendLine("   Цена/день: ${item.pricePerDay} ₽")
            appendLine("   Описание: ${item.description}")
            appendLine()
        }
    }

/*
 Универсальная секция настроек — карточка с заголовком и содержимым.
 */
@Composable
public fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
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
        SettingsFragment()
    }
}