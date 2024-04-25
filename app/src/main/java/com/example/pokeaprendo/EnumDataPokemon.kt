package com.example.pokeaprendo

import android.util.Log
import com.example.pokeaprendo.CapturarFragment.Companion.EVOLUTIONS
import com.example.pokeaprendo.CapturarFragment.Companion.GENERATION
import com.example.pokeaprendo.CapturarFragment.Companion.HABITAT
import com.example.pokeaprendo.CapturarFragment.Companion.NAME
import com.example.pokeaprendo.CapturarFragment.Companion.NAME_POKEMONS
import com.example.pokeaprendo.CapturarFragment.Companion.TYPES
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication

enum class EnumDataPokemon {
    SPANISH_DATA,
    ENGLISH_DATA,
    JAPANESE_DATA;


    private var mapOfData: MutableMap<String, MutableList<String>> = mutableMapOf(
        TYPES to mutableListOf<String>(),
        GENERATION to mutableListOf<String>(),
        HABITAT to mutableListOf<String>(),
        NAME_POKEMONS to mutableListOf<String>()
    )

    fun showMaps() {

        Log.i("RESPPT","MAP TYPES : ${mapOfData.get(TYPES)}")
        Log.i("RESPPT","MAP GENERATION : ${mapOfData.get(GENERATION)}")
        Log.i("RESPPT","MAP HABITAT: ${mapOfData.get(HABITAT)}")
        Log.i("RESPPT","MAP NAME: ${mapOfData.get(NAME)}")
        Log.i("RESPPT","MAP NAME_POKEMONS: ${mapOfData.get(NAME_POKEMONS)}")
    }

    fun getMap(key: String) = mapOfData.get(key)

    fun alreadyHaveGeneralData():Boolean
    {
        return !mapOfData[TYPES].isNullOrEmpty()
    }

    fun setMap(key: String, listOfData: MutableList<String>)
    {
        if(isKeyInTheMap(key))
        {
            mapOfData[key]!!.addAll(listOfData)
        }

    }

    fun putItemOnList(key:String,stringItem: String)
    {
        if(isKeyInTheMap(key) && stringItem.isNotBlank())
        {
            mapOfData[key]!!.add(stringItem)
        }
    }



    fun getItemString(key: String,pos:Int): String
    {
        var itemString = ""
        if(isKeyInTheMap(key))
        {
            itemString = mapOfData[key]!![pos]
        }
        else
        {
            if(key == NAME || key == EVOLUTIONS)
            {
               itemString = mapOfData[NAME_POKEMONS]!![pos]
            }
        }
        return itemString
    }

    fun getSizeOfList(key:String):Int {
        var sizeOfList = 0
        if(isKeyInTheMap(key))
        {
            sizeOfList= mapOfData.get(key)!!.size
        }
        else{
            if(key == NAME || key == EVOLUTIONS)
            {
                sizeOfList = mapOfData.get(NAME_POKEMONS)!!.size
            }
        }

        return sizeOfList
    }


    fun getTypes() = mapOfData[TYPES]!!


    private fun isKeyInTheMap(key:String) = mapOfData.contains(key)

    suspend fun translateDataOfMaps()
    {
        Log.i("RESPPT","MAPS TRADUCIDOS")
        mapOfData.forEach {
            if(it.key != NAME_POKEMONS){
                Log.i("RESPPT","${it.key}")

                val translatedCollection = TranslatorDataPokemon.validateTranslateCollection(it.value)
                mapOfData.set(it.key, translatedCollection)
            }

        }
    }


    companion object{
        lateinit var enumReference:EnumDataPokemon
    }
}