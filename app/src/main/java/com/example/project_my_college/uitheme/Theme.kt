package com.example.project_my_college.uitheme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext




private val DarkColorScheme = darkColorScheme(
    primary = ButtonColor_Dark,
    onPrimary = Color_on_Button_Dark,
    error = Error_Dark,
    onError = Error_text_Dark,
//    secondary = PurpleGrey80,
    tertiary = Specific_Dark ,
    background = BackGroundDark,

)

private val LightColorScheme = lightColorScheme(
    primary = ButtonColor_Light,
    onPrimary = Color_on_Button_Light,
    error = Error_Light,
    onError = Error_text_Light,
//    secondary = PurpleGrey40,
    tertiary = Specific_Light ,
    background = BackGroundLight




)

@Composable
fun Project_my_collegeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,

        content = content
    )
}