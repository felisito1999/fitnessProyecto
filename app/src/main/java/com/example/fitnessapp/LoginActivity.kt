package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener {
            checkLoginInfo(editTextUsername.text.toString(), editTextPassword.text.toString())
        }

        textView_linkToRegistration.setOnClickListener{
            val registrationIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(registrationIntent)
        }

        auth = Firebase.auth


//        checkUserAlreadyLoggedIn(auth.currentUser)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        checkUserAlreadyLoggedIn(currentUser)

    }

    private fun checkLoginInfo(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                //TODO: Realizar implementacion de las acciones luego de que el usuario se ha logeado de manera exitosa.
                if(task.isSuccessful) {
                    Toast.makeText(this, "Successful login", Toast.LENGTH_SHORT).show()
                    goHome()
                }
                else {
                    Toast.makeText(
                        this,
                        "Authentication failed, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    hideKeyboard(this)
                }

            }
    }

    private fun checkUserAlreadyLoggedIn(currentUser: FirebaseUser?){
        if(currentUser != null){
            val progressActivityIntent = Intent(this, ProgressActivity::class.java)
            startActivity(progressActivityIntent)
        }
    }

    private fun firstTimeWelcome(){
        var welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeActivityIntent)
    }

    private fun goHome(){
        val homeActivityIntent = Intent( this, ProgressActivity::class.java)
        startActivity(homeActivityIntent)
        this.finish()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}