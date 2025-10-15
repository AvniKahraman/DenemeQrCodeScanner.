package com.avnikahraman.denemeqrcodescanner

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class MedicationDetailActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDosage: TextView
    private lateinit var tvSideEffects: TextView
    private lateinit var imgMedication: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_detail)

        // View bağlantıları
        tvName = findViewById(R.id.tvName)
        tvDescription = findViewById(R.id.tvDescription)
        tvDosage = findViewById(R.id.tvDosage)
        tvSideEffects = findViewById(R.id.tvSideEffects)
        imgMedication = findViewById(R.id.imgMedication)

        // Intent'ten ilacın ID'sini al
        val medicationId = intent.getStringExtra("medicationId") ?: return

        // Firestore'dan verileri çek
        fetchMedicationDetail(medicationId)

        // ActionBar göster (istersen kaldırabilirsin)
        supportActionBar?.show()
    }

    private fun fetchMedicationDetail(medicationId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("medications")
            .document(medicationId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val dosage = document.getString("dosage")
                    val sideEffects = document.getString("sideEffects")
                    val imageUrl = document.getString("imageUrl")

                    tvName.text = name
                    tvDescription.text = description
                    tvDosage.text = dosage
                    tvSideEffects.text = sideEffects

                    // Görseli Glide ile yükle
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.logo) // Yüklenirken gösterilecek
                            .error(R.drawable.hata) // Hata olursa gösterilecek
                            .into(imgMedication)
                    }
                }
            }
    }
}
