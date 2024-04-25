package com.example.pokeaprendo

import android.util.Log
import com.example.pokeaprendo.api.Pokemon

object PokemonList {
    private var pokemons: MutableList<Pokemon> = mutableListOf()
    private var pokemonsEnglish: MutableList<Pokemon> = mutableListOf()


    fun addEnglishPokemon(pokemon: Pokemon){
        pokemonsEnglish.add(pokemon)
    }

    fun deletePokemonsEnglish(){
        pokemonsEnglish.clear()
    }

    fun getEnglishPokemon(posPokemon:Int) = pokemonsEnglish[posPokemon]

    fun removeEnglishPokemon(pokemon: Pokemon){
        pokemonsEnglish.remove(pokemon)
    }

    fun addPokemon(pokemon: Pokemon)
    {
        Log.i("POKINFO", "ADD: $pokemon")

        pokemons.add(pokemon)
    }

    fun getPokemon(posPokemon:Int) = pokemons[posPokemon]

    fun getSize() = pokemons.size


    fun removePokemon(pokemon: Pokemon)
    {
        pokemons.remove(pokemon)
    }

    fun showPokemons()
    {
        Log.i("RESPPT","POKEMONS OF LIST")
        pokemons.forEach(){
            Log.i("RESPPT","$it")
        }
        Log.i("RESPPT"," ")

    }

    fun showEnglishPokemons(){
        Log.i("RESPPT","ENGLISH POKEMONS")
        pokemonsEnglish.forEach(){
            Log.i("RESPPT","$it")
        }
        Log.i("RESPPT"," ")

    }

    fun isNotEmpy() = pokemons.isNotEmpty()

    fun getLastAddedPokemon():Pokemon {

        Log.i("POKINFO", "$pokemons")


            var pokemonToViewInfo = pokemons.last()
            pokemons.remove(pokemonToViewInfo)



        return pokemonToViewInfo
    }
}