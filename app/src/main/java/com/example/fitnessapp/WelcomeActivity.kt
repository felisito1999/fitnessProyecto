package com.example.fitnessapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        button_welcomeNext.setOnClickListener {
            val actualWeight = editTextActualWeightValue.text.toString().toFloat()
            val losingTarget = editTextWeightTargetValue.text.toString().toFloat()
            uploadInitialInfo(actualWeight, losingTarget)
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser

        textViewGreetingsWelcome.text = resources.getString(R.string.welcome_greeting) + " " + currentUser?.displayName

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == addProgressRegistrationRequestCode && resultCode == Activity.RESULT_OK) {
//            data?.getStringExtra(ProgressRegistrationActivity.EXTRA_REPLY)?.let{
//                val
//            }
//        }
//    }

    private fun goToHome() {
        var homeIntent = Intent(this, ProgressActivity::class.java)
        startActivity(homeIntent)
        this.finish()
    }

    private fun uploadInitialInfo(actualWeight : Float, losingTarget : Float) {
        val initialData = hashMapOf(
            "uid" to auth.currentUser?.uid,
            "initialWeight" to actualWeight,
            "losingTarget" to losingTarget,
            "targetWeight" to actualWeight - losingTarget
        )

        val db = Firebase.firestore

        if (auth.currentUser != null) {
            db.collection("userTargets").document()
                .set(initialData)
                .addOnSuccessListener {
                    goToHome()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "An error has occurred, try again", Toast.LENGTH_SHORT).show()
                }
        }
    }
}