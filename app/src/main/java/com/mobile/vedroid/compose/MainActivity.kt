package com.mobile.vedroid.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mobile.vedroid.compose.ui.compose.PrefsKeys
import com.mobile.vedroid.compose.ui.theme.RentCamTheme


class MainActivity : ComponentActivity() {
    private val userNameState = mutableStateOf("")
    private val userEmailState = mutableStateOf("")

    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "ON_CREATE")
        enableEdgeToEdge()

        prefs = getSharedPreferences(PrefsKeys.PREFS_NAME, MODE_PRIVATE)
        userNameState.value = prefs.getString(PrefsKeys.USER_NAME, "") ?: ""
        userEmailState.value = prefs.getString(PrefsKeys.USER_EMAIL, "") ?: ""

        setContent {
            RentCamTheme {
                val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                val bottomPadding =
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                val navController = rememberNavController()
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomPadding, top = topPadding)
                ) {
                    NavHost(navController, startDestination = MainActivityRoutes.Start()) {
                        composable<MainActivityRoutes.Start> { backStackEntry ->
                            StartFragment(
                                userName = userNameState.value,
                                savedEmail = userEmailState.value,
                                onLoginClick = {
                                    navController.navigate(MainActivityRoutes.Content){
                                    // Удалить все экраны до Start (включительно)
                                    popUpTo<MainActivityRoutes.Start> { inclusive = true }
                                    // Не создавать дубликат Content, если он уже есть
                                    launchSingleTop = true}
                                               },
                                onRegisterClick = {
                                    navController.navigate(MainActivityRoutes.Registration) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable<MainActivityRoutes.Registration> {
                            RegistrationFragment(
                                onRegisterSuccess = { name, email ->
                                    prefs.edit()
                                        .putString(PrefsKeys.USER_NAME, name)
                                        .putString(PrefsKeys.USER_EMAIL, email)
                                        .apply()
                                    userNameState.value = name
                                    userEmailState.value = email
                                    //navController.navigate(MainActivityRoutes.Start(name, email))
                                    navController.popBackStack()
                                },
                                onBackClick = { navController.navigate(navController.popBackStack()) }
                            )
                        }
                        composable<MainActivityRoutes.Content> {
                            ContentFragment(
                                onSettingsClick = {
                                    navController.navigate(MainActivityRoutes.Settings){
                                        launchSingleTop = true }
                                    }

                            )
                        }
                        composable<MainActivityRoutes.Settings> {
                            SettingsFragment(
                                onBackClick = { navController.navigate(navController.popBackStack()) }
                            )
                        }
                    }
                }
            }
        }
    }
        override fun onStart()    { super.onStart();    Log.d("MainActivity", "ON_START") }
        override fun onResume()   { super.onResume();   Log.d("MainActivity", "ON_RESUME") }
        override fun onPause()    { super.onPause();    Log.d("MainActivity", "ON_PAUSE") }
        override fun onStop()     { super.onStop();     Log.d("MainActivity", "ON_STOP") }
        override fun onRestart()  { super.onRestart();  Log.d("MainActivity", "ON_RESTART") }
}

//route посмотреть