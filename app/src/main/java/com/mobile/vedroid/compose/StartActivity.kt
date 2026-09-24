package com.mobile.vedroid.compose

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.vedroid.compose.ui.theme.RentCamTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class StartActivity : ComponentActivity() {

    // Launcher для получения результата из RegistrationActivity
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val userNameState = mutableStateOf("")
    private val userEmailState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("StartActivity", "onCreate")
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PrefsKeys.PREFS_NAME, MODE_PRIVATE)
        userNameState.value = prefs.getString(PrefsKeys.USER_NAME, "") ?: ""
        userEmailState.value = prefs.getString(PrefsKeys.USER_EMAIL, "") ?: ""

        // Регистрируем launcher ДО setContent
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val name = result.data?.getStringExtra("USER_NAME").orEmpty()
                val email = result.data?.getStringExtra("USER_EMAIL").orEmpty()
                Log.d("StartActivity", "registration result: $email")
                userNameState.value = name
                userEmailState.value = email
                // Сохраняем в SharedPreferences для следующих запусков
                prefs.edit()
                    .putString(PrefsKeys.USER_NAME, name)
                    .putString(PrefsKeys.USER_EMAIL, email)
                    .apply()
            }
        }

        setContent {
            RentCamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartScreen(
                        userName = userNameState.value,
                        savedEmail = userEmailState.value,
                        onLoginClick = { email ->
                            Log.d("StartActivity", "click: login ($email)")
                            val intent = Intent(this, ContentActivity::class.java)
                            intent.putExtra("USER_EMAIL", email)
                            startActivity(intent)
                        },
                        onRegisterClick = {
                            Log.d("StartActivity", "click: registration")
                            resultLauncher.launch(
                                Intent(this, RegistrationActivity::class.java)
                            )
                        }
                    )
                }
            }
        }
    }

    // Логирование жизненного цикла
    override fun onStart() {
        super.onStart()
        Log.d("StartActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("StartActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("StartActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("StartActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("StartActivity", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("StartActivity", "onRestart")
    }
}


@Preview(showSystemUi = true, name = "Light")
@Composable
fun StartScreenPreviewLight() {
    RentCamTheme(darkTheme = false) {
        StartScreen()
    }
}

@Preview(showSystemUi = true, name = "Dark")
@Composable
fun StartScreenPreviewDark() {
    RentCamTheme(darkTheme = true) {
        StartScreen()
    }
}

@Composable
fun StartScreen(
    userName: String = "",
    savedEmail: String = "",
    onLoginClick: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
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