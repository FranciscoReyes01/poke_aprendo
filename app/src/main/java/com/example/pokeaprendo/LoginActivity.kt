package com.example.pokeaprendo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.pokeaprendo.databinding.ActivityLoginBinding
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.i("IDFIRE", "ID USER LOGIN: ${currentUser.uid}")
            UserFirebase.setAuthReferenec(auth)

            startMainAc()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        initListeners()
    }

    private fun etAlreadyHaveData() = binding.etUser.text.toString().isNotBlank() && binding.etPassword.text.isNotBlank()

    private fun initListeners() {
        binding.btIniciarSesion.setOnClickListener(){
            if(etAlreadyHaveData()){
                var user =  binding.etUser.text.toString().replace(" ","")
                var password= binding.etPassword.text.toString().replace(" ","")

                auth.signInWithEmailAndPassword(user, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("LOGIN", "signInWithEmail:success")
                            val user = auth.currentUser
                            UserFirebase.setAuthReferenec(auth)

                            Toast.makeText(
                                baseContext,
                                resources.getString(R.string.logged_in),
                                Toast.LENGTH_SHORT,
                            ).show()

                            Toast.makeText(baseContext,
                                "${resources.getString(R.string.welcome)}: ${binding.etUser.text}",
                                Toast.LENGTH_SHORT).show()

                            startMainAc()
                        } else {
                            // If sign in fails, display a message to the user.
                            var message ="${task.exception?.message?:getString(R.string.no_login_user)}"

                            Toast.makeText(
                                baseContext,
                                message,
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            }
            else{
                Toast.makeText(this,resources.getString(R.string.incomplete_data),Toast.LENGTH_LONG).show()
            }


        }
    }


    fun starCreateUserAc(view: View)
    {
        var intent = Intent(this,NewUserActivity::class.java)
        startActivity(intent)
    }

    fun startMainAc()
    {
        var intentToMain  = Intent(this,MainActivity::class.java)
        startActivity(Intent(intentToMain))
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        Log.i("RESPPT","NO HACER NADA")

    }

}