package com.example.latihan1pert6

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner


@Composable
fun AirplaneModeTracker() {
    val context = LocalContext.current

    var isAirplaneModeOn by remember {
        mutableStateOf(isAirplaneModeEnabled(context))
    }


    DisposableEffect (androidx.lifecycle.compose.LocalLifecycleOwner.current) {

        val airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    isAirplaneModeOn = isAirplaneModeEnabled(context)
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)

        context.registerReceiver(airplaneModeReceiver, filter)

        onDispose {
            context.unregisterReceiver(airplaneModeReceiver)
        }
    }

    val statusText = if (isAirplaneModeOn) "Mode Pesawat: AKTIF ✈️" else "Mode Pesawat: NONAKTIF 🌎"

    androidx.compose.material3.Text(text = statusText)
}

private fun isAirplaneModeEnabled(context: Context?): Boolean {
    return Settings.Global.getInt(
        context?.contentResolver,
        Settings.Global.AIRPLANE_MODE_ON,
        0
    ) != 0
}