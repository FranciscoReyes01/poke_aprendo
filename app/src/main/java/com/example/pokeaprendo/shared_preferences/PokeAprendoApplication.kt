package com.example.pokeaprendo.shared_preferences

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import io.grpc.Context

class PokeAprendoApplication:Application()
{
    companion object{
        lateinit var pref:Pref
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("RESPP","PokeAprendoApplication")

        pref = Pref(this)

    }




}