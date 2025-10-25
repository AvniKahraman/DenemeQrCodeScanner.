package com.avnikahraman.denemeqrcodescanner

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.jvm.java

class AddAlarmActivity : AppCompatActivity() {

    private lateinit var etDoseCount: EditText
    private lateinit var timesContainer: LinearLayout
    private lateinit var btnAddTime: Button
    private lateinit var btnSaveAlarm: Button

    private val timeList = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        etDoseCount = findViewById(R.id.etDoseCount)
        timesContainer = findViewById(R.id.timesContainer)
        btnAddTime = findViewById(R.id.btnAddTime)
        btnSaveAlarm = findViewById(R.id.btnSaveAlarm)

        createNotificationChannel()

        btnAddTime.setOnClickListener {
            showTimePicker()
        }

        btnSaveAlarm.setOnClickListener {
            saveAlarmToFirestore()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeList.add(formattedTime)

            val textView = TextView(this)
            textView.text = formattedTime
            timesContainer.addView(textView)
        }, hour, minute, true).show()
    }

    private fun saveAlarmToFirestore() {
        val doseCount = etDoseCount.text.toString().toIntOrNull() ?: 0
        val medName = intent.getStringExtra("medName") ?: "Bilinmeyen"

        if (userId == null) {
            Toast.makeText(this, "Kullanıcı girişi bulunamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        val medData = hashMapOf(
            "name" to medName,
            "dosePerDay" to doseCount,
            "times" to timeList,
            "status" to "active",
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .document(userId)
            .collection("myMeds")
            .add(medData)
            .addOnSuccessListener {
                scheduleAlarms(timeList, medName)
                Toast.makeText(this, "İlaç ve alarm başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scheduleAlarms(times: List<String>, medName: String) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        for (time in times) {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
            }

            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("medName", medName)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                hour * 100 + minute,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "med_channel",
                "İlaç Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "İlaç hatırlatma bildirimleri"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
