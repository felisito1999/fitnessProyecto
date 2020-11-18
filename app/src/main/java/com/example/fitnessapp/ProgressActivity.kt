package com.example.fitnessapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_progress.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ProgressActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var records : List<ProgressRecord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        fabAddProgress.setOnClickListener {
            goToProgressRegistration()
        }

        buttonSignOut.setOnClickListener {
            auth.signOut()
            checkIfUserIsLoggedIn(auth.currentUser)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewTrackingItems)
        val adapter = ProgressRecordAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        var data = listOf<ProgressRecord>()
        if(checkIfUserIsLoggedIn(currentUser)) {
            val db = Firebase.firestore
            val userProgressRecordsRef = db.collection("userProgressRecords")
            val getUserProgressRecordsQuery = userProgressRecordsRef.whereEqualTo("uid", currentUser?.uid)
            getUserProgressRecordsQuery.get()
                .addOnSuccessListener { documentSnapshot ->

                    data = documentSnapshot.toObjects<ProgressRecord>()
                    records = data
                    adapter.setProgressRecords(records)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "An error has occurred", Toast.LENGTH_SHORT).show()
                }
        }


//        getMainData(currentUser)
//        getTrackingData(currentUser)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        checkIfUserIsLoggedIn(currentUser)

        fabAddProgress.setOnClickListener {
            goToProgressRegistration()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewTrackingItems)
        val adapter = ProgressRecordAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        var data = listOf<ProgressRecord>()
        if(checkIfUserIsLoggedIn(currentUser)) {
            val db = Firebase.firestore
            val userProgressRecordsRef = db.collection("userProgressRecords")
            val getUserProgressRecordsQuery = userProgressRecordsRef.whereEqualTo("uid", currentUser?.uid)
            getUserProgressRecordsQuery.get()
                .addOnSuccessListener { documentSnapshot ->

                    data = documentSnapshot.toObjects<ProgressRecord>()
                    records = data
                    adapter.setProgressRecords(records)

                    val db = Firebase.firestore
                    val userTargetRef = db.collection("userTargets")
                    val getTargetQuery = userTargetRef.whereEqualTo("uid", currentUser?.uid)
                    getTargetQuery.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val data = documentSnapshot.first().toObject<UserTarget>()
                            textViewOriginalValue.text =
                                data.initialWeight.toString() + getString(R.string.poundUnitEnding)
                            textViewObjectiveValue.text =
                                (data.initialWeight - data.losingTarget).toString() + getString(R.string.poundUnitEnding)

                            var lastWeight: Float

                            var lastMonthRecords : List<ProgressRecord>
                            var lastMonthTracking : Float = 0f

                            var lastWeekRecords : List<ProgressRecord>
                            var lastWeekTracking : Float = 0f

                            var totalRecords : List<ProgressRecord>
                            var totalTracking : Float = 0f

                            if (records.isNotEmpty()) {
                                totalRecords = records

                                lastWeight = records.filter{ record -> record.lastValue }.first().weight

                                var calendar = Calendar.getInstance().time

                                var format = SimpleDateFormat("dd-MM-yyyy")

                                lastMonthRecords = records.filter{ record -> LocalDate.parse(record.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")) >= LocalDate.now().minusMonths(1) &&
                                        LocalDate.parse(record.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")) <= LocalDate.now()
//                                        &&
//                                        LocalDate.parse(record.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).year == LocalDate.now().year
                                }
                                lastWeekRecords = records.filter { record ->
                                    LocalDate.parse(
                                        record.date,
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy")) >= LocalDate.now().minusWeeks(1) &&
                                            LocalDate.parse(record.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")) <= LocalDate.now()
                                }
//
                                lastMonthRecords.forEach{ record ->
                                    lastMonthTracking += record.difference
                                }

                                lastWeekRecords.forEach{ record ->
                                    lastWeekTracking += record.difference
                                }

                                totalRecords.forEach{ record ->
                                    totalTracking += record.difference
                                }

                                textViewLastMonthValue.text = lastMonthTracking.toString()
                                textViewLastWeekValue.text = lastWeekTracking.toString()
                                textViewTotalValue.text = totalTracking.toString()

                                //TODO:Change images depending on the tracking value
                                if (lastMonthTracking >= 0 ){
                                    imageViewLastMonth.setImageResource(R.drawable.ic_arrow_upward_24px)
                                }
                                else {
                                    imageViewLastMonth.setImageResource(R.drawable.ic_arrow_downward_24px)
                                }

                                if (lastWeekTracking >= 0){
                                    imageViewLastWeek.setImageResource(R.drawable.ic_arrow_upward_24px)
                                }
                                else {
                                    imageViewLastWeek.setImageResource(R.drawable.ic_arrow_downward_24px)
                                }

                                if (totalTracking >= 0){
                                    imageViewProgressTotal.setImageResource(R.drawable.ic_arrow_upward_24px)
                                }
                                else {
                                    imageViewProgressTotal.setImageResource((R.drawable.ic_arrow_downward_24px))
                                }


//                                lastWeekRecords = records.filter{ record -> DateFormat.getDateInstance(DateFormat.WEEK_OF_YEAR_FIELD).parse(record.date).toString().toInt() == calendar.get(Calendar.WEEK_OF_YEAR) &&
//                                        DateFormat.getDateInstance(DateFormat.YEAR_FIELD).parse(record.date).toString().toInt() == calendar.get(Calendar.YEAR)}
//
//                                totalRecords = records


                            } else {
                                lastWeight = 0f
                            }

                            textViewRemainingValue.text =
                                (lastWeight - data.targetWeight).toString() + getString(R.string.poundUnitEnding)
                        }


                }
                .addOnFailureListener {
                    Toast.makeText(this, "An error has occurred", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun getMainData(currentUser : FirebaseUser?) {
        if (checkIfUserIsLoggedIn(currentUser)) {
            val db = Firebase.firestore
            val userTargetRef = db.collection("userTargets")
            val getTargetQuery = userTargetRef.whereEqualTo("uid", currentUser?.uid)
            getTargetQuery.get()
                .addOnSuccessListener {documentSnapshot ->
                    val data = documentSnapshot.first().toObject<UserTarget>()

                    textViewOriginalValue.text = data.initialWeight.toString() + getString(R.string.poundUnitEnding)
                    textViewObjectiveValue.text = (data.initialWeight - data.losingTarget).toString() + getString(R.string.poundUnitEnding)

                    //TODO: The result should be the remaining of the subtraction from the initial weight and the sum of fluctuations from the client's weight

                    var lastWeight : Float

                    if(records.isNotEmpty()){
                        lastWeight = records.first { record -> record.lastValue }.weight
                    }
                    else {
                        lastWeight = 0f
                    }


                    textViewRemainingValue.text = (data.initialWeight - lastWeight).toString() + getString(R.string.poundUnitEnding)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Could not retrieve data from the server, try again", Toast.LENGTH_LONG).show()
                }
        }
        else {
            redirectToLogin()
        }
    }

    //TODO: Add recyclerView and implement the list of progresses
    private fun getTrackingData(currentUser : FirebaseUser?) {
        var data = listOf<ProgressRecord>()
        if(checkIfUserIsLoggedIn(currentUser)) {
            val db = Firebase.firestore
            val userProgressRecordsRef = db.collection("userProgressRecords")
            val getUserProgressRecordsQuery = userProgressRecordsRef.whereEqualTo("uid", currentUser?.uid)
            getUserProgressRecordsQuery.get()
                .addOnSuccessListener { documentSnapshot ->

                    data = documentSnapshot.toObjects<ProgressRecord>()
                    records = data
                }
                .addOnFailureListener {
                    Toast.makeText(this, "An error has occurred", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun redirectToLogin() {
        Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        this.finish()
    }

    private fun checkIfUserIsLoggedIn(currentUser: FirebaseUser?) : Boolean{
        return if (currentUser == null) {
            redirectToLogin()

            false
        } else {
            true
        }
    }

    private fun goToProgressRegistration(){
        val progressRegistration = Intent(this, ProgressRegistrationActivity::class.java)
        startActivity(progressRegistration)
    }
}