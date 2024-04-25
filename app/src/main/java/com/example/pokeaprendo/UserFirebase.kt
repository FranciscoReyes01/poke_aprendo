package com.example.pokeaprendo

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

object UserFirebase {

    private lateinit var auth: FirebaseAuth

    fun setAuthReferenec(auth: FirebaseAuth){
        this.auth = auth
    }

    fun getUid()= auth.uid

    fun signOut(context: Context){
        Toast.makeText(context,"${context.resources.getString(R.string.closed_session)}",Toast.LENGTH_SHORT).show()

        auth.signOut()
    }
}