package com.avnikahraman.denemeqrcodescanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val medName = intent?.getStringExtra("MED_NAME") ?: "İlacınız"
        val notificationId = System.currentTimeMillis().toInt()

        // 🔔 Bildirim oluşturma
        val builder = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("İlaç Hatırlatıcı")
            .setContentText("$medName ilacını alma zamanı!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, builder.build())

        // 🔊 Alarm sesi çal
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, alarmUri)
            r.play()
        } catch (e: Exception) {
            Toast.makeText(context, "Alarm sesi çalarken hata: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
