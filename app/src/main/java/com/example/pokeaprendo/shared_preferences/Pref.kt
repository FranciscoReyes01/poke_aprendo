package com.example.pokeaprendo.shared_preferences

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.example.pokeaprendo.MainActivity
import com.example.pokeaprendo.R
import java.util.Locale

class Pref(val context:Context) {

    val SHARED_NAME="POKE_APRENDO"
    val MODE_CODE_KEY = "MODE"
    val LANG_CODE_KEY = "LANG_CODE"

    private val storage = context.getSharedPreferences(SHARED_NAME,0)
    private val editor = storage.edit()



    fun saveDarkModeCode(modeCode:Int) {
        editor.putInt(MODE_CODE_KEY,modeCode).apply()
    }

    fun getDarkModeCode() = storage.getInt(MODE_CODE_KEY, UiModeManager.MODE_NIGHT_NO)

    fun saveLanguageCode(langCode:String){
        editor.putString(LANG_CODE_KEY,langCode).apply()
    }

    fun getLanguageCode() = storage.getString(LANG_CODE_KEY,context.resources.configuration.locale.language)


    fun setLocalLanguage(langCode:String,context: Context)
    {
        Log.i("RESPPP","langCode: $langCode")


        saveLanguageCode(langCode)
        var locale = Locale(langCode)
        val config: Configuration =  this.context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config,this.context.resources.displayMetrics)



    }

}