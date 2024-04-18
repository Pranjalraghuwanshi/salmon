package com.example.salmon

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class Bhai : AppCompatActivity() {

    private lateinit var batterylevelseekbar: SeekBar
    private lateinit var selectedbatteryleveltextview: TextView
    private lateinit var submitButooon: Button
    private var desiredbatterylevel: Int = 0
    private lateinit var batterylevelreceiver: BroadcastReceiver // Declare at class level

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bhai)
        batterylevelseekbar = findViewById(R.id.seekbar)
        selectedbatteryleveltextview = findViewById(R.id.txt)
        submitButooon = findViewById(R.id.btn)

        batterylevelseekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedbatteryleveltextview.text = "Selected Battery Level:$progress%"
                desiredbatterylevel = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        submitButooon.setOnClickListener {

        }
        batterylevelreceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val status: Int = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging: Boolean =
                        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

                    if (isCharging) {
                        val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        val batteryPct: Float = level.toFloat() / scale.toFloat() * 100

                        if (batteryPct >= desiredbatterylevel) {
                            showNotification(context)
                        }
                    }
                }
            }
        }
        registerReceiver(
            batterylevelreceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }



    @RequiresApi(Build.VERSION_CODES.ECLAIR_0_1)
    private fun showNotification(context: Context?) {
        val channelID = "batterynotification"
        val notificatioID = 1
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "Battery Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = android.app.Notification.Builder(context, channelID)
            .setContentTitle("charging complete")
            .setContentText("battery charged up tp $desiredbatterylevel%").build()
        notificationManager.notify(notificatioID, notification)
    }
}
