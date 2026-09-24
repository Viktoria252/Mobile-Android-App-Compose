package com.mobile.vedroid.compose

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RegistrationActivity", "onCreate")
        enableEdgeToEdge()

        setContent {
            RentCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegistrationScreen(
                        onRegisterSuccess = { name, email ->
                            Log.d("RegistrationActivity", "register success: $email")
                            // Возвращаем результат на StartActivity
                            val resultIntent = Intent().apply {
                                putExtra("USER_NAME", name)
                                putExtra("USER_EMAIL", email)
                            }
                            setResult(RESULT_OK, resultIntent)
                            finish() // закрываем Activity, возвращаемся на Start
                        },
                        onBackClick = {
                            Log.d("RegistrationActivity", "click: back")
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    )
                }
            }
        }
    }

    // Логирование жизненного цикла
    override fun onStart() {
        super.onStart()
        Log.d("RegistrationActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("RegistrationActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RegistrationActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("RegistrationActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RegistrationActivity", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("RegistrationActivity", "onRestart")
    }
}

@Preview(showSystemUi = true, name = "Light")
@Composable
fun RegistrationScreenPreviewLight() {
    RentCamTheme(darkTheme = false) {
        RegistrationScreen()
    }
}

@Preview(showSystemUi = true, name = "Dark")
@Composable
fun RegistrationPreviewDark() {
    RentCamTheme(darkTheme = true) {
        RegistrationScreen()
    }
}
@Composable
fun RegistrationScreen(
    onRegisterSuccess: (name: String, email: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Состояния полей
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Состояния ошибок
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        var isValid = true
        nameError = null
        emailError = null
        passwordError = null
        confirmError = null

        // Имя
        if (name.isBlank()) {
            nameError = "Введите имя"
            isValid = false
        } else if (name.trim().length < 2) {
            nameError = "Имя должно содержать минимум 2 символа"
            isValid = false
        }

        // Email
        if (email.isBlank()) {
            emailError = "Введите email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Некорректный email"
            isValid = false
        }

        // Пароль
        if (password.isBlank()) {
            passwordError = "Введите пароль"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Пароль должен быть не менее 6 символов"
            isValid = false
        }

        // Подтверждение пароля
        if (confirmPassword.isBlank()) {
            confirmError = "Подтвердите пароль"
            isValid = false
        } else if (confirmPassword != password) {
            confirmError = "Пароли не совпадают"
            isValid = false
        }

        return isValid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // ← прокрутка, если клавиатура перекрывает
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // БЛОК 1: Заголовок
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // БЛОК 2: Поля ввода
            // Имя
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Пароль
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    confirmError = null
                },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Подтверждение пароля
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmError = null
                },
                label = { Text("Подтверждение пароля") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmError != null,
                supportingText = {
                    if (confirmError != null) {
                        Text(text = confirmError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // БЛОК 3: Кнопки
            // Кнопка подтверждения
            Button(
                onClick = {
                    if (validateInputs()) {
                        onRegisterSuccess(name.trim(), email.trim())
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Проверьте правильность заполнения полей"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться", modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопка возврата
            OutlinedButton(
                onClick = { onBackClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
