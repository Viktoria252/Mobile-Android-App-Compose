package com.mobile.vedroid.compose

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.platform.LocalContext

// Ключи для SharedPreferences
object PrefsKeys {
    const val PREFS_NAME = "rentcam_prefs"
    const val USER_NAME = "user_name"
    const val USER_EMAIL = "user_email"
    const val USER_GENDER = "user_gender"
    const val USER_AGE = "user_age"
    const val THEME_DARK = "theme_dark"
    const val FONT_SCALE = "font_scale"
    const val LANGUAGE = "language"
}

// Работа с резервной копией
object BackupManager {
    private const val BACKUP_FILE = "rentcam_backup.json"

    fun backupFile(context: Context): File = File(context.filesDir, BACKUP_FILE)

    fun hasBackup(context: Context): Boolean = backupFile(context).exists()

    /**
     * Создаёт резервную копию. Здесь имитация — сохраняем демо-данные каталога.
     * В лабораторной 3 сюда попадёт список аренды.
     */
    fun createBackup(context: Context): Boolean {
        return try {
            val demoData = """
                [
                  {"id":1,"name":"Sony A7 III","price":2500,"status":"Доступно"},
                  {"id":2,"name":"Canon EF 50mm f/1.8","price":500,"status":"Занято"},
                  {"id":3,"name":"Manfrotto MT055","price":700,"status":"Доступно"}
                ]
            """.trimIndent()
            backupFile(context).writeText(demoData)
            Log.d("SettingsActivity", "backup created: ${backupFile(context).absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("SettingsActivity", "backup error", e)
            false
        }
    }

    fun deleteBackup(context: Context): Boolean {
        return try {
            val file = backupFile(context)
            if (file.exists()) file.delete()
            Log.d("SettingsActivity", "backup deleted")
            true
        } catch (e: Exception) {
            Log.e("SettingsActivity", "delete backup error", e)
            false
        }
    }

    fun restoreBackup(context: Context): String? {
        return try {
            val file = backupFile(context)
            if (!file.exists()) null else file.readText()
        } catch (e: Exception) {
            Log.e("SettingsActivity", "restore backup error", e)
            null
        }
    }
}

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SettingsActivity", "onCreate")
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PrefsKeys.PREFS_NAME, MODE_PRIVATE)

        setContent {
            RentCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(
                        initialName = prefs.getString(PrefsKeys.USER_NAME, "") ?: "",
                        initialGender = prefs.getString(PrefsKeys.USER_GENDER, "") ?: "",
                        initialAge = prefs.getInt(PrefsKeys.USER_AGE, 0),
                        initialDarkTheme = prefs.getBoolean(PrefsKeys.THEME_DARK, false),
                        initialFontScale = prefs.getFloat(PrefsKeys.FONT_SCALE, 1f),
                        initialLanguage = prefs.getString(PrefsKeys.LANGUAGE, "ru") ?: "ru",
                        hasBackup = BackupManager.hasBackup(this),
                        onSaveUser = { name, gender, age ->
                            prefs.edit()
                                .putString(PrefsKeys.USER_NAME, name)
                                .putString(PrefsKeys.USER_GENDER, gender)
                                .putInt(PrefsKeys.USER_AGE, age)
                                .apply()
                            Log.d("SettingsActivity", "user saved: $name, $gender, $age")
                        },
                        onSaveTheme = { dark ->
                            prefs.edit().putBoolean(PrefsKeys.THEME_DARK, dark).apply()
                            Log.d("SettingsActivity", "theme dark: $dark")
                        },
                        onSaveFontScale = { scale ->
                            prefs.edit().putFloat(PrefsKeys.FONT_SCALE, scale).apply()
                            Log.d("SettingsActivity", "font scale: $scale")
                        },
                        onSaveLanguage = { lang ->
                            prefs.edit().putString(PrefsKeys.LANGUAGE, lang).apply()
                            Log.d("SettingsActivity", "language: $lang")
                        },
                        onBackupCreate = { BackupManager.createBackup(this) },
                        onBackupDelete = { BackupManager.deleteBackup(this) },
                        onBackupRestore = { BackupManager.restoreBackup(this) },
                        onBackClick = {
                            Log.d("SettingsActivity", "click: back")
                            finish()
                        }
                    )
                }
            }
        }
    }

    // Логирование жизненного цикла
    override fun onStart()    { super.onStart();    Log.d("SettingsActivity", "onStart") }
    override fun onResume()   { super.onResume();   Log.d("SettingsActivity", "onResume") }
    override fun onPause()    { super.onPause();    Log.d("SettingsActivity", "onPause") }
    override fun onStop()     { super.onStop();     Log.d("SettingsActivity", "onStop") }
    override fun onDestroy()  { super.onDestroy();  Log.d("SettingsActivity", "onDestroy") }
    override fun onRestart()  { super.onRestart();  Log.d("SettingsActivity", "onRestart") }
}

@Preview(showSystemUi = true, name = "Light")
@Composable
fun SettingsScreenPreviewLight() {
    RentCamTheme(darkTheme = false) {
        SettingsScreen()
    }
}

@Preview(showSystemUi = true, name = "Dark")
@Composable
fun SettingsScreenPreviewDark() {
    RentCamTheme(darkTheme = true) {
        SettingsScreen()
    }
}

@Composable
fun SettingsScreen(
    initialName: String = "",
    initialGender: String = "",
    initialAge: Int = 0,
    initialDarkTheme: Boolean = false,
    initialFontScale: Float = 1f,
    initialLanguage: String = "ru",
    hasBackup: Boolean = false,
    onSaveUser: (name: String, gender: String, age: Int) -> Unit = { _, _, _ -> },
    onSaveTheme: (Boolean) -> Unit = {},
    onSaveFontScale: (Float) -> Unit = {},
    onSaveLanguage: (String) -> Unit = {},
    onBackupCreate: () -> Boolean = { false },
    onBackupDelete: () -> Boolean = { false },
    onBackupRestore: () -> String? = { null },
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Локальное состояние
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf(initialGender) }
    var ageText by remember { mutableStateOf(if (initialAge > 0) initialAge.toString() else "") }
    var ageError by remember { mutableStateOf<String?>(null) }

    var darkTheme by remember { mutableStateOf(initialDarkTheme) }
    var fontScale by remember { mutableFloatStateOf(initialFontScale) }
    var language by remember { mutableStateOf(initialLanguage) }

    var backupExists by remember { mutableStateOf(hasBackup) }

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
                                onSaveUser(name.trim(), gender, age)
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
                            onSaveTheme(it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Размер шрифта: ${"%.1f".format(fontScale)}x")
                Slider(
                    value = fontScale,
                    onValueChange = { fontScale = it },
                    onValueChangeFinished = { onSaveFontScale(fontScale) },
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
                            selected = language == code,
                            onClick = {
                                language = code
                                onSaveLanguage(code)
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
                        val ok = onBackupCreate()
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
                        val ok = onBackupDelete()
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
                        val data = onBackupRestore()
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
fun SettingsSection(
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