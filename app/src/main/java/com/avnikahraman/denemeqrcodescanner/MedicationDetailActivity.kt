package com.avnikahraman.denemeqrcodescanner

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MedicationDetailActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDosage: TextView
    private lateinit var tvSideEffects: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_detail)

        tvName = findViewById(R.id.tvName)
        tvDescription = findViewById(R.id.tvDescription)
        tvDosage = findViewById(R.id.tvDosage)
        tvSideEffects = findViewById(R.id.tvSideEffects)

        val medicationId = intent.getStringExtra("medicationId") ?: return
        fetchMedicationDetail(medicationId)
        supportActionBar?.show()

    }

    private fun fetchMedicationDetail(medicationId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("medications")
            .document(medicationId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tvName.text = document.getString("name")
                    tvDescription.text = document.getString("description")
                    tvDosage.text = document.getString("dosage")
                    tvSideEffects.text = document.getString("sideEffects")
                }
            }
    }
}