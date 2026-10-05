package com.mobile.vedroid.compose

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
public fun StartFragment(
    userName: String = "",
    savedEmail: String = "",
    onLoginClick: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {}
    ) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE)  { Log.d("StartFragment", "ON_CREATE") }
    LifecycleEventEffect(Lifecycle.Event.ON_START)   { Log.d("StartFragment", "ON_START") }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME)  { Log.d("StartFragment", "ON_RESUME") }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE)   { Log.d("StartFragment", "ON_PAUSE") }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP)    { Log.d("StartFragment", "ON_STOP") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Состояния для полей ввода
    var login by remember(savedEmail) { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }

    // Состояния для ошибок валидации
    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateInputs(): Boolean {
        var isValid = true
        loginError = null
        passwordError = null

        // Проверка логина (email)
        if (login.isBlank()) {
            loginError = "Введите email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            loginError = "Некорректный email"
            isValid = false
        }

        // Проверка пароля
        if (password.isBlank()) {
            passwordError = "Введите пароль"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Пароль должен быть не менее 6 символов"
            isValid = false
        }

        return isValid
    }

    val greetingText = if (userName.isNotBlank()) {
        "С возвращением, $userName!"
    } else {
        "Добро пожаловать!"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Информация о бренде
            Image(
                painter = painterResource(R.drawable.rentcam_big),
                contentDescription = "Jetpack Compose"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "RentCam",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Аренда фото и видео оборудования",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Авторизация
            Text(
                text = greetingText,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Поле логина с обработкой ошибки
            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                    loginError = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = loginError != null,
                supportingText = {
                    if (loginError != null) {
                        Text(text = loginError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле пароля с обработкой ошибки
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
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

            Spacer(modifier = Modifier.height(32.dp))

            //  Кнопки навигации
            // Кнопка "Войти"
            Button(
                onClick = {
                    if (validateInputs()) {
                        onLoginClick(login)
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
                Text("Войти", modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопка "Зарегистрироваться"
            OutlinedButton(
                onClick = { onRegisterClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться", modifier = Modifier.padding(vertical = 8.dp))
            }
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
        StartFragment()
    }
}