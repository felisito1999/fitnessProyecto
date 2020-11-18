package com.example.fitnessapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        button_SignUp.setOnClickListener {
            val firstName = editTextTextFirstNameRegistration.text.toString()
            val lastName = editTextTextLastNameRegistration.text.toString()
            val email = editTextEmailAddressRegistration.text.toString()
            val password = editTextPasswordRegistration.text.toString()

            registerUser(firstName, lastName, email, password)

        }

        textView_linkToSignIn.setOnClickListener {
            val signInIntent = Intent(this, LoginActivity::class.java)
            startActivity(signInIntent)
        }


        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        checkUserAlreadyLoggedIn(currentUser)
    }

    private fun checkUserAlreadyLoggedIn(currentUser: FirebaseUser?){
        if(currentUser != null){

        }
        else{

        }
    }

    private fun registerUser(firstName : String, lastName : String, email : String, password : String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    val user = auth.currentUser

                    val profileUpdates = userProfileChangeRequest{
                        displayName = firstName
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->

                        if(task.isComplete){
                            var uid = user?.uid
                            var userDisplayName = user?.displayName

                            val registeredUser = hashMapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "email" to email
                            )

                            val db = Firebase.firestore
                            if (uid != null) {
                                db.collection("users").document(uid)
                                    .set(registeredUser)
                                    .addOnSuccessListener {
                                        firstTimeWelcome()
                                    }
                                    .addOnFailureListener{ e -> Log.w("Error", "Error writing document", e) }
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun firstTimeWelcome(){
        var welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeActivityIntent)
    }

    private fun isDataValid(firstName : String, lastName : String, emailAddress : String, password : String): Boolean {
        if (true){
            return true
        }
        else {
            return false
        }
    }
}