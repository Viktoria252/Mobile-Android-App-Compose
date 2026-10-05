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

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
public fun SettingsFragment(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember {
        context.getSharedPreferences(PrefsKeys.PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }
    val initialName = prefs.getString(PrefsKeys.USER_NAME, "") ?: ""
    val initialGender = prefs.getString(PrefsKeys.USER_GENDER, "") ?: ""
    val initialAge = prefs.getInt(PrefsKeys.USER_AGE, 0)
    val initialDarkTheme = prefs.getBoolean(PrefsKeys.THEME_DARK, false)
    val initialFontScale = prefs.getFloat(PrefsKeys.FONT_SCALE, 1f)
    val initialLanguage = prefs.getString(PrefsKeys.LANGUAGE, "ru") ?: "ru"

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE)  { Log.d("StartFragment", "ON_CREATE") }
    LifecycleEventEffect(Lifecycle.Event.ON_START)   { Log.d("StartFragment", "ON_START") }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME)  { Log.d("StartFragment", "ON_RESUME") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE)   { Log.d("StartFragment", "ON_PAUSE") }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP)    { Log.d("StartFragment", "ON_STOP") }

    // Локальное состояние
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf(initialGender) }
    var ageText by remember { mutableStateOf(if (initialAge > 0) initialAge.toString() else "") }
    var ageError by remember { mutableStateOf<String?>(null) }
    var darkTheme by remember { mutableStateOf(initialDarkTheme) }
    var fontScale by remember { mutableFloatStateOf(initialFontScale) }
    var language by remember { mutableStateOf(initialLanguage) }
    var backupExists by remember { mutableStateOf(BackupManager.hasBackup(context)) }

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
                        checked = darkTheme,
                        onCheckedChange = {
                            darkTheme = it
                            prefs.edit().putBoolean(PrefsKeys.THEME_DARK, it).apply()
                            //TO DO
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Размер шрифта: ${"%.1f".format(fontScale)}x")
                Slider(
                    value = fontScale,
                    onValueChange = { fontScale = it },
                    onValueChangeFinished = { prefs.edit().putFloat(PrefsKeys.FONT_SCALE, fontScale).apply() },
                    valueRange = 0.8f..1.5f,
                    steps = 6
                    //TO DO
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Язык")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ru" to "Русский", "en" to "English").forEach { (code, label) ->
                        FilterChip(
                            selected = language == code,
                            onClick = {
                                language = code
                                prefs.edit().putString(PrefsKeys.LANGUAGE, code).apply()
                                //TO DO
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Резервное копирование
            SettingsSection(title = "Резервное копирование") {
                Text(
                    text = if (backupExists) "Резервная копия найдена"
                    else "Резервная копия отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (backupExists) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Создать
                Button(
                    onClick = {
                        val ok = BackupManager.createBackup(context)
                        backupExists = BackupManager.hasBackup(context)
                        showMessage(if (ok) "Копия создана" else "Ошибка создания копии")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Создать резервную копию")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Удалить
                OutlinedButton(
                    onClick = {
                        val ok = BackupManager.deleteBackup(context)
                        backupExists = BackupManager.hasBackup(context)
                        showMessage(if (ok) "Копия удалена" else "Ошибка удаления")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupExists
                ) {
                    Text("Удалить резервную копию")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Восстановить
                OutlinedButton(
                    onClick = {
                        val data = BackupManager.restoreBackup(context)
                        showMessage(
                            if (data == null) "Копия не найдена"
                            else "Восстановлено ${data.length} символов"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupExists
                ) {
                    Text("Восстановить из копии")
                }

                if (!backupExists) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Сначала создайте резервную копию, чтобы использовать восстановление.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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