package com.example.pokeaprendo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.pokeaprendo.databinding.ActivityNewuserBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class NewUserActivity : AppCompatActivity() {
    lateinit var binding:ActivityNewuserBinding
    private lateinit var auth: FirebaseAuth



    private fun matchedPassword() =binding.etPassword.text.toString() == binding.etSecondPassword.text.toString()

    private fun etAlreadyHaveData():Boolean {

        Log.i("RESSPY","${binding.etUser.text.toString()}")
        Log.i("RESSPY","${binding.etPassword.text.toString()}")
        Log.i("RESSPY","${binding.etSecondPassword.text.toString()}")

        return binding.etUser.text.toString().isNotBlank() && binding.etPassword.text.toString()
            .isNotBlank() && binding.etSecondPassword.text.toString().isNotBlank()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewuserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = Firebase.auth

       binding.btCreateUser.setOnClickListener()
        {
            if(etAlreadyHaveData()){
               if(matchedPassword())
               {
                   auth.createUserWithEmailAndPassword(binding.etUser.text.toString(), binding.etPassword.text.toString())
                       .addOnCompleteListener(this) { task ->
                           if (task.isSuccessful) {
                               // Sign in success, update UI with the signed-in user's information
                               Log.i("CREATE", "createUserWithEmail:success")

                               var intent = Intent(this,MainActivity::class.java)
                               UserFirebase.setAuthReferenec(auth)
                               Toast.makeText(
                                   baseContext,
                                   getString(R.string.created_user),
                                   Toast.LENGTH_SHORT,
                               ).show()

                               startActivity(intent)


                               Log.i("RESPP","$task")
                           } else {
                               // If sign in fails, display a message to the user.
                               //Log.i("CREATEEX", "createUserWithEmail:failure", task.exception)
                               var message ="${task.exception?.message?:getString(R.string.no_created_user)}"
                               Toast.makeText(
                                   baseContext,
                                   message,
                                   Toast.LENGTH_SHORT,
                               ).show()
                               //Log.i("CREATEEX","MENSAJE ${}")
                           }
                       }
               }
               else{
                   Toast.makeText(
                       baseContext,
                       "${resources.getString(R.string.no_matched)}",
                       Toast.LENGTH_SHORT,
                   ).show()

                   Log.i("RESSPY","${binding.etPassword.text.length}")
                   Log.i("RESSPY","${binding.etSecondPassword.text.length}")


               }

           }
            else{
               Toast.makeText(
                   baseContext,
                   "${resources.getString(R.string.incomplete_data)}",
                   Toast.LENGTH_SHORT,
               ).show()

           }
        }
    }


    fun startHomeAct(view: View)
    {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        Log.i("RESPPT","NO HACER NADA")

    }
}