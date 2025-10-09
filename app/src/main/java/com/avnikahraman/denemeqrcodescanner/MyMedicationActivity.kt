package com.avnikahraman.denemeqrcodescanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyMedicationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicationAdapter
    private val myMedsList = mutableListOf<Medication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_medication)

        recyclerView = findViewById(R.id.recyclerViewMyMeds)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MedicationAdapter(myMedsList) { medication ->
            val intent = Intent(this, MedicationDetailActivity::class.java)
            intent.putExtra("medicationId", medication.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fetchMyMedications()
    }

    private fun fetchMyMedications() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .collection("myMeds")
            .get()
            .addOnSuccessListener { result ->
                myMedsList.clear()
                for (doc in result) {
                    val med = doc.toObject(Medication::class.java)
                    med.id = doc.id
                    myMedsList.add(med)
                }
                adapter.notifyDataSetChanged()
            }
    }
}
