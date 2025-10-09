package com.avnikahraman.denemeqrcodescanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MedicationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicationAdapter
    private val medicationList = mutableListOf<Medication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MedicationListActivity)
        }
        setContentView(recyclerView)

        adapter = MedicationAdapter(medicationList) { medication ->
            val intent = Intent(this, MedicationDetailActivity::class.java)
            intent.putExtra("medicationId", medication.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        supportActionBar?.show()

        fetchMedications()
    }

    private fun fetchMedications() {
        val db = FirebaseFirestore.getInstance()
        db.collection("medications")
            .get()
            .addOnSuccessListener { result ->
                medicationList.clear()
                Log.d("Firestore", "GELEN DOKÜMAN SAYISI: ${result.size()}")
                for (document in result) {
                    Log.d("Firestore", "doc id: ${document.id}, data: ${document.data}")
                    val med = document.toObject(Medication::class.java)
                    med.id = document.id // ID'yi manuel set et!
                    medicationList.add(med)
                }
                Log.d("Firestore", "Liste: $medicationList")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Firestore HATASI: ${exception.message}")
            }
    }
}
