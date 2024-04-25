package com.example.pokeaprendo

import android.util.Log
import com.example.pokeaprendo.api.Pokemon
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslatorDataPokemon {

    private lateinit var options:TranslatorOptions
    private var translator: Translator? = null
    private var conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()
    private var targetLanguage:String = ""


    fun config(languageSource:String, languageTarget:String):Task<Void>{
        targetLanguage = languageTarget
        options = TranslatorOptions.Builder()
            .setSourceLanguage(languageSource)
            .setTargetLanguage(languageTarget)
            .build()
        translator = Translation.getClient(options)

        Log.i("RESPPPP", "Config Traductor")

        Log.i("RESPPPP","$targetLanguage")
        return validateModelTraduction()
    }

    fun isTranslatorConfigured() = translator !=null

    fun deleteTranslator(){
        translator = null
    }


    private fun validateModelTraduction():Task<Void>
    {
        return translator!!.downloadModelIfNeeded(conditions)
    }


    /*suspend fun validateTranslateCollection(collecSourceLanguage:MutableList<String>):MutableList<String>
    {
        var collecTargetLanguage = mutableListOf<String>()


        if(isTranslatorConfigured())
        {
            Log.i("RESPP","Traducir collection: $collecSourceLanguage")

            collecSourceLanguage.forEach {cad->
                var translatedText = translate(cad).await()
                collecTargetLanguage.add(translatedText)
            }
        }
        else{
            Log.i("RESPP","Coleccion no traducida: $collecSourceLanguage")

            collecTargetLanguage = collecSourceLanguage
        }

        return collecTargetLanguage
    }*/


    suspend fun validateTranslateCollection(collecSourceLanguage:MutableList<String>):MutableList<String>
    {
        var collecTargetLanguage = mutableListOf<String>()

        if(isTranslatorConfigured())
        {
            Log.i("RESPPT","Traducir collection: $collecSourceLanguage")
            collecTargetLanguage=translateCollection(collecSourceLanguage)
        }
        else{
            Log.i("RESPPT","Coleccion no traducida: $collecSourceLanguage")

            collecTargetLanguage = collecSourceLanguage
        }

        return collecTargetLanguage
    }

    private suspend fun translateCollection(collecSourceLanguage:MutableList<String>):MutableList<String>
    {
        var collecTargetLanguage = mutableListOf<String>()
        collecSourceLanguage.forEach {cad->
            var translatedText = translate(cad).await()
            collecTargetLanguage.add(translatedText)
        }

        return collecTargetLanguage
    }


    fun translate(sourceString:String): Task<String>
    {
        var stringToTranslate = sourceString
        if (targetLanguage =="es"){
            if(stringToTranslate =="fighting"){
                stringToTranslate  = "fight"
                Log.i("RESPPPP","$stringToTranslate")
            }
        }

        return translator!!.translate(stringToTranslate)
    }




    suspend fun validateTranslatePokemonObject(sourcePokemon:Pokemon):Pokemon
    {
        var targetPokemon:Pokemon

        if(isTranslatorConfigured())
        {
            targetPokemon = translatePokemonObject(sourcePokemon)
        }
        else
        {
            targetPokemon = sourcePokemon
        }

        return targetPokemon
    }

    private suspend fun translatePokemonObject(sourcePokemon:Pokemon):Pokemon {
        val habitat = translate(sourcePokemon.habitat.orEmpty()).await()
        val generation= translate(sourcePokemon.generation.orEmpty()).await()
        val types= translateCollection(sourcePokemon.types)



        return Pokemon(
            sourcePokemon.idPokemon,
            sourcePokemon.name,
            habitat,
            generation,
            types,
            sourcePokemon.evolutions,
            sourcePokemon.urlIconPokemon)
    }


}