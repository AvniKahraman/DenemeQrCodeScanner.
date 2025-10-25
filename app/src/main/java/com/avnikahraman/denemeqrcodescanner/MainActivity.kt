package com.avnikahraman.denemeqrcodescanner

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    private lateinit var scanQRBtn: Button
    private lateinit var scannedValueTV: TextView
    private lateinit var scanner: GmsBarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        scanQRBtn = findViewById(R.id.scanQR)
        scannedValueTV = findViewById(R.id.scannedValue)

        // QR tarayıcı ayarları
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        scanner = GmsBarcodeScanning.getClient(this, options)

        // QR butonu
        scanQRBtn.setOnClickListener {
            startScanning()
        }

        // İlaç listesi butonları
        val btnMyMeds = findViewById<Button>(R.id.btnMyMeds)
        btnMyMeds.setOnClickListener {
            startActivity(Intent(this, MyMedicationActivity::class.java))
        }

        // Çıkış
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
            sharedPref.edit().putBoolean("rememberMe", false).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun startScanning() {
        scanner.startScan()
            .addOnSuccessListener { result ->
                val scannedName = result.rawValue
                scannedValueTV.text = "Scanned Value: $scannedName"
                if (!scannedName.isNullOrEmpty()) {
                    askToAddMedication(scannedName)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun askToAddMedication(medicationName: String) {
        AlertDialog.Builder(this)
            .setTitle("İlaç Ekleme")
            .setMessage("“$medicationName” ilacını eklemek ister misiniz?")
            .setPositiveButton("Evet") { _: DialogInterface, _: Int ->
                // Kullanıcı “Evet” derse alarm ekranına yönlendir
                val intent = Intent(this, AddAlarmActivity::class.java)
                intent.putExtra("medName", medicationName)
                startActivity(intent)
            }
            .setNegativeButton("Hayır", null)
            .show()
    }
}
