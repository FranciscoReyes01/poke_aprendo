package com.example.pokeaprendo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import com.example.pokeaprendo.api.ListaDeObjetos
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication

import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        /*
            Para implementar el icono central que muestra en spash screen se definio un tema
            en el archivo "themes"

            Para insertar una imagen que se vea correcta y no cortada la imagen debe caber en un circulo
            de 2/3 del tamaño de la imagen
        * */

        var splash = installSplashScreen()


        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_splash)

        Log.i("RESPP","SPALSH")

        splash.setKeepOnScreenCondition { true }

        setLanguage()

        var intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setLanguage()
    {
        var langSavedSharedP = PokeAprendoApplication.pref.getLanguageCode()!!
        var systemDefaultLang = resources.configuration.locale.language

        if(langSavedSharedP != systemDefaultLang)
        {
            PokeAprendoApplication.pref.setLocalLanguage(langSavedSharedP ,this.baseContext)

        }

    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        Log.i("RESPPT","NO HACER NADA")

    }

}