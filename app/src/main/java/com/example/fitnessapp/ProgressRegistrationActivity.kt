package com.example.fitnessapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_progress_registration.*
import java.sql.Time
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.days

class ProgressRegistrationActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private val addProgressRegistrationRequestCode = 1
    private lateinit var selectedDate : String
    private var oldWeight : Float = 0f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_registration)

        auth = Firebase.auth

        val calendar = findViewById<CalendarView>(R.id.calendarViewRecord)
        val weightTextView = findViewById<EditText>(R.id.editTextNumberWeightRegistration)

        val calendarDate = Calendar.getInstance()


//        val date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.C).format(calendarDate.time)

        val date = LocalDate.now()

        selectedDate = "${date.dayOfMonth}-${date.monthValue}-${date.year}"

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = String.format("%02d",dayOfMonth) + "-" + String.format("%02d",month + 1) + "-" + year
        }

        buttonAddRecord.setOnClickListener {
            addProgress(selectedDate, weightTextView.text.toString().toFloat(), oldWeight)
        }
    }

    private fun addProgress(date : String, newWeight : Float, oldWeight : Float){

        val currentUser = auth.currentUser


        val db = Firebase.firestore

        val userTargetRef = db.collection("userProgressRecords")
        val getLastItemQuery = userTargetRef.whereEqualTo("uid", currentUser?.uid.toString()).whereEqualTo("lastValue", true)

        getLastItemQuery.get()
            .addOnSuccessListener { documentSnapshot ->
                val size = documentSnapshot.size()

                if(size > 0) {
                    val original = documentSnapshot.documents[0]
                    this.oldWeight = documentSnapshot.first().get("weight").toString().toFloat()
                    original.reference.update("lastValue", false)

                    val progressRecord = hashMapOf(
                        "uid" to  currentUser?.uid,
                        "date" to date,
                        "difference" to newWeight - this.oldWeight,
                        "weight" to newWeight,
                        "lastValue" to true
                    )

                    userTargetRef.document()
                        .set(progressRecord)
                        .addOnSuccessListener {
                            goToHome()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save record, try again", Toast.LENGTH_SHORT).show()
                        }

                }
                else {
                    val userProgressRecordRef = db.collection("userTargets")
                        val getUserInitialQuery = userProgressRecordRef.whereEqualTo("uid", currentUser?.uid.toString())
                        getUserInitialQuery.get()
                            .addOnSuccessListener { initialDocumentSnapshot ->
                            this.oldWeight = initialDocumentSnapshot.first().get("initialWeight").toString().toFloat()
                                Toast.makeText(this, initialDocumentSnapshot.first().get("initialWeight").toString(), Toast.LENGTH_SHORT).show()

                                val progressRecord = hashMapOf(
                                    "uid" to  currentUser?.uid,
                                    "date" to date,
                                    "difference" to newWeight - this.oldWeight,
                                    "weight" to newWeight,
                                    "lastValue" to true
                                )

                                userTargetRef.document()
                                    .set(progressRecord)
                                    .addOnSuccessListener {
                                        goToHome()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to save record, try again", Toast.LENGTH_SHORT).show()
                                    }

                            }
                        .addOnFailureListener{
                            Toast.makeText(this, "An error occurred, please try again later", Toast.LENGTH_SHORT).show()
                        }
                }


            }

    }

    private fun checkIfUserIsLoggedIn(currentUser : FirebaseUser?) : Boolean {
        return if (currentUser == null){
            goToLogin()
            false
        } else {
            true
        }
    }

    private fun goToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        this.finish()
    }

    private fun goToHome() {
        val homeIntent = Intent(this, ProgressActivity::class.java)
        startActivity(homeIntent)
        this.finish()
    }

//    companion object {
//        const val EXTRA_REPLY = "com.example.fitnessapp.REPLY"
//    }
}